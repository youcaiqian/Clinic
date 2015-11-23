package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TransferRequestMessage extends Message {

	/**
	 * Wire properties
	 */
	private String mObjectName;
	private String mInfo;
	private long mObjectSize;
	private int mId;
	
	public TransferRequestMessage(String objectName, String mimeType, long objectSize, int id) {
		super(MessageType.TRANSFER_REQ, MessagePriority.HIGH);
		mObjectName = objectName;
		mInfo = mimeType;
		mObjectSize = objectSize;
		mId = id;
	}
	
	TransferRequestMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.HIGH);
		mObjectName = Message.ReadString(buffer);
		mInfo = Message.ReadString(buffer);
		mObjectSize = buffer.getLong();
		mId = buffer.getInt();
	}
	
	public int getId() {
		return mId;
	}
	
	public long getSize() {
		return mObjectSize;
	}
	
	public String getName() {
		return mObjectName;
	}
	
	public String getInfo() {
		return mInfo;
	}
	
	@Override
	protected void write(ByteBuffer buffer) {
		Message.WriteString(buffer, mObjectName);
		Message.WriteString(buffer, mInfo);
		buffer.putLong(mObjectSize);
		buffer.putInt(mId);
	}

}
