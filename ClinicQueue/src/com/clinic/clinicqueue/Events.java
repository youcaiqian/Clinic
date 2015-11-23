package com.clinic.clinicqueue;

import java.util.ArrayList;
import com.clinic.clinicqueue.message.Patient;

public class Events {

	public static class OnBackPressEvent {
	}
	
	public static class OnDeptWaitEvent {
	}
	
	public static class OnPatientDataEvent {
		public byte type;
		public ArrayList<Patient> data;
	}
	
	public static class OnDisplayInfoEvent {
		public byte type;
		public String info;
	}
	
	public static class OnMediaEvent {
		public String path;
		public boolean fullScreen;
		public int repCount;
	}
	
	public static class OnTransferringEvent {
		public long progress;
		public long total;
	}
	
	public static class OnTransferringError {
		public String filename;
	}

	public static class OnStatusEvent {
		OnStatusEvent() {
			status = -1;
			progress = 0;
			info = "";
		}

		public int status;
		public int progress;
		public String info;
	}
}
