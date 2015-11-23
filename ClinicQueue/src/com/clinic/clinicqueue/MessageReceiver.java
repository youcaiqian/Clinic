package com.clinic.clinicqueue;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Pair;

import com.clinic.clinicqueue.message.Message;
import com.clinic.clinicqueue.message.MessageHeader;
import com.clinic.clinicqueue.message.MessageType;

public class MessageReceiver extends Thread {

	private static final int BUFFER_SIZE = 65535;

	private int mServerId;
	private CQConnection mParent = null;
	private SocketChannel mChannel;
	private ByteBuffer mReceiveBuffer;
	private ByteBuffer mRemainBuffer;
	private Map<MessageType, List<Pair<Integer, WeakReference<MessageReceived>>>> mNotificators;
	private static AtomicInteger CurrentCookie = new AtomicInteger();
	final Lock mLock = new ReentrantLock();
	final Condition mStateChanged = mLock.newCondition();
	volatile boolean mTerminated;

	public MessageReceiver(CQConnection parent) {
		super("msg receiver");

		mParent = parent;
		mChannel = null;
		mTerminated = false;
		mReceiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		mRemainBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);// Message header size
														// equal to 52, it is
														// enough space at
														// present
		mServerId = -1;
		mNotificators = new EnumMap<MessageType, List<Pair<Integer, WeakReference<MessageReceived>>>>(
				MessageType.class);
		mNotificators
				.put(MessageType.TRANSFER_ACK,
						Collections
								.synchronizedList(new LinkedList<Pair<Integer, WeakReference<MessageReceived>>>()));
		mNotificators
				.put(MessageType.TEXT,
						Collections
								.synchronizedList(new LinkedList<Pair<Integer, WeakReference<MessageReceived>>>()));
		mNotificators
				.put(MessageType.TRANSFER_REQ,
						Collections
								.synchronizedList(new LinkedList<Pair<Integer, WeakReference<MessageReceived>>>()));
		mNotificators
				.put(MessageType.TRANSFER_DATA,
						Collections
								.synchronizedList(new LinkedList<Pair<Integer, WeakReference<MessageReceived>>>()));
		mNotificators
				.put(MessageType.DISPLAY_INFO,
						Collections
								.synchronizedList(new LinkedList<Pair<Integer, WeakReference<MessageReceived>>>()));
		
		mNotificators
				.put(MessageType.DEPT_WAIT,
						Collections
								.synchronizedList(new LinkedList<Pair<Integer, WeakReference<MessageReceived>>>()));
		mNotificators
				.put(MessageType.PATIENT_DATA,
						Collections
								.synchronizedList(new LinkedList<Pair<Integer, WeakReference<MessageReceived>>>()));
	}

	public void setServerId(int serverid) {
		mServerId = serverid;
	}

	public void setChannel(SocketChannel channel) {
		if (!this.isAlive()) {
			mChannel = channel;
		}
	}

	public int registerNotify(MessageType type, MessageReceived callback) {
		int cookie = CurrentCookie.incrementAndGet();
		mNotificators.get(type).add(
				new Pair<Integer, WeakReference<MessageReceived>>(cookie,
						new WeakReference<MessageReceived>(callback)));
		return cookie;
	}

	public int registerNotify(MessageType type,
			WeakReference<MessageReceived> callback) {
		int cookie = CurrentCookie.incrementAndGet();
		mNotificators.get(type).add(
				new Pair<Integer, WeakReference<MessageReceived>>(cookie,
						callback));
		return cookie;
	}

	public int registerNotify(WeakReference<MessageReceived> callback) {
		int cookie = CurrentCookie.incrementAndGet();
		Iterator<List<Pair<Integer, WeakReference<MessageReceived>>>> notificators = mNotificators
				.values().iterator();
		while (notificators.hasNext()) {
			List<Pair<Integer, WeakReference<MessageReceived>>> typeNotificator = notificators
					.next();
			typeNotificator
					.add(new Pair<Integer, WeakReference<MessageReceived>>(
							cookie, callback));
		}
		return cookie;
	}

	public void unregisterNotify(MessageType type, int cookie) {
		Iterator<Pair<Integer, WeakReference<MessageReceived>>> notificator = mNotificators
				.get(type).iterator();
		while (notificator.hasNext()) {
			Pair<Integer, WeakReference<MessageReceived>> notify = notificator
					.next();
			if (notify.first == cookie) {
				notificator.remove();
				break;
			}
		}
	}

	public void unregisterNotify(int cookie) {
		Iterator<List<Pair<Integer, WeakReference<MessageReceived>>>> notificators = mNotificators
				.values().iterator();
		while (notificators.hasNext()) {
			Iterator<Pair<Integer, WeakReference<MessageReceived>>> notificator = notificators
					.next().iterator();
			while (notificator.hasNext()) {
				Pair<Integer, WeakReference<MessageReceived>> notify = notificator
						.next();
				if (notify.first == cookie) {
					notificator.remove();
					break;
				}
			}
		}
	}

	@Override
	public void run() {

		mRemainBuffer.clear();
		mRemainBuffer.flip();
		mTerminated = false;

		try {

			while (!mTerminated) {
				mReceiveBuffer.clear();

				// copy the last incomplete packets
				if (mRemainBuffer.remaining() > 0) {
					mReceiveBuffer.put(mRemainBuffer);
					mRemainBuffer.clear();
					mRemainBuffer.flip();
				}

				int bytesRead = mChannel.read(mReceiveBuffer);
				if (bytesRead < 0) {
					throw new IOException(
							"channel reach end of stream or some error occours!!!");
				}
				
				if (bytesRead > 0)
				{
					mReceiveBuffer.flip();
					while (mReceiveBuffer.remaining() >= MessageHeader.HeaderSize()) {
						mReceiveBuffer.mark();
						Message msg = Message.readBuffer(mReceiveBuffer);
						if (msg != null) {
							MessageType type = msg.getType();
	
							// Notify registered clients
							Iterator<Pair<Integer, WeakReference<MessageReceived>>> notificator = mNotificators
									.get(type).iterator();
							while (notificator.hasNext()) {
								WeakReference<MessageReceived> callbackRef = notificator
										.next().second;
								MessageReceived callback = callbackRef.get();
								if (callback != null) {
									callback.onMessageReceived(mServerId, type, msg);
								}
							}
						} else {
							mReceiveBuffer.reset();
							break;
						}
					}
	
					// still has incomplete packet left?
					if (mReceiveBuffer.remaining() > 0) {
						mRemainBuffer.clear();
						mRemainBuffer.put(mReceiveBuffer);
						mRemainBuffer.flip();
					}
				}
			}
		} catch (AsynchronousCloseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// Something weird happened
			e.printStackTrace();
			mParent.Disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void terminate() {
		mTerminated = true;
		this.interrupt();
	}

	public boolean isTerminated() {
		return mTerminated;
	}

}
