package com.ibm.extract.model;

public class OrgCode {
	private String hrOrgCode;
	private String hrOrgDisplay;
	private String hrUnitId;
	
	public OrgCode() {
		super();
	}
	public OrgCode(String hrOrgCode, String hrOrgDisplay, String hrUnitId) {
		super();
		this.hrOrgCode = hrOrgCode;
		this.hrOrgDisplay = hrOrgDisplay;
		this.hrUnitId = hrUnitId;
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
		return "OrgCode [hrOrgCode=" + hrOrgCode + ", hrOrgDisplay=" + hrOrgDisplay + ", hrUnitId=" + hrUnitId + "]";
	}
	
	
}
