package com.clinic.clinicqueue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class WifiReceiver extends BroadcastReceiver {
	public interface WifiStateListener {
		void onStateChanged(State state);
	}

	private NetworkInfo mCurrentNetworkInfo;
	private IntentFilter mFilter;
	private WifiStateListener mListener;

	public WifiReceiver(WifiStateListener listener) {
		mListener = listener;
	}

	public void register(Context context) {
		if (mFilter == null) {
			mFilter = new IntentFilter();
			mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		}

		context.registerReceiver(this, mFilter);
	}

	public void unregister(Context context) {
		context.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context content, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			mCurrentNetworkInfo = intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (mCurrentNetworkInfo != null
					&& mCurrentNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				State state = mCurrentNetworkInfo.getState();
				if (mListener != null)
					mListener.onStateChanged(state);
			}
		}
	}
}
