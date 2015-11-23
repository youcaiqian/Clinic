package com.clinic.clinicqueue;

import java.util.ArrayList;

import com.clinic.clinicqueue.message.Dept;
import com.clinic.clinicqueue.message.Patient;

public interface ICQMessageNotify {
	/**
	 * Text message received from connected server.
	 * @param server	Server id.
	 * @param id		Text message id (set by server application).
	 * @param text		Text
	 */
	void onTextMessage(int server, int id, String text);
	
	/**
	 * Progress message received from connected server.
	 * @param server	Server id.
	 * @param id		Text message id (set by server application).
	 */
	void onProgressMessage(int server, int id, long min, long max, long progress);
	
	/**
	 * Transfer request message received from connected server.
	 * @param server		Server id.
	 * @param tid			Transfer id.
	 * @param path			Transfer item path (on server).
	 * @param mimetype		Transfer item mimeType.
	 * @param size			Transfer item size.
	 */
	void onTransferRequest(int server, int tid, String path, String mimeType, long size);
	
	/**
	 * Transfer request message received from connected server.
	 * @param server		Server id.
	 * @param tid			Transfer id.
	 * @param offset		Transfer data offset.
	 * @param size			Transfer data size.
	 * @param data			Transfer data.
	 */
	void onTransferData(int server, int tid, long offset, int size, byte[] data);

	void onDeptWaitMessage(byte dataType, ArrayList<Dept> data);
	void onPatientDataMessage(byte dataType, ArrayList<Patient> data);
	void onDisplayInfoMessage(byte dataType, String info);
}
