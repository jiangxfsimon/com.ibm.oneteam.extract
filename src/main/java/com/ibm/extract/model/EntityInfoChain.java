package com.ibm.extract.model;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

public class EntityInfoChain extends EntityInfo {
	private String chain="";
	
	public EntityInfoChain(String chain) {
		super();
		this.chain = chain;
	}
	
	
	
	public EntityInfoChain(String cnum, String empNotes, String funLeaderNotes, String funLeaderCnum, String bpMgrNotes,
			String bpMgrCnum, String jobRespons, String hrOrgCode, String hrOrgDisplay, String hrUnitId) {
		super(cnum, empNotes, funLeaderNotes, funLeaderCnum, bpMgrNotes, bpMgrCnum, jobRespons, hrOrgCode, hrOrgDisplay,
				hrUnitId);
	}

	public String getAttributeByIndex(int index) {
		String attr="";
		switch(index) {
			case 1: attr=this.getCnum(); break;
			case 2: attr=this.getEmpNotes();break;
			case 3: attr=this.getFunLeaderNotes();break;
			case 4: attr=this.getBpMgrNotes();break;
			case 5: attr=this.getChain();break;
			case 6: attr=this.getJobRespons();break;
			case 7: attr=this.getHrOrgDisplay();break;
			case 8: attr=this.getHrUnitId();break;
			default: attr="Error";
		}
			return attr;
	}

	
//	public static EntityInfoChain entityInfoConverter(EntityInfo entityInfo) {
//		EntityInfoChain entityInfoChain=new EntityInfoChain();
//		entityInfoChain.setCnum(entityInfo.getCnum());
//		entityInfoChain.setEmpNotes(entityInfo.getEmpNotes());
//		entityInfoChain.setBpMgrCnum(entityInfo.getBpMgrCnum());
//		entityInfoChain.setBpMgrNotes(entityInfo.getBpMgrNotes());
//		entityInfoChain.setFunLeaderCnum(entityInfo.getFunLeaderCnum());
//		entityInfoChain.setFunLeaderNotes(entityInfo.getFunLeaderNotes());
//		entityInfoChain.setJobRespons(entityInfo.getJobRespons());
//		entityInfoChain.setHrOrgCode(entityInfo.getHrOrgCode());
//		entityInfoChain.setHrOrgDisplay(entityInfo.getHrOrgDisplay());
//		entityInfoChain.setHrUnitId(entityInfo.getHrUnitId());
//		System.out.println("Constractor: "+entityInfoChain);
//		return entityInfoChain;
//	}
//	public EntityInfoChain(Chain chain) {
//		EntityInfoChain entityInfoChain=null;
//		en
//	}
//	public EntityInfoChain entityInfoConverter(EntityInfo entityInfo) {
//		EntityInfoChain entityInfoChain=null;
//		entityInfoChain=(EntityInfoChain) entityInfo;
//		entityInfoChain.setChain("1");
//		return entityInfoChain;
//	}
	
	public EntityInfoChain() {
		super();
	}
	
	
	
	public String getChain() {
		return chain;
	}
	public void setChain(String chain) {
		this.chain = chain;
	}
	@Override
	public String toString() {
		return "EntityInfoChain [chain=" + chain + ", getFunLeaderCnum()=" + getFunLeaderCnum() + ", getBpMgrCnum()="
				+ getBpMgrCnum() + ", getCnum()=" + getCnum() + ", getEmpNotes()=" + getEmpNotes()
				+ ", getFunLeaderNotes()=" + getFunLeaderNotes() + ", getBpMgrNotes()=" + getBpMgrNotes()
				+ ", getJobRespons()=" + getJobRespons() + ", getHrOrgCode()=" + getHrOrgCode() + ", getHrOrgDisplay()="
				+ getHrOrgDisplay() + ", getHrUnitId()=" + getHrUnitId()+ "]";
	}
	

	
	
}
