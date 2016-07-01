package com.happy.model.app;

import java.io.Serializable;

/**
 * Toast提示
 * 
 * @author Administrator
 * 
 */
public class Alert implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String alertText;

	public String getAlertText() {
		return alertText;
	}

	public void setAlertText(String alertText) {
		this.alertText = alertText;
	}

}
