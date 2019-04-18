package com.ibm.extract.model;

public class EntityInfo {
	private String cnum;
	private String EmpNotes;
	private String funLeaderNotes;
	private String funLeaderCnum;
	private String bpMgrNotes;
	private String bpMgrCnum;
	private String jobRespons;
	private String hrOrgCode;
	private String hrOrgDisplay;
	private String hrUnitId;
	
	
	public EntityInfo() {
		super();
	}
	
	public EntityInfo(String cnum, String empNotes, String funLeaderNotes, String bpMgrNotes, String jobRespons,
			String hrOrgCode, String hrOrgDisplay, String hrUnitId) {
		super();
		this.cnum = cnum;
		EmpNotes = empNotes;
		this.funLeaderNotes = funLeaderNotes;
		this.bpMgrNotes = bpMgrNotes;
		this.jobRespons = jobRespons;
		this.hrOrgCode = hrOrgCode;
		this.hrOrgDisplay = hrOrgDisplay;
		this.hrUnitId = hrUnitId;
	}
	
	
	//Constructor with funLeaderCnum and bpMgrCnum
	public EntityInfo(String cnum, String empNotes, String funLeaderNotes, String funLeaderCnum, String bpMgrNotes,
			String bpMgrCnum, String jobRespons, String hrOrgCode, String hrOrgDisplay, String hrUnitId) {
		super();
		this.cnum = cnum;
		this.EmpNotes = empNotes;
		this.funLeaderNotes = funLeaderNotes;
		this.funLeaderCnum = funLeaderCnum;
		this.bpMgrNotes = bpMgrNotes;
		this.bpMgrCnum = bpMgrCnum;
		this.jobRespons = jobRespons;
		this.hrOrgCode = hrOrgCode;
		this.hrOrgDisplay = hrOrgDisplay;
		this.hrUnitId = hrUnitId;
	}

	
	public String getAttributeByIndex(int index) {
		String attr="";
		switch(index) {
			case 1: attr=this.getCnum(); break;
			case 2: attr=this.getEmpNotes();break;
			case 3: attr=this.getFunLeaderNotes();break;
			case 4: attr=this.getBpMgrNotes();break;
			case 5: attr=this.getJobRespons();break;
			case 6: attr=this.getHrOrgDisplay();break;
			case 7: attr=this.getHrUnitId();break;
			default: attr="Error";
		}
			return attr;
	}
	
	

	public String getFunLeaderCnum() {
		if(funLeaderCnum==null || funLeaderCnum.equals("")) {
			return bpMgrCnum;
		}
		return funLeaderCnum;
	}

	public void setFunLeaderCnum(String funLeaderCnum) {
		this.funLeaderCnum = funLeaderCnum;
	}

	public String getBpMgrCnum() {
		return bpMgrCnum;
	}

	public void setBpMgrCnum(String bpMgrCnum) {
		this.bpMgrCnum = bpMgrCnum;
	}

	public String getCnum() {
		return cnum;
	}
	public void setCnum(String cnum) {
		this.cnum = cnum;
	}
	public String getEmpNotes() {
		return EmpNotes;
	}
	public void setEmpNotes(String empNotes) {
		EmpNotes = empNotes;
	}
	public String getFunLeaderNotes() {
		if(funLeaderNotes==null || funLeaderNotes.equals("")) {
			return bpMgrNotes;
		}
		return funLeaderNotes;
	}
	public void setFunLeaderNotes(String funLeaderNotes) {
		this.funLeaderNotes=funLeaderNotes;
	}
	public String getBpMgrNotes() {
		return bpMgrNotes;
	}
	public void setBpMgrNotes(String bpMgrNotes) {
		this.bpMgrNotes = bpMgrNotes;
	}
	public String getJobRespons() {
		return jobRespons;
	}
	public void setJobRespons(String jobRespons) {
		this.jobRespons = jobRespons;
	}
	public String getHrOrgCode() {
		return hrOrgCode;
	}
	public void setHrOrgCode(String hrOrgCode) {
		this.hrOrgCode = hrOrgCode;
	}
	public String getHrOrgDisplay() {
		return hrOrgDisplay;
	}
	public void setHrOrgDisplay(String hrOrgDisplay) {
		this.hrOrgDisplay = hrOrgDisplay;
	}
	public String getHrUnitId() {
		return hrUnitId;
	}
	public void setHrUnitId(String hrUnitId) {
		this.hrUnitId = hrUnitId;
	}
	@Override
	public String toString() {
		return "EntityInfo [cnum=" + cnum + ", EmpNotes=" + EmpNotes + ", funLeaderNotes=" + funLeaderNotes
				+ ", bpMgrNotes=" + bpMgrNotes + ", jobRespons=" + jobRespons + ", hrOrgCode=" + hrOrgCode
				+ ", hrOrgDisplay=" + hrOrgDisplay + ", hrUnitId=" + hrUnitId + "]";
	}
	
}
