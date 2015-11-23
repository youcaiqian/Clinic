package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class LoginMessage extends Message {

	private byte mUserType = 3; // for displayer

	private String mUserName = "";
	private String mPassword = "";
	private String mDeptName = "";
	private String mHostAddr = "";
	
	public LoginMessage(String name) {
		super(MessageType.LOGIN, MessagePriority.NORMAL);
		mUserName = name;
	}
	
	LoginMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.NORMAL);
		buffer.getInt();
		mUserType = buffer.get();
		mUserName = Message.ReadString(buffer);
		mPassword = Message.ReadString(buffer);
		mDeptName = Message.ReadString(buffer);
		mHostAddr = Message.ReadString(buffer);
	}

	@Override
	protected void write(ByteBuffer buffer) {
		int sizePos = buffer.position();
		int size = 4 + 1;
		buffer.putInt(0);
		buffer.put(mUserType);
		size += Message.WriteString(buffer, mUserName);
		size += Message.WriteString(buffer, mPassword);
		size += Message.WriteString(buffer, mDeptName);
		size += Message.WriteString(buffer, mHostAddr);
		int lastPos = buffer.position();
		buffer.position(sizePos);
		buffer.putInt(size);
		buffer.position(lastPos);		
	}

}
