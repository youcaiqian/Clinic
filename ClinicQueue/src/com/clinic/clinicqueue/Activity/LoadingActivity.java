package com.clinic.clinicqueue.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.clinic.clinicqueue.Connection;
import com.clinic.clinicqueue.Events;
import com.clinic.clinicqueue.R;

import de.greenrobot.event.EventBus;

public class LoadingActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.loading);
		EventBus.getDefault().register(this);
    }
	
	public void onEventMainThread(Events.OnStatusEvent event) {
    	this.finish();
	}
	
	@Override
    public void onDestroy()
    {
    	EventBus.getDefault().unregister(this);
    	super.onDestroy();
    	
    }
}