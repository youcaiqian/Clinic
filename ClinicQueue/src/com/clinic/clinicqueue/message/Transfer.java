package com.clinic.clinicqueue.message;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.clinic.clinicqueue.CQApplication;

public class Transfer{
	
	public String mFileName = "";
	public String mInfo = "";
	public long mSize = 0;
	public long mPos = 0;
	public int mId = 0;
	//private FileOutputStream mHandler = null;
	private RandomAccessFile mHandler = null;
	
	public Transfer(String file, String info, long size) {
		mFileName = file;
		mInfo = info;
		mSize = size;
	}
	
	public boolean Init() {
		//mHandler = CQApplication.getInstance().OpenFile(mFileName);
		try {
			mHandler = new RandomAccessFile(mFileName, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mHandler != null;
	}
	
	public void Close() {
		try {
			if(mHandler != null)
				mHandler.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void WriteBuffer(byte[] data, long offset, int count){
		try {
			if(mHandler == null) Init();
			if(mHandler != null){
				mHandler.seek(offset);
				mHandler.write(data);
				mPos += count;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
