package com.clinic.clinicqueue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.clinic.clinicqueue.message.Dept;
import com.clinic.clinicqueue.message.Patient;

public final class CQMessageReceiver extends BroadcastReceiver {

	private WeakReference<ICQMessageNotify> mNotify;
	
	public CQMessageReceiver(ICQMessageNotify notify) {
		if (notify == null)
			throw new NullPointerException();
		
		mNotify = new WeakReference<ICQMessageNotify>(notify);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ICQMessageNotify notify = mNotify.get();
		if (notify == null)
			return;
		
		final String action = intent.getAction();
		final int serverid = intent.getIntExtra("server", -1);
		if (serverid == -1)
			return;
		
		if (action == ICQClient.NOTIFY_ON_MSG_TEXT) {
			final int id = intent.getIntExtra("id", -1);
			final String text = intent.getStringExtra("text");
			
			notify.onTextMessage(serverid, id, text);
		}
		else if (action == ICQClient.NOTIFY_ON_MSG_DISPLAY_INFO) {
			final byte dataType = intent.getByteExtra("type", (byte)0);
			final String info = intent.getStringExtra("info");
			
			notify.onDisplayInfoMessage(dataType, info);
		}
		else if (action == ICQClient.NOTIFY_ON_MSG_DEPT_WAIT) {
			final byte dataType = intent.getByteExtra("type", (byte)0);
			final ArrayList<Dept> depts = (ArrayList<Dept>)(intent.getExtras().get("data"));
			
			notify.onDeptWaitMessage(dataType, depts);
		}
		else if (action == ICQClient.NOTIFY_ON_MSG_PATIENT_DATA) {
			final byte dataType = intent.getByteExtra("type", (byte)0);
			final ArrayList<Patient> patients = (ArrayList<Patient>)(intent.getExtras().get("data"));
			
			notify.onPatientDataMessage(dataType, patients);
		}
		else if (action == ICQClient.NOTIFY_ON_MSG_PROGRESS) {
			final int id = intent.getIntExtra("id", -1);
			final long min = intent.getLongExtra("min", 0);
			final long max = intent.getLongExtra("max", 0);
			final long progress = intent.getLongExtra("progress", 0);
			
			notify.onProgressMessage(serverid, id, min, max, progress);
		}
		else if (action == ICQClient.NOTIFY_ON_MSG_TRANS_REQ) {
			final int transid = intent.getIntExtra("id", -1);
			final String name = intent.getStringExtra("name");
			final String mimeType = intent.getStringExtra("mimetype");
			final long size = intent.getLongExtra("size", 0);
			
			notify.onTransferRequest(serverid, transid, name, mimeType, size);
		}
		else if (action == ICQClient.NOTIFY_ON_MSG_TRANS_DATA) {
			final int transid = intent.getIntExtra("id", -1);
			final long offset = intent.getLongExtra("offset", 0);
			final int size = intent.getIntExtra("size", 0);
			final byte[] data = intent.getByteArrayExtra("data");
			
			notify.onTransferData(serverid, transid, offset, size, data);
		}
	}

	public void register(Context context)  {
		LocalBroadcastManager.getInstance(context).registerReceiver(this, ICQClient.MESSAGE_FILTER);
	}
	
	public void unregister(Context context) {
		LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
	}
	
}
