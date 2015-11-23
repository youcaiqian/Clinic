package com.clinic.clinicqueue;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.clinic.clinicqueue.message.LoginMessage;
import com.clinic.clinicqueue.message.TextMessage;
import com.clinic.clinicqueue.message.TransferAckMessage;

import android.content.Context;
import android.util.Log;

public class CQConnection {
	private ExecutorService m_taskExecutor;
	private SocketChannel mClientChannel;
	private WeakReference<IConnectionCallback> mCallbacks;
	private Context mContext;
	private ICQClient mCQClient = null;
	private MessageSender mMessageSender;
	private MessageReceiver mMessageReceiver;
	private int mReceiveCookie;

	public boolean mbDisconnecting = false;

	/**
	 * Timeout for a connection request.
	 */
	// private static final int CONNECTION_TIMEOUT = 10000;

	/**
	 * Lifetime of a transfer request before it is cancelled.
	 */
	// private static final int TRANSFER_REQ_TTL = 10000;

	public enum ConnectResult {
		CONNECTED, FAILED, NOT_AUTH
	}

	public CQConnection(ICQClient client) {
		mCQClient = client;
		m_taskExecutor = Executors.newCachedThreadPool();
		mClientChannel = null;
		mMessageSender = new MessageSender(this);
		mMessageReceiver = new MessageReceiver(this);
		mCallbacks = new WeakReference<IConnectionCallback>(null);
		mReceiveCookie = -1;
	}

	public final ExecutorService getExecutorService() {
		return m_taskExecutor;
	}

	public final boolean IsConnected() {
		return (mClientChannel != null && !mbDisconnecting);
	}

	public ConnectResult Connect(ServerConfig server,IConnectionCallback cb, MessageReceived recvCb, Context context) {
		try {
			InetSocketAddress remoteAddr = new InetSocketAddress(
					server.getHost(), server.getPort());
			mClientChannel = StartConnection("client id",
					remoteAddr, recvCb, context);
		} catch (IOException e) {
			Log.e("ABClient", "Could connect to server", e);
			return ConnectResult.FAILED;
		} catch (Exception e) {
			e.printStackTrace();
			return ConnectResult.FAILED;
		}

		// Store connection related information
		mCallbacks = new WeakReference<IConnectionCallback>(cb);

		return ConnectResult.CONNECTED;
	}

	public void Disconnect() {
		if (mbDisconnecting)
			return;
		mbDisconnecting = true;
		m_taskExecutor.submit(new Runnable() {

			@Override
			public void run() {
				ShutdownConnection();
				// Send disconnect notification
				IConnectionCallback cb = mCallbacks.get();
				if (cb != null) {
					cb.onDisconnect(0);
					mCallbacks.clear();
				}

				// Reset all stored state information
				mMessageSender = null;
				mMessageReceiver = null;
				mClientChannel = null;
				mCallbacks = new WeakReference<IConnectionCallback>(null);
				mbDisconnecting = false;
			}
		});
	}

	private SocketChannel StartConnection(String clientid,
			InetSocketAddress addr, MessageReceived cb, Context context)
			throws IOException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(true);
		channel.socket().setTcpNoDelay(true);
		//channel.socket().setKeepAlive(true);

		if (channel.connect(addr)) {
			// Start write thread
			if (null == mMessageSender)
				mMessageSender = new MessageSender(this);
			mMessageSender.setClientId(clientid);
			mMessageSender.setChannel(channel);
			mMessageSender.start();

			// Start read thread
			if (null == mMessageReceiver)
				mMessageReceiver = new MessageReceiver(this);
			mMessageReceiver.setServerId(0);
			mMessageReceiver.setChannel(channel);
			mReceiveCookie = mMessageReceiver
					.registerNotify(new WeakReference<MessageReceived>(cb));
			mMessageReceiver.start();

			mContext = context;
		}

		return channel;
	}

	private void ShutdownConnection() {
		mMessageSender.terminate();
		try {
			mMessageSender.join();
		} catch (InterruptedException e1) {
		}

		if (mReceiveCookie != -1) {
			mMessageReceiver.unregisterNotify(mReceiveCookie);
			mReceiveCookie = -1;
		}
		mMessageReceiver.terminate();
		try {
			mMessageReceiver.join();
		} catch (InterruptedException e1) {
		}

		try {
			mClientChannel.close();
		} catch (IOException e) {
			Log.e("ABClient", "Could not close socket", e);
		}
	}

	public void SendText(int id, String text) {
		TextMessage msg = new TextMessage(id, text);
		if (!mbDisconnecting && null != mMessageSender)
			mMessageSender.send(msg);
	}
	
	
	public void Login(String user) {
		LoginMessage msg = new LoginMessage(user);
		if (!mbDisconnecting && null != mMessageSender)
			mMessageSender.send(msg);
	}
	
	public void SendTransferAck(boolean ack, int cookie, long offset) {
		TransferAckMessage msg = new TransferAckMessage(ack, cookie, offset);
		if (!mbDisconnecting && null != mMessageSender)
			mMessageSender.send(msg);
	}
}
