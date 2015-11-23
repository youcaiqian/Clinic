package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class Patient{
	
	public long m_nRegId = 0;
	public long m_nState = 0;
	public String m_csRegType = "";
	public String m_csPatientId = "";
	public long m_nVisitNum = 0;
	public String m_csDeptCode = "";
	public String m_csDeptName = "";
	public String m_csPatientName = "";
	public String m_csDeptLocation = "";
	
	public Patient() {
	}
	
	Patient(ByteBuffer buffer) throws BufferUnderflowException {
		buffer.getInt();
		m_nRegId = buffer.getLong();
		m_nState = buffer.getLong();
		m_csRegType = Message.ReadString(buffer);
		m_csPatientId = Message.ReadString(buffer);
		m_nVisitNum = buffer.getLong();
		m_csDeptCode = Message.ReadString(buffer);
		m_csDeptName = Message.ReadString(buffer);
		m_csPatientName = Message.ReadString(buffer);
		m_csDeptLocation = Message.ReadString(buffer);
	}

	protected int write(ByteBuffer buffer) {
		int sizePos = buffer.position();
		int size = 4 + 8 + 8 + 8;
		buffer.putInt(0);            // 4   
		buffer.putLong(m_nRegId);    // 8
		buffer.putLong(m_nState);    // 8
		size += Message.WriteString(buffer, m_csRegType);
		size += Message.WriteString(buffer, m_csPatientId);
		buffer.putLong(m_nVisitNum); // 8
		size += Message.WriteString(buffer, m_csDeptCode);
		size += Message.WriteString(buffer, m_csDeptName);
		size += Message.WriteString(buffer, m_csPatientName);
		size += Message.WriteString(buffer, m_csDeptLocation);
		int lastPos = buffer.position();
		buffer.position(sizePos);
		buffer.putInt(size);
		buffer.position(lastPos);
		return size;
	}

}
