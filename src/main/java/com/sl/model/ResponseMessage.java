package com.sl.model;

import java.util.List;

import lombok.Data;

@Data
public class ResponseMessage {

	private Integer stausCode;
	private String status;
	private String massage;
	private Object data;
	private List<?> list;
	public ResponseMessage(Integer stausCode, String status, String massage, Object data) {
		super();
		this.stausCode = stausCode;
		this.status = status;
		this.massage = massage;
		this.data = data;
	}
	public ResponseMessage(Integer stausCode, String status, String massage) {
		super();
		this.stausCode = stausCode;
		this.status = status;
		this.massage = massage;
	}
	public ResponseMessage(Integer stausCode, String status, String massage, List<?> list) {
		super();
		this.stausCode = stausCode;
		this.status = status;
		this.massage = massage;
		this.list = list;
	}
	
	
}
