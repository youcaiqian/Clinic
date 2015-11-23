package com.clinic.clinicqueue.Activity;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.clinic.clinicqueue.CQApplication;
import com.clinic.clinicqueue.Connection;
import com.clinic.clinicqueue.Events;
import com.clinic.clinicqueue.ICQGifCompleteNotify;
import com.clinic.clinicqueue.Setting;
import com.clinic.clinicqueue.message.Patient;
import com.clinic.clinicqueue.ui.AutoResizeTextView;
import com.clinic.clinicqueue.ui.CustomTextView;
import com.clinic.clinicqueue.ui.DisplayParams;
import com.clinic.clinicqueue.ui.DisplayUtil;
import com.clinic.clinicqueue.ui.ImageViewEx;
import com.clinic.clinicqueue.R;

import de.greenrobot.event.EventBus;

@SuppressLint({ "NewApi", "DefaultLocale" }) 
public class MainActivity extends Activity implements ICQGifCompleteNotify{
	private TextView titleText = null;
	private CustomTextView bottomText = null;
	private CustomTextView notifyText = null;
	private VideoView videoView = null;
	private ImageViewEx imgView = null;
	private LinearLayout.LayoutParams videoViewParams = null;
	private LinearLayout.LayoutParams fulParam = null;
	private ArrayList<CustomTextView> mPatients = null;
	private ArrayList<CustomTextView> mWaits = null;
	private int deptIndex = 0;
	final private String HIDE_CHAR = "★";
	private boolean switchWait = false;
	private int repeatCount = 1;
	private int currentCount = 0;
	private String currentPath = "";
	private boolean playing = false;
	
	@SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case 0:
    			int count = CQApplication.getConnection().GetDeptWaitSize();
    			if(count > 4)
    			{
    				deptIndex += 4;
    				if(deptIndex > count)
    					deptIndex = 0;

    				UpdateDeptWait();
    				if(switchWait) sendEmptyMessageDelayed(0, 4000);
    			}
    			else
    			{
    				UpdateDeptWait();
    			}
    		break;
    		default:
    			break;
    		}
    		super.handleMessage(msg);
    	}
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        DisplayParams displayParams = DisplayParams.getInstance(this);
        
        AutoResizeTextView autoSize = new AutoResizeTextView(this);
        autoSize.setTextSize(200);
        autoSize.setText("测试");
        autoSize.adjustTextSize(displayParams.screenWidth, displayParams.screenHeight/20);
        float textSize = autoSize.getTextSize();
        int fontSize = DisplayUtil.px2sp(textSize, displayParams.scale);
        int smallFontSize = fontSize*2/3;
        
        videoView = (VideoView) findViewById(R.id.videoView);
        videoViewParams = (LinearLayout.LayoutParams)videoView.getLayoutParams();
        fulParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        fulParam.gravity = Gravity.CENTER;
        videoView.setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer p) {
				currentCount++;
				if(currentCount >= repeatCount && repeatCount != 0)
				{
					p.reset();
					videoView.setVisibility(View.GONE);
					File tmpFile = new File(currentPath);
					if(tmpFile.exists())
					{
						tmpFile.delete();
					}
					currentPath = "";
					playing = false;
				}
				else
				{
					p.start();
				}
			}
        	
        });
        
        videoView.setOnErrorListener(new OnErrorListener(){

			@Override
			public boolean onError(MediaPlayer p, int arg1, int arg2) {
				p.reset();
				videoView.setVisibility(View.GONE);
				return true;
			}
        	
        });
        
        videoView.setVisibility(View.GONE);
        
        imgView = (ImageViewEx)findViewById(R.id.imageViewEx);
        imgView.setVisibility(View.GONE);
        imgView.SetCompleteNotify(this);
        
        LinearLayout titleLay = (LinearLayout) findViewById(R.id.titleView);  
        titleText = new TextView(this);  
        titleText.setText(Setting.getTitle());  
        titleText.setTextSize(fontSize);
        titleText.setGravity(Gravity.CENTER);
        titleText.setTextColor(Color.YELLOW);  
        titleText.setBackgroundColor(Color.BLACK);  
        titleLay.addView(titleText, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);  
    
        LinearLayout bottomLay = (LinearLayout) findViewById(R.id.bottomView); 
        bottomText = new CustomTextView(this);  
        bottomText.setText(Setting.getBottom());  
        bottomText.setTextSize(fontSize);
        bottomText.setTextAlign(CustomTextView.TEXT_ALIGN_LEFT | CustomTextView.TEXT_ALIGN_CENTER_VERTICAL);
        bottomText.setTextColor(Color.RED);  
        bottomText.setBackgroundColor(Color.BLACK);
        bottomLay.addView(bottomText, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);  
        bottomText.setScroll(0);
        
        LinearLayout.LayoutParams param = 
        		new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,1.0f);
        
        LinearLayout patientLay = (LinearLayout) findViewById(R.id.patientView);
        mPatients = new ArrayList<CustomTextView>();
        for(int i = 0; i < 10; i++)
        {
        	 CustomTextView patientText = new CustomTextView(this);  
        	 patientText.setText("");
        	 patientText.setTextSize(fontSize);  
        	 patientText.setTextAlign(CustomTextView.TEXT_ALIGN_CENTER_HORIZONTAL | CustomTextView.TEXT_ALIGN_CENTER_VERTICAL);  
        	 patientText.setTextColor(Color.YELLOW);  
        	 patientText.setBackgroundColor(Color.BLACK);
        	 mPatients.add(patientText);
        	 patientLay.addView(patientText, param);
        }
        
        LinearLayout waitLay = (LinearLayout) findViewById(R.id.waitView);
        mWaits = new ArrayList<CustomTextView>();
        for(int i = 0; i < 4; i++)
        {
        	 CustomTextView waitText = new CustomTextView(this);
        	 waitText.setText("");
        	 waitText.setTextSize(smallFontSize);  
        	 waitText.setTextAlign(CustomTextView.TEXT_ALIGN_CENTER_HORIZONTAL | CustomTextView.TEXT_ALIGN_CENTER_VERTICAL);  
        	 waitText.setTextColor(Color.GREEN);  
        	 waitText.setBackgroundColor(Color.BLACK);
        	 mWaits.add(waitText);
        	 waitLay.addView(waitText, param);
        }
        
        LinearLayout subTitleLay = (LinearLayout) findViewById(R.id.subTitleView);
        CustomTextView subTitleText = new CustomTextView(this);  
        subTitleText.setText("等待信息");  
        subTitleText.setTextSize(fontSize);  
        subTitleText.setTextAlign(CustomTextView.TEXT_ALIGN_CENTER_HORIZONTAL | CustomTextView.TEXT_ALIGN_CENTER_VERTICAL);  
        subTitleText.setTextColor(Color.YELLOW);  
        subTitleText.setBackgroundColor(Color.BLACK);
        subTitleLay.addView(subTitleText, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        
        LinearLayout notifyLay = (LinearLayout) findViewById(R.id.notifyView);
        notifyText = new CustomTextView(this);  
        notifyText.setText(Setting.getNotify());
        notifyText.setTextSize(smallFontSize);
        notifyText.setTextAlign(CustomTextView.TEXT_ALIGN_CENTER_HORIZONTAL | CustomTextView.TEXT_ALIGN_TOP);  
        notifyText.setTextColor(Color.RED);
        notifyText.setBackgroundColor(Color.BLACK);
        notifyText.setMultiLine(true);
        notifyText.setScroll(1);
        notifyLay.addView(notifyText, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);  
    
        EventBus.getDefault().register(this);
        
        UpdateDeptWait();
    }
    
    public void StopPlayback(String path){
    	imgView.stop();
    	videoView.stopPlayback();
    	imgView.setVisibility(View.GONE);
    	videoView.setVisibility(View.GONE);
    	currentCount = 0;
    	if(!currentPath.isEmpty() && !currentPath.equals(path) )
    	{
    		File tmpFile = new File(currentPath);
			if(tmpFile.exists())
			{
				tmpFile.delete();
			}
    	}
    	currentPath = path;
    	playing = false;
    }
    
    public void onEventMainThread(Events.OnMediaEvent event) {
    	boolean bGif = event.path.toLowerCase().endsWith(".gif");
    	repeatCount = event.repCount;
    	StopPlayback(event.path);
    	if(bGif){
    		imgView.setSourcePath(event.path);
    		imgView.setLayoutParams(event.fullScreen ? fulParam : videoViewParams);
    		imgView.setVisibility(View.VISIBLE);
    		playing = true;
    	}
    	else{
    		videoView.setVideoPath(event.path);
        	videoView.setLayoutParams(event.fullScreen ? fulParam : videoViewParams);
        	//videoView.getHolder().setSizeFromLayout();
        	videoView.setVisibility(View.VISIBLE);
        	videoView.start();
        	playing = true;
    	}
    	
    }
    
    public void onEventMainThread(Events.OnPatientDataEvent event) {
    	if(event.data != null){
    	    int patSize = event.data.size();
    		for(int i = 0; i < mPatients.size(); i++)
    		{
    			if(i < patSize)
        		{
    				Patient p = event.data.get(i);
        			String patientName = p.m_csPatientName;
        			String tmpPatientName = "";
        			if(patientName.length() > 0)
        			{
        				tmpPatientName = patientName.substring(0, 1)+HIDE_CHAR;
        			}
        			else
        			{
        				tmpPatientName = HIDE_CHAR+HIDE_CHAR;
        			}
        			if(patientName.length() > 2)
        			{
        				tmpPatientName += patientName.substring(2, 3);
        			}

        			mPatients.get(i).setPatient(tmpPatientName, p.m_csDeptName, p.m_nVisitNum+"号", p.m_csDeptLocation);
        		}
        		else
        		{
        			mPatients.get(i).setText("");
        		}

        		if(i == 0)
        		{
        			mPatients.get(i).doShine();
        		}
    		}
    	}
    }
    
    public void onEventMainThread(Events.OnDisplayInfoEvent event) {
    	if(event.type==3){
    		Setting.setNotify(event.info);
    		notifyText.setText(Setting.getNotify());
    	}
    }
    
    public void onEventMainThread(Events.OnDeptWaitEvent event) {
    	switchWait = false;
    	if (mHandler.hasMessages(0))  
            mHandler.removeMessages(0);
    	UpdateDeptWait();
    	switchWait = true;
		mHandler.sendEmptyMessageDelayed(0, 4000);
    }
    
    public void onEventMainThread(Events.OnStatusEvent event) {
    	switch(event.status)
    	{
    	case Connection.STATUS_CONNECTED:
    		break;
    	// here meanings connect lost
    	case Connection.STATUS_INVALID:
    		this.finish();
    		break;
    	case Connection.STATUS_CONNECTFAILED:
    		break;
    	default:
    		break;
    	}
	}
    
    private void UpdateDeptWait() {
    	if(mWaits == null)
    		return;
    	
    	Connection con = CQApplication.getConnection();
    	
    	try{
    		con.mDeptsMutex.lock();
    		if(con.mDepts != null){
	    	    int deptSize = con.mDepts.size();
	    		for(int i = 0; i < mWaits.size(); i++)
	    		{
	    			int index = i+deptIndex;
	    			if(index < deptSize)
	    			{
	    				mWaits.get(i).setDept(con.mDepts.get(index).m_csDeptName, con.mDepts.get(index).m_nWaitNum+"人");
	    			}
	    			else
	    			{
	    				mWaits.get(i).setText("");
	    			}
	    		}
    		}
    	}finally{
    		con.mDeptsMutex.unlock();
    	}
    	
    }
    
    @Override
	public void onBackPressed() {
    	if(playing)
    	{
    		StopPlayback("");
    	}
    	else
    	{
	    	CQApplication.getConnection().disconnect();
			super.onBackPressed();
    	}
	}
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    }
    
    private void displayToast(String s)
    {
    	Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onGifComplete(int count) {
		if(count >= repeatCount && repeatCount != 0)
		{
			StopPlayback("");
		}
	}
}
