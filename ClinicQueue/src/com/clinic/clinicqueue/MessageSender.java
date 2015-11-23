package com.clinic.clinicqueue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.clinic.clinicqueue.message.Message;

public class MessageSender extends Thread {

	// private static final String MSG_SEND_TAG = "MSG";

	private SocketChannel mChannel;
	private CQConnection mParent = null;
	private PriorityBlockingQueue<Message> mMessageQueue;
	private ByteBuffer mSendBuffer;
	private String mClientId;
	volatile boolean mTerminated;

	// For blocking send - allow only 2 data transfer packets at one time in the
	// queue
	final Semaphore mSemaphore = new Semaphore(2, true);

	public static final int BUFFER_SIZE = 65535;

	public MessageSender(CQConnection parent) {
		super("msg sender");

		mChannel = null;
		mParent = parent;
		mMessageQueue = new PriorityBlockingQueue<Message>(20);
		mTerminated = false;
		mSendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		mClientId = "";

	}

	public void setClientId(String clientid) {
		mClientId = clientid;
	}

	public void setChannel(SocketChannel channel) {
		if (!this.isAlive()) {
			mChannel = channel;
		}

	}

	@Override
	public void run() {

		mTerminated = false;
		while (!mTerminated) {
			Message msg = null;

			try {
				msg = mMessageQueue.take();
				if (msg != null) {

					mSendBuffer.clear();
					msg.writeBuffer(mSendBuffer);
					mSendBuffer.flip();
					mChannel.write(mSendBuffer);
					mSendBuffer.limit(BUFFER_SIZE);

					if (msg.isRestricted()) {
						mSemaphore.release();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	public void send(Message message) {
		mMessageQueue.offer(message);
	}

	public boolean trySendBlocking(Message message, long timeout) {
		try {
			boolean done = mSemaphore.tryAcquire(timeout, TimeUnit.SECONDS);
			if (done && !mTerminated) {
				message.setRestricted(true);
				mMessageQueue.offer(message);
			} else if (!done) {
			}
			return done;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void sendBlocking(Message message) {
		try {
			mSemaphore.acquire();
			if (!mTerminated) {
				message.setRestricted(true);
				mMessageQueue.offer(message);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void terminate() {
		mTerminated = true;
		mMessageQueue.clear();
		this.interrupt();
	}

	public boolean isTerminated() {
		return mTerminated;
	}

}
