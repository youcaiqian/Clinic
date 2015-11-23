package com.clinic.clinicqueue;

import java.lang.ref.WeakReference;

import com.clinic.clinicqueue.ICQClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public final class CQConnectReceiver extends BroadcastReceiver {

	private WeakReference<ICQConnectNotify> mNotify;
	
	public CQConnectReceiver(ICQConnectNotify notify) {
		if (notify == null)
			throw new NullPointerException();
		
		mNotify = new WeakReference<ICQConnectNotify>(notify);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {

		ICQConnectNotify notify = mNotify.get();
		if (notify == null)
			return;
		
		final String action = intent.getAction();
		if (action == ICQClient.NOTIFY_ON_CONNECT) {
			int id = intent.getIntExtra("id", -1);
			if (id != -1) {
				notify.onConnect(id);
			}
		}
		else if (action == ICQClient.NOTIFY_ON_DISCONNECT) {
			int id = intent.getIntExtra("id", -1);
			if (id != -1) {
				notify.onDisconnect(id);
			}
		}
		else if (action == ICQClient.NOTIFY_ON_CONNECT_ERROR) {
			notify.onConnectError(intent.getIntExtra("id", -1));
		}
	}

	public void register(Context context)  {
		LocalBroadcastManager.getInstance(context).registerReceiver(this, ICQClient.CONNECTION_FILTER);
	}
	
	public void unregister(Context context) {
		LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
	}
}
