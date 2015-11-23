package com.clinic.clinicqueue;

import java.io.FileOutputStream;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Application;
import android.speech.tts.TextToSpeech;

@SuppressLint("NewApi") public class CQApplication extends Application {

	private static CQApplication mInstance = null;
	private static Connection mConnection = null;
	private static TextToSpeech mTextToSpeech = null;
	
	public static Connection getConnection()
	{
		if(mConnection == null)
			mConnection = new Connection();
		return mConnection;
	}
	
	public static CQApplication getInstance()
	{
		return mInstance;
	}
	
	public static void CQTextToSpeech(String text){
		if(mTextToSpeech != null){
			mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
		}
	}
	
	public FileOutputStream OpenFile(String file){
		try{
			FileOutputStream os = this.openFileOutput(file, MODE_PRIVATE );
			return os;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		if(mConnection == null)
			mConnection = new Connection();
		mConnection.start(getApplicationContext());
		
		mTextToSpeech = new TextToSpeech(this,new TextToSpeech.OnInitListener()
        {
        	@Override
        	public void onInit(int status) 
        	{
        		if(status == TextToSpeech.SUCCESS)
        		{
        			int supported = mTextToSpeech.setLanguage(Locale.CHINA);
        			if( (supported != TextToSpeech.LANG_AVAILABLE)
        					&&(supported != TextToSpeech.LANG_COUNTRY_AVAILABLE))
        			{
        				
        			}
        			
        			mTextToSpeech.speak("语音系统启动", TextToSpeech.QUEUE_ADD, null);
        		}
        	}
        }, "com.iflytek.tts");
	}

	@Override
	public void onTerminate() {
		mConnection.stop();
		mConnection = null;
    	if(mTextToSpeech != null){
    		mTextToSpeech.shutdown();
    	}
    	mTextToSpeech = null;
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
