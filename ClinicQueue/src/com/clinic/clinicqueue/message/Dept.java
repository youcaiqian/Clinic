package com.clinic.clinicqueue.message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class Dept{
	
	public String m_csDeptCode = "";
	public String m_csDeptName = "";
	public long m_nMZKS = 0;
	public String m_csLocation = "";
	public int m_nWaitNum = 0;
	
	public Dept() {
	}
	
	Dept(ByteBuffer buffer) throws BufferUnderflowException {
		buffer.getInt();
		m_csDeptCode = Message.ReadString(buffer);
		m_csDeptName = Message.ReadString(buffer);
		m_nMZKS = buffer.getLong();
		m_csLocation = Message.ReadString(buffer);
		m_nWaitNum = buffer.getInt();
	}

	protected int write(ByteBuffer buffer) {
		int sizePos = buffer.position();
		int size = 4 + 8 + 4;
		buffer.putInt(0);            // 4   
		size += Message.WriteString(buffer, m_csDeptCode);
		size += Message.WriteString(buffer, m_csDeptName);
		buffer.putLong(m_nMZKS);     // 8
		size += Message.WriteString(buffer, m_csLocation);
		buffer.putInt(m_nWaitNum);   // 4
		int lastPos = buffer.position();
		buffer.position(sizePos);
		buffer.putInt(size);
		buffer.position(lastPos);
		return size;
	}

}
