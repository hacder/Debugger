package com.ude.debugger.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class TimeData {
	private int timestamp;

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "TimeData [timestamp=" + timestamp + "]";
	}
	
	
}
