package com.clinic.clinicqueue;

public class ServerConfig implements java.io.Serializable {

	private static final long serialVersionUID = -8291221201223702258L;

	public ServerConfig(String host, int port) {
		m_port = port;
		m_host = host;
	}
	
	private int m_port;
	private String m_host;
	
	public final int getPort() {
		return m_port;
	}
	
	public final String getHost() {
		return m_host;
	}
}
