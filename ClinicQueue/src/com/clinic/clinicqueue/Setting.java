package com.clinic.clinicqueue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;

@SuppressLint("NewApi")
public class Setting {
	private static final String PREFS_NAME = "setting.perf";
	
	public static String getServerAddr()
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sPreferences.getString("server_host", "127.0.0.1");
	}
	
	public static void setServerAddr(String host)
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sPreferences.edit();
		editor.putString("server_host", host);
		editor.apply();
	}
	
	public static int getServerPort()
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sPreferences.getInt("server_port", 999);
	}
	
	public static void setServerPort(int port)
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sPreferences.edit();
		editor.putInt("server_port", port);
		editor.apply();
	}
	
	public static String getUser()
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sPreferences.getString("user", "1502");
	}
	
	public static void setUser(String user)
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sPreferences.edit();
		editor.putString("user", user);
		editor.apply();
	}
	
	public static String getTitle()
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sPreferences.getString("title", "标题栏");
	}
	
	public static void setTitle(String title)
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sPreferences.edit();
		editor.putString("title", title);
		editor.apply();
	}
	
	public static String getBottom()
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sPreferences.getString("bottom", "底部栏");
	}
	
	public static void setBottom(String bottom)
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sPreferences.edit();
		editor.putString("bottom", bottom);
		editor.apply();
	}
	
	public static String getNotify()
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return sPreferences.getString("notify", "通知公告\n请各位病人耐心等待叫号,并根据叫号信息到指定诊室就诊,如果过号请重新取号。特殊情况请联系值班护士。");
	}
	
	public static void setNotify(String notify)
	{
		SharedPreferences sPreferences = CQApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sPreferences.edit();
		editor.putString("notify", notify);
		editor.apply();
	}
}
