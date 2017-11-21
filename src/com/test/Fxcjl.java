package com.test;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Fxcjl {
	
	private long id;
	private String message;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
