package com.clinic.clinicqueue.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clinic.clinicqueue.CQApplication;
import com.clinic.clinicqueue.Connection;
import com.clinic.clinicqueue.Events;
import com.clinic.clinicqueue.R;
import com.clinic.clinicqueue.Setting;

import de.greenrobot.event.EventBus;

public class SettingActivity extends Activity {
	
	private EditText addr = null;
	private EditText port = null;
	private EditText user = null;
	private EditText title = null;
	private EditText bottom = null;
	private EditText notify = null;
	private AlertDialog processDlg = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        addr =  (EditText)this.findViewById(R.id.addrEditText);
        addr.setText(Setting.getServerAddr());
        
        port =  (EditText)this.findViewById(R.id.portEditText);
        port.setText(String.valueOf(Setting.getServerPort()));
        
        user =  (EditText)this.findViewById(R.id.userEditText);
        user.setText(Setting.getUser());
        
        title =  (EditText)this.findViewById(R.id.titleEditText);
        title.setText(Setting.getTitle());
        
        bottom =  (EditText)this.findViewById(R.id.bottomEditText);
        bottom.setText(Setting.getBottom());
        
        notify =  (EditText)this.findViewById(R.id.notifyEditText);
        notify.setText(Setting.getNotify());
        
        EventBus.getDefault().register(this);
        
        ((Button)this.findViewById(R.id.okbtn)).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Editable serverAddr = addr.getText();
				Setting.setServerAddr(serverAddr.toString());
				Editable serverPort = port.getText();
				Setting.setServerPort(Integer.parseInt(serverPort.toString()));
				Setting.setUser(user.getText().toString());
				Setting.setTitle(title.getText().toString());
				Setting.setBottom(bottom.getText().toString());
				Setting.setNotify(notify.getText().toString());
//				showControlClient();
//				Intent i = new Intent(SettingActivity.this, 
//						LoadingActivity.class);
//				startActivity(i);
				CQApplication.getConnection().connect();
			}});
    }
    
    public void onEventMainThread(Events.OnStatusEvent event) {
    	switch(event.status)
    	{
    	case Connection.STATUS_CONNECTED:
    		if (null != processDlg && processDlg.isShowing())
    			processDlg.hide();
    		Intent i = new Intent(SettingActivity.this, 
					MainActivity.class);
			startActivity(i);
    		break;
    	case Connection.STATUS_INVALID:
    		if (null != processDlg && processDlg.isShowing())
    			processDlg.hide();
    		displayToast("连接断开！");
    		break;
    	case Connection.STATUS_CONNECTFAILED:
    		if (null != processDlg && processDlg.isShowing())
    			processDlg.hide();
    		displayToast("连接失败！");
    		break;
    	default:
    		break;
    	}
	}
    
    private void showControlClient() {
    	LayoutInflater inflater = getLayoutInflater();
		final View view = inflater.inflate(R.layout.loading, null);
    	processDlg = new AlertDialog.Builder(this)
    			.setView(view)
				.setTitle("登入")
				.setMessage("登入中")
				.setNegativeButton("登入",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								
							}
						}).create();
    	processDlg.setCancelable(false);
    	processDlg.show();
	}
    
    @Override
    public void onDestroy()
    {
    	EventBus.getDefault().unregister(this);
    	super.onDestroy();
    	
    }
    
    private void displayToast(String s)
    {
    	Toast.makeText(SettingActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}
