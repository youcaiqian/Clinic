package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TransferAckMessage extends Message {

	private boolean mAck;
	private int mCookie;
	private long mOffset;
	
	public TransferAckMessage(boolean ack, int cookie, long offset) {
		super(MessageType.TRANSFER_ACK, MessagePriority.HIGH);
		
		mAck = ack;
		mCookie = cookie;
		mOffset = offset;
	}
	
	TransferAckMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.HIGH);

		mAck = (buffer.getInt() != 0);
		mOffset = buffer.getLong();
		mCookie = buffer.getInt();
	}

	public boolean isAcknowledged() {
		return mAck;
	}
	
	public int getCookie() {
		return mCookie;
	}
	
	public long getOffset() {
		return mOffset;
	}
	
	@Override
	protected void write(ByteBuffer buffer) {
		buffer.putInt(mAck ? 1 : 0);
		buffer.putLong(mOffset);
		buffer.putInt(mCookie);
	}

}
