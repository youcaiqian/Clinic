package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DisplayInfoMessage extends Message {
	public byte mDataType = 0;
	public String mInfo = "";

	public DisplayInfoMessage(byte dataType) {
		super(MessageType.DISPLAY_INFO, MessagePriority.NORMAL);
		mDataType = dataType;
	}
	
	DisplayInfoMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.NORMAL);
		mDataType = buffer.get();
		mInfo = Message.ReadString(buffer);
	}

	@Override
	protected void write(ByteBuffer buffer) {
		buffer.put(mDataType);
		Message.WriteString(buffer, mInfo);
	}
}
