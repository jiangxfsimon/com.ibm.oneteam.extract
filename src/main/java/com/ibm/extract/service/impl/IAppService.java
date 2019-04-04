package com.ibm.extract.service.impl;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.extract.model.App;
import com.ibm.extract.model.Chain;
import com.ibm.extract.model.EntityInfo;
import com.ibm.extract.model.OrgCode;

public interface IAppService {
	public List<App> getAllApp();
	public List<String> getRequests(String appCode);
	public List<String> getRequestsBySpecificApp(String appCode);
	public List<Chain> getChains(List<String> serialCcs);
	public Map<String, Object> getUpLineChain(String serialCcs);
	public List<OrgCode> getBpCodeByApi();
	public void saveInLocalDB(List<OrgCode> list);
	public List<OrgCode> getAllOrgCode();
	public List<EntityInfo> buildEntityInfo(String serialCcs,Map<String, Object> maps,List<OrgCode> orgCodes );
	public Map<String, String> getJobResponses(List<String> serialCcs);
	
	
}
