package com.ibm.extract.model;

public class User {
	private String serialcc;
	private String notesMail;
	private String bpManager;
	private String funcLeader;
	private String jobRespons;
	private String organization;
	private String hrUnitCode;
	public String getSerialcc() {
		return serialcc;
	}
	public void setSerialcc(String serialcc) {
		this.serialcc = serialcc;
	}
	public String getNotesMail() {
		return notesMail;
	}
	public void setNotesMail(String notesMail) {
		this.notesMail = notesMail;
	}
	public String getBpManager() {
		return bpManager;
	}
	public void setBpManager(String bpManager) {
		this.bpManager = bpManager;
	}
	public String getFuncLeader() {
		return funcLeader;
	}
	public void setFuncLeader(String funcLeader) {
		this.funcLeader = funcLeader;
	}
	public String getJobRespons() {
		return jobRespons;
	}
	public void setJobRespons(String jobRespons) {
		this.jobRespons = jobRespons;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getHrUnitCode() {
		return hrUnitCode;
	}
	public void setHrUnitCode(String hrUnitCode) {
		this.hrUnitCode = hrUnitCode;
	}
	@Override
	public String toString() {
		return "User [serialcc=" + serialcc + ", notesMail=" + notesMail + ", bpManager=" + bpManager + ", funcLeader="
				+ funcLeader + ", jobRespons=" + jobRespons + ", organization=" + organization + ", hrUnitCode="
				+ hrUnitCode + "]";
	}
}
