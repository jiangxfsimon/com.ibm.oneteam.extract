package com.ibm.extract.model;

public class Chain {
	private String user;
	private String userNotes;
	private String userName;
	private String bpOrgCode;
	
	private String manager;
	private String mgrNotes;
	private String mgrName;
	
	private String funLeader;
	private String funLeaderNotes;
	private String funLeaderName;
	
	public Chain() {
		super();
	}
	
	public Chain(String user, String userNotes, String userName, String bpOrgCode, String manager, String mgrNotes,
			String mgrName, String funLeader, String funLeaderNotes, String funLeaderName) {
		super();
		this.user = user;
		this.userNotes = userNotes;
		this.userName = userName;
		this.bpOrgCode = bpOrgCode;
		this.manager = manager;
		this.mgrNotes = mgrNotes;
		this.mgrName = mgrName;
		this.funLeader = funLeader;
		this.funLeaderNotes = funLeaderNotes;
		this.funLeaderName = funLeaderName;
	}

	public String getBpOrgCode() {
		return bpOrgCode;
	}
	public void setBpOrgCode(String bpOrgCode) {
		this.bpOrgCode = bpOrgCode;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUserNotes() {
		return userNotes;
	}
	public void setUserNotes(String userNotes) {
		this.userNotes = userNotes;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getMgrNotes() {
		return mgrNotes;
	}
	public void setMgrNotes(String mgrNotes) {
		this.mgrNotes = mgrNotes;
	}
	public String getMgrName() {
		return mgrName;
	}
	public void setMgrName(String mgrName) {
		this.mgrName = mgrName;
	}
	public String getFunLeader() {
		if(funLeader==null || funLeader.equals("")) {
			return manager;
		}
		return funLeader;
	}
	public void setFunLeader(String funLeader) {
		this.funLeader = funLeader;
	}
	public String getFunLeaderNotes() {
		if(funLeaderNotes==null || funLeaderNotes.equals("")) {
			return mgrNotes;
		}
		return funLeaderNotes;
	}
	public void setFunLeaderNotes(String funLeaderNotes) {
		this.funLeaderNotes = funLeaderNotes;
	}
	public String getFunLeaderName() {
		return funLeaderName;
	}
	public void setFunLeaderName(String funLeaderName) {
		this.funLeaderName = funLeaderName;
	}
	@Override
	public String toString() {
		return "Chain [user=" + user + ", userNotes=" + userNotes + ", userName=" + userName + ", bpOrgCode="
				+ bpOrgCode + ", manager=" + manager + ", mgrNotes=" + mgrNotes + ", mgrName=" + mgrName
				+ ", funLeader=" + funLeader + ", funLeaderNotes=" + funLeaderNotes + ", funLeaderName=" + funLeaderName
				+ "]";
	}
	
	
	
}
