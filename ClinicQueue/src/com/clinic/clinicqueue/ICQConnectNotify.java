package com.clinic.clinicqueue;

/**
 * Implement this interface in combination with @see ABConnectReceiver to
 * get connection based notifications.
 * @author Frank Schmid
 *
 */
public interface ICQConnectNotify {
	
	/**
	 * Called when an authenticated connection to a server
	 * has been established.
	 * @param id	Server id.
	 */
	void onConnect(int id);
	
	/**
	 * Called when a server has been disconnected from the client.
	 * @param id	Server id.
	 */
	void onDisconnect(int id);
	
	/**
	 * Called when something went wrong while connecting to the server.
	 * @param id	Server id.
	 */
	void onConnectError(int id);
}
