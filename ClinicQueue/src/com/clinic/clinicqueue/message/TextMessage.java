package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TextMessage extends Message {

	private String mText;
	private int mId;
	
	public TextMessage(int id, String text) {
		super(MessageType.TEXT, MessagePriority.NORMAL);
		mText = text;
		mId = id;
	}
	
	TextMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.NORMAL);

		// Read data
		mId = buffer.getInt();
		int length = buffer.getInt();
		byte[] textBytes = new byte[length];
		buffer.get(textBytes);
		mText = new String(textBytes);
	}
	
	public int getId() {
		return mId;
	}
	
	public String getText() {
		return mText;
	}

	@Override
	protected void write(ByteBuffer buffer) {
		buffer.putInt(mId);
		buffer.putInt(mText.getBytes().length);
		buffer.put(mText.getBytes());
	}

}
