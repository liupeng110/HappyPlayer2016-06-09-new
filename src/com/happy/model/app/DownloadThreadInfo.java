package com.happy.model.app;

import java.io.Serializable;

/**
 * 任务线程
 * 
 * @author zhangliangming
 * 
 */
public class DownloadThreadInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tid;
	private int threadNum;
	private int threadID;
	private int startIndex;
	private int endIndex;
	private int type;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getThreadID() {
		return threadID;
	}

	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
