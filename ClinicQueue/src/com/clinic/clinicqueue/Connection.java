package com.clinic.clinicqueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.SparseArray;

import com.clinic.clinicqueue.Events.OnDeptWaitEvent;
import com.clinic.clinicqueue.Events.OnDisplayInfoEvent;
import com.clinic.clinicqueue.Events.OnMediaEvent;
import com.clinic.clinicqueue.Events.OnPatientDataEvent;
import com.clinic.clinicqueue.WifiReceiver.WifiStateListener;
import com.clinic.clinicqueue.message.Dept;
import com.clinic.clinicqueue.message.Patient;
import com.clinic.clinicqueue.message.Transfer;

import de.greenrobot.event.EventBus;

public class Connection implements ICQConnectNotify,
		ICQMessageNotify, WifiStateListener {

	public static final int STATUS_INVALID = -1;
	public static final int STATUS_CONNECTING = 0;
	public static final int STATUS_CONNECTED = 1;
	public static final int STATUS_CONNECTFAILED = 2;
	
	private int status = STATUS_INVALID;
	private CQConnectReceiver connectReceiver;
	private CQMessageReceiver messageReceiver;
	private WifiReceiver wifiReceiver;
	private Context context;
	private CQClient client;
	
	public ReentrantLock mDeptsMutex = new ReentrantLock();
	public ArrayList<Dept> mDepts = null;
	private ReentrantLock mPatientsMutex = new ReentrantLock();
	private ArrayList<Patient> mPatientData = new ArrayList<Patient>();
	private SparseArray<Transfer> mTransMap = new SparseArray<Transfer>();
	
	public int GetDeptWaitSize(){
		int size = 0 ;
		try{
			mDeptsMutex.lock();
			if(mDepts != null) size = mDepts.size();
		}finally{
			mDeptsMutex.unlock();
		}
		
		return size;
	}

	public void start(Context context) {
		this.context = context;
		try {
			this.client = new CQClient(context);
		} catch (CQClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectReceiver = new CQConnectReceiver(this);
		wifiReceiver = new WifiReceiver(this);
		wifiReceiver.register(context);
		connectReceiver.register(context);
		messageReceiver = new CQMessageReceiver(this);
		messageReceiver.register(context);
	}

	public void stop() {
		messageReceiver.unregister(context);
		messageReceiver = null;
		connectReceiver.unregister(context);
		connectReceiver = null;
		wifiReceiver.unregister(context);
	}

	public boolean sendMessage(int id, String msg) {
		try {
			client.SendText(id, msg);
			return true;
		} catch (CQClientException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void disconnect() {
		if (client.IsConnected()) {
			try {
				client.Disconnect();
			} catch (CQClientException e) {
				e.printStackTrace();
			}
		}
	}

	public void connect() {
		if (client.IsConnected()) {
			try {
				client.Disconnect();
			} catch (CQClientException e) {
				e.printStackTrace();
			}
		} else {
			try {
				client.SetServerInfo(Setting.getServerAddr(), Setting.getServerPort());
				client.Connect();
			} catch (CQClientException e) {

			}
		}
	}

	@Override
	public void onTextMessage(int server, int id, String text) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProgressMessage(int server, int id, long min, long max,
			long progress) {
		
	}
	
	@Override
	public void onPatientDataMessage(byte dataType, ArrayList<Patient> data) {		
    	switch(dataType)
		{
		case 1:
		{
			try{
				mPatientsMutex.lock();
				mPatientData.clear();
				mPatientData = data;
			}finally{
				mPatientsMutex.unlock();
			}
			break;
		}
		case 5:
		case 6:
		{
			for(int i = 0; i < data.size(); i++)
			{
				UpdatePatientList(data.get(i), dataType);
			}
			break;
		}
		default:
			break;
		}
    }
    
    public void UpdatePatientList(Patient p, byte type){
    	if(type == 5){
    		try{
				mPatientsMutex.lock();
				if(mPatientData.size() < 10){
					mPatientData.add(0, p);
				}
				else{
					while(mPatientData.size() > 9){
						mPatientData.remove(9);
					}
					mPatientData.add(0, p);
				}
    		}finally{
				mPatientsMutex.unlock();
			}
    	}
    	else{
    		try{
				mPatientsMutex.lock();
				int i = 0;
				for(i = 0; i < mPatientData.size(); i++){
					if(mPatientData.get(i).m_nRegId == p.m_nRegId){
						break;
					}
				}
				if(i >= 0 && i < mPatientData.size()){
					mPatientData.remove(i);
				}
				while(mPatientData.size() > 9){
					mPatientData.remove(9);
				}
				mPatientData.add(0, p);
    		}finally{
				mPatientsMutex.unlock();
			}
    	}
    	
    	GenerateStringsAndDisplay();
    }
    
    public void GenerateStringsAndDisplay(){
    	for(int i = 0; i < mPatientData.size(); i++){
    		if(i == 0){
    			CQApplication.CQTextToSpeech(GetVoiceString(mPatientData.get(0)));
    		}
    	}
    	
    	OnPatientDataEvent ev = new OnPatientDataEvent();
		ev.data = mPatientData;
		EventBus.getDefault().post(ev);
    }
    
    private String GetVoiceString(Patient p){
    	String strVoice = p.m_nVisitNum + "号" + p.m_csPatientName + "请到" + p.m_csDeptName + p.m_csDeptLocation + "就诊";
    	return strVoice;
    }
 	
	@Override
	public void onDeptWaitMessage(byte dataType, ArrayList<Dept> data) {
		try{
			mDeptsMutex.lock();
			switch(dataType)
	    	{
	    	case 1:
	    		mDepts = data;
	    		break;
	    	case 3:
	    		for(int i = 0; i < data.size(); i++)
	    		{
	    			for(int j = 0; j < mDepts.size(); j++)
	    			{
	    				if(mDepts.get(j).m_csDeptCode.equals(data.get(i).m_csDeptCode))
	    				{
	    					mDepts.get(j).m_nWaitNum = data.get(i).m_nWaitNum;
	    					break;
	    				}
	    			}
	    		}
	    		break;
	    	default:
	    		break;
	    	}
		}finally {
			mDeptsMutex.unlock();
		}
		
		OnDeptWaitEvent ev = new OnDeptWaitEvent();
		EventBus.getDefault().post(ev);
	}
	@Override
	public void onDisplayInfoMessage(byte dataType, String info){
		OnDisplayInfoEvent ev = new OnDisplayInfoEvent();
		ev.type = dataType;
		ev.info = info;
		EventBus.getDefault().post(ev);
	}
	
	@Override
	public void onTransferRequest(int server, int tid, String name,
			String mimeType, long size) {
		try {
			mTransMap.put(tid, new Transfer("/sdcard/DCIM/" + name, mimeType, size));
			
			client.SendTransferAck(true, tid, 0);
		} catch (CQClientException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTransferData(int server, int tid, long offset, int size,
			byte[] data) {
		Transfer item = mTransMap.get(tid);
		if(item != null){
			item.WriteBuffer(data, offset, size);
			
			if(item.mPos >= item.mSize){
				item.Close();
				String info = item.mInfo;
				mTransMap.remove(tid);
				OnMediaEvent ev = new OnMediaEvent();
				JSONTokener tokener = new JSONTokener(info);
				JSONObject object;
				try {
					object = (JSONObject) tokener.nextValue();
					if (object != null) {
						ev.fullScreen = object.optInt("full") == 1;
						ev.repCount = object.optInt("count");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ev.path = item.mFileName;
				EventBus.getDefault().post(ev);
			}
		}
		
	}

	@Override
	public void onConnect(int id) {
		status = STATUS_CONNECTED;
		Events.OnStatusEvent sevent = new Events.OnStatusEvent();
		sevent.status = this.status;
		EventBus.getDefault().post(sevent);
		
		try {
			client.Login(Setting.getUser());
		} catch (CQClientException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisconnect(int id) {
		status = STATUS_INVALID;
		Events.OnStatusEvent sevent = new Events.OnStatusEvent();
		sevent.status = this.status;
		EventBus.getDefault().post(sevent);
	}

	@Override
	public void onConnectError(int id) {
		status = STATUS_CONNECTFAILED;
		Events.OnStatusEvent sevent = new Events.OnStatusEvent();
		sevent.status = this.status;
		EventBus.getDefault().post(sevent);
	}

	@Override
	public void onStateChanged(State state) {

	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}

		return false;
	}
}
