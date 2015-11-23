package com.clinic.clinicqueue.message;

//ProtocolUnknown = 0,
//ProtocolPatientReq = 100,
//ProtocolPatientData,
//ProtocolTransferReq,
//ProtocolTransferAck,
//ProtocolTransferData,
//ProtocolUserLogin,
//ProtocolUserLoginAck,
//ProtocolCall,
//ProtocolCallAck,
//ProtocolStateChange,
//ProtocolDeptWait,
//ProtocolDisplayInfo

public enum MessageType {
	/**
	 * Simple text message
	 */
	TEXT((byte)2),
	/**
	 * Patient 
	 */
	PATIENT_DATA((byte)101),
	/**
	 * User login
	 */
	LOGIN((byte)105),
	LOGIN_ACK((byte)106),
	/**
	 * Transfer control
	 */
	TRANSFER_REQ((byte)102),
	TRANSFER_ACK((byte)103),
	TRANSFER_DATA((byte)104),
	DEPT_WAIT((byte)110),
	DISPLAY_INFO((byte)111);
	
    private final byte index;

    MessageType(byte index) {
        this.index = index;
    }

    public byte index() { 
        return index; 
    }
    
    public static MessageType fromByte(byte i) {
    	switch (i) {
    	case 2:
    		return TEXT;
    	case 101:
    		return PATIENT_DATA;
    	case 102:
    		return TRANSFER_REQ;
    	case 103:
    		return TRANSFER_ACK;
    	case 104:
    		return TRANSFER_DATA;
    	case 105:
    		return LOGIN;
    	case 106:
    		return LOGIN_ACK;
    	case 110:
    		return DEPT_WAIT;
    	case 111:
    		return DISPLAY_INFO;
    	}
    	
    	return null;
    }
}
