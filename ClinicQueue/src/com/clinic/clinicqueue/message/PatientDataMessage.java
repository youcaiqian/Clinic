package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PatientDataMessage extends Message {
	public byte mDataType = 0;
	public ArrayList<Patient> mPatients = new ArrayList<Patient>();

	public PatientDataMessage(byte dataType) {
		super(MessageType.PATIENT_DATA, MessagePriority.NORMAL);
		mDataType = dataType;
	}
	
	PatientDataMessage(MessageHeader header, ByteBuffer buffer) throws BufferUnderflowException {
		super(header.getType(), MessagePriority.NORMAL);
		mDataType = buffer.get();
		int size = buffer.getInt(); // list size
		int count = buffer.getInt();
		for(int i = 0; i < count; i++)
		{
			Patient p = new Patient(buffer);
			mPatients.add(p);
		}
	}

	@Override
	protected void write(ByteBuffer buffer) {
		buffer.put(mDataType);
		int size = 4+4;
		int sizePos = buffer.position();
		buffer.putInt(0); // list size
		buffer.putInt(mPatients.size()); // list count
		for(int i = 0; i < mPatients.size(); i++)
		{
			size += mPatients.get(i).write(buffer);
		}
		int lastPos = buffer.position();
		buffer.position(sizePos);
		buffer.putInt(size);
		buffer.position(lastPos);		
	}
}
