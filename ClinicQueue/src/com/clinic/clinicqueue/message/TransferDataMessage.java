package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TransferDataMessage extends Message {

	private int mId;
	private long mOffset;
	private byte[] mData;
	
	public TransferDataMessage(int cookie, long offset, byte[] data) {
		super(MessageType.TRANSFER_DATA, MessagePriority.HIGH);
		
		mId = cookie;
		mOffset = offset;
		mData = data;
	}
	
	TransferDataMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.HIGH);

		mId = buffer.getInt();
		mOffset = buffer.getLong();
		int size = buffer.getInt();
		mData = new byte[size-4];
		buffer.get(mData);
	}

	public int getId() {
		return mId;
	}
	
	public long getOffset() {
		return mOffset;
	}
	
	public int getSize() {
		return mData.length;
	}
	
	public byte[] getData() {
		return mData;
	}
	
	@Override
	protected void write(ByteBuffer buffer) {
		buffer.putInt(mId);
		buffer.putLong(mOffset);
		buffer.putInt(mData.length+4);
		buffer.put(mData);
	}

}
