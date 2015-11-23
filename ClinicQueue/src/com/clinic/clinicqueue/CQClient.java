package com.clinic.clinicqueue;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.clinic.clinicqueue.message.DeptWaitMessage;
import com.clinic.clinicqueue.message.DisplayInfoMessage;
import com.clinic.clinicqueue.message.Message;
import com.clinic.clinicqueue.message.MessageType;
import com.clinic.clinicqueue.message.PatientDataMessage;
import com.clinic.clinicqueue.message.TextMessage;
import com.clinic.clinicqueue.message.TransferDataMessage;
import com.clinic.clinicqueue.message.TransferRequestMessage;

/**
 * Implementation of IABClient interface, For obfuscation to be effective most
 * of the methods will be delegated to the connection object.
 */
final class CQClient implements ICQClient, IConnectionCallback, MessageReceived {

	private Lock m_knownServersMutex;
	private ServerConfig m_server;
	private CQConnection m_connection;
	private LocalBroadcastManager mBroadcastManager;
	private Context mContext;

	/**
	 * AirBorne client. Allows control of a server object.
	 * 
	 * @param appId
	 *            Server application id.
	 */
	public CQClient(Context applicationContext)
			throws CQClientException {
		mContext = applicationContext;
		mBroadcastManager = LocalBroadcastManager.getInstance(mContext);

		m_connection = new CQConnection(this);
		m_knownServersMutex = new ReentrantLock();
	}
	
	public void SetServerInfo(String host, int port)
	{
		m_server = new ServerConfig(host, port);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nero.airborne.client.IABClient#Connect(int, java.lang.String,
	 * com.nero.airborne.client.IABConnectionCallbacks)
	 */
	@Override
	public void Connect() throws CQClientException {
		if (m_connection.IsConnected()) {
			throw new CQClientException(
					CQClientException.Type.ALREADY_CONNECTED);
		}
		
		final ServerConfig selectedServer = m_server;
		final CQClient _this = this;
		
		m_connection.getExecutorService().submit(new Runnable() {

			@Override
			public void run() {
				CQConnection.ConnectResult connectResult = m_connection.Connect(selectedServer,_this, _this,
						mContext);
				switch (connectResult) {
				case CONNECTED:
					NotifyServerId(ICQClient.NOTIFY_ON_CONNECT, 0);
					break;
				case FAILED:
					NotifyServerId(ICQClient.NOTIFY_ON_CONNECT_ERROR, 0);
					break;
				default:
					break;
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nero.airborne.client.IABClient#Disconnect()
	 */
	@Override
	public void Disconnect() throws CQClientException {
		if (!m_connection.IsConnected()) {
			throw new CQClientException(CQClientException.Type.NOT_CONNECTED);
		}

		m_connection.Disconnect();
	}

	@Override
	public void SendText(int id, String text) throws CQClientException {
		if (!m_connection.IsConnected()) {
			throw new CQClientException(CQClientException.Type.NOT_CONNECTED);
		}

		m_connection.SendText(id, text);
	}
	
	@Override
	public void Login(String user) throws CQClientException {
		if (!m_connection.IsConnected()) {
			throw new CQClientException(CQClientException.Type.NOT_CONNECTED);
		}

		m_connection.Login(user);
	}
	
	@Override
	public void SendTransferAck(boolean ack, int cookie, long offset) throws CQClientException {
		if (!m_connection.IsConnected()) {
			throw new CQClientException(CQClientException.Type.NOT_CONNECTED);
		}

		m_connection.SendTransferAck(ack, cookie, offset);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nero.airborne.client.IABClient#IsConnected()
	 */
	@Override
	public final boolean IsConnected() {
		return m_connection.IsConnected();
	}

	/**
	 * Send a standard notification to the user (app).
	 * 
	 * @param action
	 *            Intent to send.
	 * @param serverId
	 *            Associated server id.
	 */
	private void NotifyServerId(String action, int serverId) {
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra("id", serverId);
		mBroadcastManager.sendBroadcast(intent);
	}

	@Override
	public void onDisconnect(int id) {
		NotifyServerId(ICQClient.NOTIFY_ON_DISCONNECT, id);
	}

	
	@Override
	public void onMessageReceived(int serverid, MessageType type,
			Message message) {
		Intent intent = null;

		switch (type) {
		case TEXT:
			intent = new Intent();
			TextMessage msg = (TextMessage) message;
			intent.setAction(NOTIFY_ON_MSG_TEXT);
			intent.putExtra("id", msg.getId());
			intent.putExtra("text", msg.getText());
			break;
		case TRANSFER_REQ:
			intent = new Intent();
			TransferRequestMessage reqmsg = (TransferRequestMessage) message;
			intent.setAction(NOTIFY_ON_MSG_TRANS_REQ);
			intent.putExtra("id", reqmsg.getId());
			intent.putExtra("name", reqmsg.getName());
			intent.putExtra("mimetype", reqmsg.getInfo());
			intent.putExtra("size", reqmsg.getSize());
			break;
		case TRANSFER_DATA:
			intent = new Intent();
			TransferDataMessage datamsg = (TransferDataMessage) message;
			intent.setAction(NOTIFY_ON_MSG_TRANS_DATA);
			intent.putExtra("id", datamsg.getId());
			intent.putExtra("offset", datamsg.getOffset());
			intent.putExtra("size", datamsg.getSize());
			intent.putExtra("data", datamsg.getData());
			break;
		case PATIENT_DATA:
			intent = new Intent();
			PatientDataMessage pdatamsg = (PatientDataMessage) message;
			intent.setAction(NOTIFY_ON_MSG_PATIENT_DATA);
			intent.putExtra("type", pdatamsg.mDataType);
			intent.putExtra("data", pdatamsg.mPatients);
			break;
		case DEPT_WAIT:
			intent = new Intent();
			DeptWaitMessage dwaitmsg = (DeptWaitMessage) message;
			intent.setAction(NOTIFY_ON_MSG_DEPT_WAIT);
			intent.putExtra("type", dwaitmsg.mDataType);
			intent.putExtra("data", dwaitmsg.mDepts);
			break;
		case DISPLAY_INFO:
			intent = new Intent();
			DisplayInfoMessage disInfomsg = (DisplayInfoMessage) message;
			intent.setAction(NOTIFY_ON_MSG_DISPLAY_INFO);
			intent.putExtra("type", disInfomsg.mDataType);
			intent.putExtra("info", disInfomsg.mInfo);
			break;
		default:
			break;
		}

		if (intent != null) {
			intent.putExtra("server", serverid);
			mBroadcastManager.sendBroadcast(intent);
		}
	}
}
