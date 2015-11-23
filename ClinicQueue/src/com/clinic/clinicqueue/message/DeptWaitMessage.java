package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DeptWaitMessage extends Message {
	public byte mDataType = 0;
	public ArrayList<Dept> mDepts = new ArrayList<Dept>();

	public DeptWaitMessage(byte dataType) {
		super(MessageType.DEPT_WAIT, MessagePriority.NORMAL);
		mDataType = dataType;
	}
	
	DeptWaitMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.NORMAL);
		mDataType = buffer.get();
		int size = buffer.getInt(); // list size
		int count = buffer.getInt();
		for(int i = 0; i < count; i++)
		{
			Dept p = new Dept(buffer);
			mDepts.add(p);
		}
	}

	@Override
	protected void write(ByteBuffer buffer) {
		buffer.put(mDataType);
		int size = 4+4;
		int sizePos = buffer.position();
		buffer.putInt(0); // list size
		buffer.putInt(mDepts.size()); // list count
		for(int i = 0; i < mDepts.size(); i++)
		{
			size += mDepts.get(i).write(buffer);
		}
		int lastPos = buffer.position();
		buffer.position(sizePos);
		buffer.putInt(size);
		buffer.position(lastPos);		
	}
}
