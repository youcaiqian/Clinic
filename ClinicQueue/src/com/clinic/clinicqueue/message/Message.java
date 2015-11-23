package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("NewApi") public abstract class Message implements Comparable<Message> {

	private static AtomicInteger SequenceNumber = new AtomicInteger();

	private static final int VERSION = 1;
	private final int mSeqNo = SequenceNumber.incrementAndGet();
	private MessageType mType;
	private MessagePriority mPriority;
	private boolean mRestricted;

	public final MessageType getType() {
		return mType;
	}

	public final int getSequenceNo() {
		return mSeqNo;
	}

	public final boolean isRestricted() {
		return mRestricted;
	}
	
	public static String ReadString(ByteBuffer buffer) {
		int length = buffer.getInt();
		buffer.get();
		if(length > 5)
		{
			byte[] textBytes = new byte[length-5];
			buffer.get(textBytes);
			return new String(textBytes);
		}
		return "";
	}
	
	public static int WriteString(ByteBuffer buffer, String str) {
		int len = GetStringBufSize(str);
		buffer.putInt(len);
		buffer.put((byte)4);
		if(len > 5)
		{
			buffer.put(str.getBytes());
		}
		
		return len;
	}
	
	public static int GetStringBufSize(String str) {
		return str.getBytes().length + 4 + 1;
	}

	Message(MessageType type, MessagePriority priority) {
		mType = type;
		mPriority = priority;
		mRestricted = false;
	}

	@Override
	public int compareTo(Message another) {
		return (mPriority == another.mPriority) ? (mSeqNo - another.mSeqNo)
				: (mPriority.index() - another.mPriority.index());
	}

	public void setRestricted(boolean restricted) {
		mRestricted = restricted;
	}

	/**
	 * Write the message to a buffer
	 * 
	 * @param buffer
	 *            Target buffer
	 */
	public final int writeBuffer(ByteBuffer buffer) {
		writeHeader(buffer);
		write(buffer);
		return finishMessage(buffer);
	}

	public static Message readBuffer(ByteBuffer buffer)
			throws BufferUnderflowException {
		if (buffer.remaining() < MessageHeader.HeaderSize()) {
			throw new BufferUnderflowException();
		}

		Message result = null;

		MessageHeader header = MessageHeader.read(buffer);
		// Sanity checks
		if ((header.getSize() - MessageHeader.HeaderSize() > buffer.remaining())) {
			// don't have all the message data yet
			return result;
			//throw new BufferUnderflowException();
		}

		switch (header.getType()) {
		case TEXT:
			result = new TextMessage(header, buffer);
			break;
		case TRANSFER_ACK:
			result = new TransferAckMessage(header, buffer);
			Log.d("MSG", "<- ACK :" + ((TransferAckMessage) result).getCookie());
			break;
		case TRANSFER_REQ:
			result = new TransferRequestMessage(header, buffer);
			break;
		case TRANSFER_DATA:
			result = new TransferDataMessage(header, buffer);
			break;
		case PATIENT_DATA:
			result = new PatientDataMessage(header, buffer);
			break;
		case DEPT_WAIT:
			result = new DeptWaitMessage(header, buffer);
			break;
		case DISPLAY_INFO:
			result = new DisplayInfoMessage(header, buffer);
			break;
		default:
			break;
		}

		return result;
	}

	protected abstract void write(ByteBuffer buffer);

	private final void writeHeader(ByteBuffer buffer) {
		buffer.mark();
		buffer.putInt(0); // Placeholder for size
		buffer.put(mType.index());
	}

	private final int finishMessage(ByteBuffer buffer) {
		// Insert packet size
		int size = buffer.position();
		buffer.reset();
		buffer.putInt(size);
		buffer.position(size);

		return size;
	}
}
