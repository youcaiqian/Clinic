package com.clinic.clinicqueue;

import com.clinic.clinicqueue.message.Message;
import com.clinic.clinicqueue.message.MessageType;

/**
 * Callback interface for message receiver.
 */
public interface MessageReceived {
	void onMessageReceived(int serverid, MessageType type, Message message);
}
