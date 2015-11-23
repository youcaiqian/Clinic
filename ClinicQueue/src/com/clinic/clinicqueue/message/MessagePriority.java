package com.clinic.clinicqueue.message;

/**
 * Message priority definition
 * @author Frank
 *
 */
public enum MessagePriority {
	/**
	 * Network level message
	 */
	NETWORK(0),
	/**
	 * Highest priority
	 */
	HIGH(1),
	/**
	 * Normal priority
	 */
	NORMAL(2),
	/**
	 * Lowest priority
	 */
	LOW(3);
	
	private final int index;   

	MessagePriority(int index) {
        this.index = index;
    }

    public int index() { 
        return index; 
    }
	
}
