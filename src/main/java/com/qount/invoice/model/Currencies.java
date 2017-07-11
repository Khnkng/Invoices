package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Currencies {

	private String code;
	private String name;
	private String html_symbol;
	private String java_symbol;

	public String getJava_symbol() {
		return java_symbol;
	}

	public void setJava_symbol(String java_symbol) {
		this.java_symbol = java_symbol;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHtml_symbol() {
		return html_symbol;
	}

	public void setHtml_symbol(String html_symbol) {
		this.html_symbol = html_symbol;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.toString();
	}
}
