package com.clinic.clinicqueue;

public class CQClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6019173034207144092L;

	public enum Type {
		GENERIC,
		INVALID_CONFIGURATION,
		SERVER_NOT_AVAILABLE, 
		ALREADY_CONNECTED, 
		NOT_CONNECTED
	}
	
	private Type m_type;
	
	public CQClientException(Type type) {
		m_type = type;
	}
	
	public final Type getType() {
		return m_type;
	}
}
