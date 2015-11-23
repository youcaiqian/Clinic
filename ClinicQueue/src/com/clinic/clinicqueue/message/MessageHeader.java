package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class MessageHeader {
	private MessageType mType;
	private int mSize;
	
	private MessageHeader() {
	}

	public MessageType getType() {
		return mType;
	}

	public int getSize() {
		return mSize;
	}
	
	public static int HeaderSize() {
		return 5;
	}
	
	public static MessageHeader read(ByteBuffer buffer) throws BufferUnderflowException {
		MessageHeader result = new MessageHeader();

		result.mSize = buffer.getInt();
		result.mType = MessageType.fromByte(buffer.get());
		
		return result;
	}
}
