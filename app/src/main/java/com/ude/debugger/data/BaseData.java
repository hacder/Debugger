package com.ude.debugger.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class BaseData<T> {
	private int code;
	private String runTime;
	private T data;
	private String count;
	private String searchtime;
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getRunTime() {
		return runTime;
	}
	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getSearchtime() {
		return searchtime;
	}
	public void setSearchtime(String searchtime) {
		this.searchtime = searchtime;
	}
	@Override
	public String toString() {
		return "BaseData [code=" + code + ", runTime=" + runTime + ", data=" + data + ", count=" + count
				+ ", searchtime=" + searchtime + "]";
	}
	
	
	
}
