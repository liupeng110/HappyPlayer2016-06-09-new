package com.happy.model.app;

import java.util.List;

/**
 * 
 * @author zhangliangming http结果实体类
 * @param <T>
 *            实体类
 */
public class HttpResult<T> {
	/**
	 * 状态
	 */
	private int status;
	/**
	 * 结果
	 */
	private String resultStr;
	/**
	 * 实体类列表
	 */
	private List<T> models;
	/**
	 * 实体
	 */
	private T model;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResultStr() {
		return resultStr;
	}

	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}

	public List<T> getModels() {
		return models;
	}

	public void setModels(List<T> models) {
		this.models = models;
	}

	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}

}
