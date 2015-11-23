package com.clinic.clinicqueue;

import android.content.IntentFilter;

/**
 * Main AirBorne client interface.
 * 
 * @author Frank Schmid
 * 
 *         Use @see ABClientFactory to create an instance.
 */
public interface ICQClient {

	/**
	 * Intent for server notification. Will be broadcast locally as soon as a
	 * new server appears and discovery has been enabled.
	 */
	public static final String NOTIFY_ON_SERVER = "com.clinic.clinicqueue.notify.ON_SERVER";

	/**
	 * Intent for server loss notification. Will be broadcast locally as soon as
	 * a server disappears.
	 */
	public static final String NOTIFY_ON_SERVER_LOST = "com.clinic.clinicqueue.notify.ON_SERVER_LOST";

	/**
	 * Intent filter to get only server related intents in a @see
	 * BroadcastReceiver.
	 */
	public static final IntentFilter SERVER_FILTER = new IntentFilter() {

		{
			addAction(ICQClient.NOTIFY_ON_SERVER);
			addAction(ICQClient.NOTIFY_ON_SERVER_LOST);
		}

	};

	/**
	 * Intent for connection notification.
	 */
	public static final String NOTIFY_ON_CONNECT = "com.clinic.clinicqueue.notify.ON_CONNECT";

	/**
	 * Intent for loss of connection.
	 */
	public static final String NOTIFY_ON_DISCONNECT = "com.clinic.clinicqueue.notify.ON_DISCONNECT";

	/**
	 * Intent for loss of connection.
	 */
	public static final String NOTIFY_ON_AUTH_NEEDED = "com.clinic.clinicqueue.notify.ON_AUTH_NEEDED";

	/**
	 * Intent for connection error.
	 */
	public static final String NOTIFY_ON_CONNECT_ERROR = "com.clinic.clinicqueue.notify.ON_CONNECT_ERROR";

	/**
	 * Intent for heart beat timeout.
	 */
	public static final String NOTIFY_ON_HEARTBEAT_TIMEOUT = "com.clinic.clinicqueue.notify.ON_HEARTBEAT_TIMEOUT";
	
	/**
	 * Intent filter to get only server related intents in a @see
	 * BroadcastReceiver.
	 */
	public static final IntentFilter CONNECTION_FILTER = new IntentFilter() {

		{
			addAction(ICQClient.NOTIFY_ON_CONNECT);
			addAction(ICQClient.NOTIFY_ON_DISCONNECT);
			addAction(ICQClient.NOTIFY_ON_CONNECT_ERROR);
			addAction(ICQClient.NOTIFY_ON_AUTH_NEEDED);
			addAction(ICQClient.NOTIFY_ON_HEARTBEAT_TIMEOUT);
		}

	};

	public static final String NOTIFY_ON_TRANSFER_START = "com.clinic.clinicqueue.notify.ON_TRANSFER_START";
	public static final String NOTIFY_ON_TRANSFER_PROGRESS = "com.clinic.clinicqueue.notify.ON_TRANSFER_PROGRESS";
	public static final String NOTIFY_ON_TRANSFER_FINISHED = "com.clinic.clinicqueue.notify.ON_TRANSFER_FINISHED";
	public static final String NOTIFY_ON_TRANSFER_CANCELLED = "com.clinic.clinicqueue.notify.ON_TRANSFER_CANCELLED";

	public static final IntentFilter TRANSFER_FILTER = new IntentFilter() {
		{
			addAction(ICQClient.NOTIFY_ON_TRANSFER_START);
			addAction(ICQClient.NOTIFY_ON_TRANSFER_CANCELLED);
			addAction(ICQClient.NOTIFY_ON_TRANSFER_FINISHED);
			addAction(ICQClient.NOTIFY_ON_TRANSFER_PROGRESS);
		}
	};

	public static final String NOTIFY_ON_MSG_TEXT = "com.clinic.clinicqueue.notify.ON_MSG_TEXT";
	public static final String NOTIFY_ON_MSG_TRANS_REQ = "com.clinic.clinicqueue.notify.ON_MSG_TRANS_REQ";
	public static final String NOTIFY_ON_MSG_TRANS_DATA = "com.clinic.clinicqueue.notify.ON_MSG_TRANS_DATA";
	public static final String NOTIFY_ON_MSG_PROGRESS = "com.clinic.clinicqueue.notify.ON_MSG_PROGRESS";
	public static final String NOTIFY_ON_MSG_PATIENT_DATA = "com.clinic.clinicqueue.notify.ON_MSG_PATIENT_DATA";
	public static final String NOTIFY_ON_MSG_DEPT_WAIT = "com.clinic.clinicqueue.notify.ON_MSG_DEPT_WAIT";
	public static final String NOTIFY_ON_MSG_DISPLAY_INFO = "com.clinic.clinicqueue.notify.ON_MSG_DISPLAY_INFO";

	public static final IntentFilter MESSAGE_FILTER = new IntentFilter() {
		{
			addAction(ICQClient.NOTIFY_ON_MSG_TEXT);
			addAction(ICQClient.NOTIFY_ON_MSG_PROGRESS);
			addAction(ICQClient.NOTIFY_ON_MSG_PATIENT_DATA);
			addAction(ICQClient.NOTIFY_ON_MSG_DEPT_WAIT);
			addAction(ICQClient.NOTIFY_ON_MSG_TRANS_REQ);
			addAction(ICQClient.NOTIFY_ON_MSG_TRANS_DATA);
			addAction(ICQClient.NOTIFY_ON_MSG_DISPLAY_INFO);
		}
	};

	/**
	 * Connect to a given application instance.
	 * 
	 * @param id
	 *            Server id retrieved with @see IABClientCallbacks.OnServer()
	 * @param cb
	 *            Callback interface instance.
	 * @throws ABClientException
	 */
	public abstract void Connect() throws CQClientException;

	/**
	 * Disconnect from server. Callback given on connect will be used for
	 * notification.
	 */
	public abstract void Disconnect() throws CQClientException;

	/**
	 * Check if the client is currently connected.
	 */
	public abstract boolean IsConnected();

	/**
	 * Send a text to the application. The client has to be connected.
	 * 
	 * @param id
	 *            Category or id for message dispatching.
	 * @param text
	 *            Text to send (will be sent as UTF-8)
	 * @throws ABClientException
	 *             if the client is currently not connected to any application.
	 */
	public abstract void SendText(int id, String text) throws CQClientException;
	
	/**
	 * Send a login msg to server notify server that user info
	 * 
	 * @param user
	 *            here is the dept code (will be sent as UTF-8)
	 * @throws ABClientException
	 *             if the client is currently not connected to any application.
	 */
	public abstract void Login(String user) throws CQClientException;
	
	/**
	 * Send a transfer ack to the application. The client has to be connected.
	 * 
	 * @param ack
	 *            If this transfer is accepted.
	 * @param cookie
	 *            Transfer cookie
     * @param offset
	 *            Transfer offset
	 * @throws ABClientException
	 *             if the client is currently not connected to any application.
	 */
	public abstract void SendTransferAck(boolean ack, int cookie, long offset) throws CQClientException;
}