package com.ibm.extract.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.ibm.extract.config.Configure;
import com.ibm.extract.dao.impl.IAppDao;
import com.ibm.extract.enums.Request;
import com.ibm.extract.model.App;
import com.ibm.extract.model.Chain;
import com.ibm.extract.model.EntityInfo;
import com.ibm.extract.model.EntityInfoChain;
import com.ibm.extract.model.OrgCode;
import com.ibm.extract.service.impl.IAppService;
import com.ibm.extract.thread.BatchThread;
import com.ibm.extract.utils.HttpClient;
import com.ibm.extract.utils.Utils;

@Service
@Transactional
public class AppService implements IAppService {
	
	@Resource
	private IAppDao appDao;
	
	
	@Resource
	private Configure configure;
	@Resource
	private HttpClient httpClient;
	@Resource
	private Utils utils;
	
	
	private static String AllUID;
	
	
	public static String getAllUID() {
		return AllUID;
	}
	
	@Override
	public List<App> getAllApp() {
		return appDao.getAllApp();
	}
	@Override
	public List<String> getRequests(String appCode) {
		return appDao.getRequests(appCode);
	}
	@Override
	public List<Chain> getChains(List<String> serialCcs) {
		String serialCcString=utils.listToString(serialCcs);
		List<Chain> baseChains=appDao.getChain(serialCcString);
//		System.out.println("baseChains: "+baseChains);
		return baseChains;
	}
	@Override
	public List<String> getRequestsBySpecificApp(String appCode) {
		List<String> serialCcs=null;
		switch (appCode) {
		case "FIW_UICAMMC":
			serialCcs=appDao.getRequestsFromFIW();
			break;
		case "CDMS":
			serialCcs=appDao.getRequestsFromCDMS();
			break;
		case "WWBalsht":
			serialCcs=appDao.getRequestsFromCDMS();
			break;
		case "WWConsol":
			serialCcs=appDao.getRequestsFromWWConsol();
			break;
		case "WWSD":
			serialCcs=appDao.getRequestsFromWWSD();
			break;
		default:
			throw new RuntimeException("Error");
		}
		return serialCcs;
	}
	@Override
	public Map<String, Object> getUpLineChain(String serialCcs) {
		Map<String, Object> map=new HashMap<String,Object>();
		List<String> specialEmp=appDao.getSpecialEmp();
		String specialEmpRegEx="'"+String.join("'|'", specialEmp)+"'";
		System.out.println("specialEmpRegEx: "+specialEmpRegEx);
		serialCcs="'"+serialCcs.replaceAll(",", "','")+"'";
		String funLeaderSerialCcs=serialCcs;
		Map<String, String> mgrMap=new HashMap<String, String>();
		Map<String, String> funLeaderMap=new HashMap<String, String>();
		Map<String, String> jopResponses=new HashMap<String, String>();
		Set<String> allUId=new HashSet<String>();
		//get Manager up line
		while(true) {
			List<Chain> mgrChains=appDao.getUpMgrLine(serialCcs);
//			System.out.println("mgrChains: "+mgrChains);
			List<String> mgrSerialCc=new ArrayList<String>();
			for(Chain entity:mgrChains) {
				mgrSerialCc.add(entity.getManager());
				if(entity.getUser().equals(entity.getManager())) continue;
				mgrMap.put(entity.getUser(),entity.getManager());
			}
			serialCcs=utils.removeSpecialEmp(mgrSerialCc, specialEmpRegEx);
//			System.out.println("serialCcs++: "+serialCcs);
			if(serialCcs.length()==0) {
				break;
			}
		}
		System.out.println("funLeaderSerialCcs:**** "+funLeaderSerialCcs);
		//get function leader up line
		while(true) {
			List<Chain> funLeaderChains=appDao.getUpFunLeaderLine(funLeaderSerialCcs);
			System.out.println("funLeaderChains: "+funLeaderChains);
			List<String> tempFunLeaderSerialCcs=new ArrayList<String>();
			for(Chain entity:funLeaderChains) {
//				System.out.println("FunLeader: "+entity.getFunLeader());
				if(entity.getFunLeader()!=null && !entity.getFunLeader().equals("")) {
					tempFunLeaderSerialCcs.add(entity.getFunLeader());
					funLeaderMap.put(entity.getUser(), entity.getFunLeader());
				}else {
					//if funLeader is null, add the bp manager as funLeader's chain
					tempFunLeaderSerialCcs.add(entity.getManager());
					funLeaderMap.put(entity.getUser(), entity.getManager());
				}
			}
			funLeaderSerialCcs=utils.removeSpecialEmp(tempFunLeaderSerialCcs, specialEmpRegEx);
			System.out.println("tempFunLeaderSerialCcs:"+tempFunLeaderSerialCcs);
			if(funLeaderSerialCcs.length()==0) break;
//			funLeaderSerialCcs=utils.listToString(tempFunLeaderSerialCcs);
			System.out.println("funLeaderSerialCc++: "+funLeaderSerialCcs);
		}
		System.out.println("mgrMap: "+mgrMap);
		System.out.println("funLeaderMap: "+funLeaderMap);
		
		allUId=utils.mapToSet(mgrMap,funLeaderMap);
		String allUIdStr=utils.listToString(allUId);
		
//		AllUID=allUIdStr;
		//get all the user id's serial&cc to fetch job response by BP api
		jopResponses.putAll(utils.startBatchThread(allUIdStr));
		System.out.println("jobResponses: "+jopResponses);
		
//			// 1. 将 serialCC 转化为 list
//				List<String> serialccList = Arrays.asList(allUIdStr.split(","));
//				// 2. 拆分 list
//				int size = serialccList.size();
//				// 最多启用 10 个线程
//				int maxNumber = 9, minSize = 20;
//				Map<String, String> resultMap = new HashMap<String, String>();
//				if(size > minSize) {
//					int partNumber = size/maxNumber;
//					System.out.println("partNumber: "+partNumber);
//					List<List<String>> seriaLists = Lists.partition(serialccList, partNumber);
//					System.out.println("^^^^^^^^^^^ "+seriaLists.size());
//					CountDownLatch latch = new CountDownLatch(seriaLists.size());
//					System.out.println("将启动 : " + seriaLists.size() + " 个线程");
//					for(int i = 0; i < seriaLists.size(); i++) {
//						BatchThread batchThread = new BatchThread(latch, httpClient, resultMap, seriaLists.get(i));
//						new Thread(batchThread).start();
//					}
//					try {
//						latch.await();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				} else {
//					resultMap = httpClient.requestBluePageForMap(serialccList);
//				}
//				
//				System.out.println(resultMap);
//				System.out.println("******************" + resultMap.size());
		

		
//		Map<String, String> jobResponses=utils.enableThread(allUIdStr);
//		System.out.println("jobResponses: "+jobResponses);
		
		
		
		List<Chain> allUIdChains=appDao.getChain(allUIdStr);
		
		map.put("mgrMap", mgrMap);
		map.put("funLeaderMap", funLeaderMap);
		map.put("allEntity", utils.listToMap(allUIdChains));
		map.put("jopResponses", jopResponses);
		return map;
	}
	
	@Override
	public List<OrgCode> getBpCodeByApi() {
		List<OrgCode> list=new ArrayList<OrgCode>();
		OrgCode entity=null;
		List<String> orgCodes=appDao.getBpOrgCode();
		System.out.println("orgCodes: "+orgCodes);
		for(String orgCode:orgCodes) {
			String url = String.format(configure.getOrganizationApi(), orgCode);
			String result = httpClient.request(url, Request.GET);
			System.out.println(result);
			entity=utils.stringToOrgCode(orgCode, result);
			list.add(entity);
			//////////////////////////////////////////
//			if(list.size()==20)break;
		}
		return list;
	}
	@Override
	public void saveInLocalDB(List<OrgCode> list) {
		System.out.println(list);
//		List<OrgCode> list=Arrays.asList(new OrgCode("02", "IBM Global Markets, Unknown", "Unknown"),new OrgCode("06", "", ""),new OrgCode("0A", "Finance and Operations", "CIO"),new OrgCode("0B", "Finance and Operations", "CIO"),new OrgCode("0C", "Finance and Operations", "CIO"),new OrgCode("0D", "Finance and Operations", "CIO"),new OrgCode("0E", "Finance and Operations", "CIO"),new OrgCode("0F", "Finance and Operations", "CIO"),new OrgCode("0G", "Finance and Operations", "CIO"),new OrgCode("0H", "Finance and Operations", "CIO"),new OrgCode("0I", "Finance and Operations", "CIO"),new OrgCode("0J", "Finance and Operations", "CIO"),new OrgCode("0K", "IBM Industry Platform", "WatsonFSS"),new OrgCode("0L", "IBM Cloud and Cognitive Software", "CloudLabSvcs"),new OrgCode("0M", "Global Business Services", "Dig Strat & iX"),new OrgCode("0N", "Global Business Services", "Bus Support"),new OrgCode("0O", "Global Business Services", "Cloud App Innov"),new OrgCode("0P", "Global Business Services", "Dig Strat & iX"),new OrgCode("0Q", "Finance and Operations", "CISO"),new OrgCode("0S", "IBM Systems", "IBM Z"),new OrgCode("0U", "IBM Systems", "Storage"),new OrgCode("0V", "IBM Research", "Albany"),new OrgCode("0Z", "", ""),new OrgCode("16", "IBM Global Markets - Cognitive Solutions Unit Industry Platforms (Geo)", "CSUIP Sales"),new OrgCode("1A", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("1C", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("1D", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("1F", "IBM Hybrid Cloud", "Data and AI"),new OrgCode("1G", "IBM Security", "SecuritySvcs"),new OrgCode("1H", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("1I", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("1J", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("1K", "IBM Systems", "StDev_EntSys"),new OrgCode("1L", "IBM Systems", "Power"),new OrgCode("1M", "IBM Systems", "Sys HWS L2"),new OrgCode("1N", "IBM Systems", "Power"),new OrgCode("1T", "IBM Services for Managed Applications", "Managed Apps"),new OrgCode("1V", "Global Business Services", "Bus Support"),new OrgCode("1X", "Global Technology Services", "GTS HQ/Top"),new OrgCode("1Y", "GTS, GTS NA Geo", "GTS NA Geo"),new OrgCode("1Z", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("20", "Global Technology Services", "GTS HQ/Top"),new OrgCode("22", "IBM Research", "Res Geo"),new OrgCode("23", "Global Business Services", "Cog Proc Trans"),new OrgCode("24", "Global Business Services", "Cog Proc Trans"),new OrgCode("25", "IBM Hybrid Cloud", "Cloud WW Sales"),new OrgCode("27", "IBM Global Markets", "GLMKT Non Brand"),new OrgCode("2F", "GTS, Solutioning", "Solutioning"),new OrgCode("2K", "IBM Services for Managed Applications", "Managed Apps"),new OrgCode("2L", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("2N", "Global Business Services", "Dig Strat & iX"),new OrgCode("2O", "Global Business Services", "Bus Support"),new OrgCode("2P", "IBM Cognitive Applications", "CSU Services"),new OrgCode("2R", "IBM Hybrid Cloud", "Data and AI"),new OrgCode("2T", "IBM Systems", "StDev_ShdTechOp"),new OrgCode("2U", "Finance and Operations", "EntSvGblSCQ&I"),new OrgCode("2V", "Global Business Services", "Dig Strat & iX"),new OrgCode("2W", "IBM Cognitive Applications", "TalentMgmtSolns"),new OrgCode("2X", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("2Y", "IBM Systems", "StDev_EntSys"),new OrgCode("30", "GTS, SO", "SO Other"),new OrgCode("31", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("37", "IBM Cognitive Applications", "TalentMgmtSolns"),new OrgCode("38", "IBM Global Markets, e-Business", "GLMKT Non Brand"),new OrgCode("3E", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("3F", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("3K", "IBM Systems", "StDev_EntSys"),new OrgCode("3L", "IBM Systems", "StDev_EntSys"),new OrgCode("3M", "IBM Systems", "S&O - Micro"),new OrgCode("3N", "IBM Systems", "StDev_EntSys"),new OrgCode("3O", "IBM Systems", "StDev_EntSys"),new OrgCode("3P", "IBM Systems", "StDev_EntSys"),new OrgCode("3S", "IBM Cognitive Applications", "Cog App Top"),new OrgCode("3U", "IBM Security", "SecuritySols"),new OrgCode("3Y", "IBM Cognitive Applications", "TalentMgmtSolns"),new OrgCode("3Z", "", ""),new OrgCode("41", "IBM Cognitive Applications", "Watson M&W"),new OrgCode("43", "IBM Cognitive Applications", "TalentMgmtSolns"),new OrgCode("46", "GTS, ITS", "Int Tech Svcs"),new OrgCode("48", "GTS,TSS", "TSS"),new OrgCode("4B", "IBM Cognitive Applications", "TalentMgmtSolns"),new OrgCode("4C", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("4D", "Global Business Services", "Cloud App Innov"),new OrgCode("4E", "IBM Hybrid Cloud", "Analytics"),new OrgCode("4F", "IBM Global Markets - Systems SW Sales (Geo)", "SysSWSales"),new OrgCode("4G", "IBM Systems", "Storage"),new OrgCode("4H", "IBM Services for Managed Applications", "Managed Apps"),new OrgCode("4N", "IBM Security", "SecuritySols"),new OrgCode("4R", "IBM Research", "AI&Quantum"),new OrgCode("4T", "IBM Research", "CaaS"),new OrgCode("4W", "IBM Cloud", "Cloud Svc Del"),new OrgCode("4X", "IBM Industry Platform", "WatsonFSS"),new OrgCode("4Z", "IBM Security", "Fiberlink"),new OrgCode("50", "IBM Global Markets - Global Business Partners (WW)", "Gbl Bus Ptnrs"),new OrgCode("51", "IBM Research", "Ind&Sols"),new OrgCode("55", "Finance and Operations", "Real Estate Svc"),new OrgCode("56", "IBM Global Markets - Cognitive Solutions Unit Industry Platforms (WW)", "CSUIP Sales"),new OrgCode("57", "IBM Global Markets - Global Business Partners", "Gbl Bus Ptnrs"),new OrgCode("58", "IBM Industry Platform", "IndPlatformTop"),new OrgCode("59", "IBM Cognitive Applications", "CSU Services"),new OrgCode("5A", "GTS,System Services", "SystemSvcs"),new OrgCode("5F", "IBM Global Markets, Technical Sales", "GLMKT Non Brand"),new OrgCode("5M", "SO, Gbl Security & Risk Mgmt", "Gbl Sec&RiskMgt"),new OrgCode("5R", "IBM Cloud and Cognitive Software", "Business Dev"),new OrgCode("60", "IBM Cognitive Applications", "CSU Services"),new OrgCode("66", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("6A", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("6B", "IBM Watson Health", "WH_GTM"),new OrgCode("6C", "IBM Watson Health", "WH_Innovates"),new OrgCode("6D", "GTS, GTS NA Geo", "GTS NA Geo"),new OrgCode("6E", "Enterprise & Technology Security", "T&IP"),new OrgCode("6G", "IBM Hybrid Cloud", "Cloud Top"),new OrgCode("6H", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("6J", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("6L", "IBM Watson Health", "WH_Imp&Ops"),new OrgCode("6M", "GTS, GTS NA Geo", "GTS NA Geo"),new OrgCode("6N", "IBM Watson Health", "WH_Payer"),new OrgCode("6O", "IBM Cloud and Cognitive Software", "Cloud WW Sales"),new OrgCode("6Z", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("70", "Global Business Services", "Cloud App Innov"),new OrgCode("73", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("77", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("79", "Finance and Operations", "CST"),new OrgCode("7A", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("7E", "GTS, GTS MEA Geo", "GTS MEA Geo"),new OrgCode("7F", "GTS, Optimized Services", "OptimizedSvcs"),new OrgCode("7K", "IBM Cognitive Applications", "Watson M&W"),new OrgCode("7L", "IBM Hybrid Cloud", "Cloud Platform"),new OrgCode("7M", "IBM Industry Platform", "WatsonFSS"),new OrgCode("7O", "IBM Security", "SecuritySols"),new OrgCode("7Q", "IBM Watson Health", "WH_Gov&HHS"),new OrgCode("7U", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("7V", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("7W", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("7X", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("7Y", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("7Z", "Finance and Operations, CFO", "CFO"),new OrgCode("81", "IBM Global Markets - Cloud Sales (Geo)", "Cloud Sales"),new OrgCode("82", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("83", "IBM Global Markets (WW)", "GLMKT Non Brand"),new OrgCode("88", "GTS, GTS NA Geo", "GTS NA Geo"),new OrgCode("8A", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("8B", "GTS,Networking Services", "Networkg Svcs"),new OrgCode("8E", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8G", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("8H", "GTS, Global Operations", "GlobalOps"),new OrgCode("8I", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8J", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8K", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8L", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8M", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8N", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8O", "GTS, Solutioning", "Solutioning"),new OrgCode("8P", "IBM Industry Platform", "WatsonFSS"),new OrgCode("8U", "GTS, Infrastructure Services", "InfraSvcs"),new OrgCode("8V", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("8W", "Global Business Services", "Cloud App Innov"),new OrgCode("8X", "GTS, GTS NA Geo", "GTS NA Geo"),new OrgCode("8Y", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("8Z", "GTS,Resiliency Services", "ResiliencySvcs"),new OrgCode("90", "GTS,Resiliency Services", "ResiliencySvcs"),new OrgCode("93", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("94", "GTS, System Services", "SystemSvcs"),new OrgCode("96", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("97", "IBM Global Markets - Global Business Partners (Geo)", "Gbl Bus Ptnrs"),new OrgCode("98", "GTS, System Services", "SystemSvcs"),new OrgCode("9C", "Global Business Services", "Cloud App Innov"),new OrgCode("9D", "IBM Security", "SecuritySvcs"),new OrgCode("9E", "IBM Security", "SecuritySvcs"),new OrgCode("9F", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("9G", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("9H", "IBM Industry Platform", "CSUIP Sales"),new OrgCode("9I", "", ""),new OrgCode("9J", "IBM Security", "Security Top"),new OrgCode("9K", "IBM Security", "SecuritySvcs"),new OrgCode("9L", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("9N", "Global Business Services", "Cloud App Innov"),new OrgCode("9O", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("9P", "GTS, TSS", "TSS"),new OrgCode("9R", "IBM JV", "CHQ / Other"),new OrgCode("9S", "IBM DV", "CHQ / Other"),new OrgCode("9U", "Geodis Supply Chain Operations", "GlblProcurement"),new OrgCode("9W", "", ""),new OrgCode("9X", "Finance and Operations", "CIO"),new OrgCode("9Y", "", ""),new OrgCode("A1", "IBM Systems", "IBM Z"),new OrgCode("A3", "IBM Systems", "Power"),new OrgCode("A4", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("A7", "IBM Systems", "Systems Top"),new OrgCode("A8", "IBM Systems", "Systems Top"),new OrgCode("AA", "", ""),new OrgCode("AB", "IBM Systems", "Systems Top"),new OrgCode("AD", "IBM Global Administration", "Human Resources"),new OrgCode("AE", "IBM Systems", "Marketing"),new OrgCode("AH", "IBM Cloud and Cognitive Software", "CloudLabSvcs"),new OrgCode("AI", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("AJ", "IBM Global Markets - Cognitive Solutions Unit Industry Platforms", "CSUIP Sales"),new OrgCode("AN", "IBM Systems", "StDev_ShdTechOp"),new OrgCode("AO", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("AP", "", ""),new OrgCode("AQ", "Finance and Operations, CFO", "CFO"),new OrgCode("AR", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("AS", "GTS, Optimized Services", "OptimizedSvcs"),new OrgCode("AU", "IBM Cognitive Applications", "Watson IoT"),new OrgCode("AX", "IBM Global Markets - Cloud Sales", "Cloud Sales"),new OrgCode("AY", "IBM Systems", "Storage"),new OrgCode("AZ", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("B0", "IBM Industry Platform", "Blockchain"),new OrgCode("B3", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("B8", "GTS, Optimized Services", "OptimizedSvcs"),new OrgCode("B9", "IBM Systems", "Marketing"),new OrgCode("BB", "IBM Systems", "Systems Top"),new OrgCode("BE", "IBM Systems", "Power"),new OrgCode("BF", "IBM Systems", "Sys HWS L2"),new OrgCode("BI", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("BJ", "Global Business Services", "Cog Proc Trans"),new OrgCode("BL", "", ""),new OrgCode("BO", "IBM Systems", "StDev_ClntAdv"),new OrgCode("BS", "Finance and Operations", "EntSvQ2C"),new OrgCode("BU", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("BV", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("BW", "IBM Software Sales", "CSUIP Sales"),new OrgCode("BY", "IBM Systems", "StDev_EntSys"),new OrgCode("BZ", "IBM Systems", "Systems Top"),new OrgCode("C0", "IBM Industry Platform", "Blockchain"),new OrgCode("C1", "IBM Industry Platform", "WatsonFSS"),new OrgCode("C3", "IBM Systems", "S&O - Micro"),new OrgCode("C7", "IBM Systems", "S&O - Micro"),new OrgCode("CA", "IBM Systems", "S&O - Micro"),new OrgCode("CI", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("CJ", "IBM Systems", "S&O - Micro"),new OrgCode("CL", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("CM", "IBM Industry Platform", "Promontory"),new OrgCode("CN", "IBM Systems", "S&O - Micro"),new OrgCode("CO", "IBM CHQ, Communications", "Communications"),new OrgCode("CP", "IBM Systems", "S&O - Micro"),new OrgCode("CS", "IBM Systems", "S&O - Micro"),new OrgCode("CU", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("CW", "IBM Systems", "StDev_Micro"),new OrgCode("CX", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("CZ", "IBM Software Sales", "Cloud Sales"),new OrgCode("D5", "IBM Industry Platform", "IndPlatformGTM"),new OrgCode("D8", "IBM Cognitive Applications", "Cog App Top"),new OrgCode("DB", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("DC", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("DD", "Global Business Services", "Cog Proc Trans"),new OrgCode("DF", "IBM Services for Managed Applications", "Managed Apps"),new OrgCode("DG", "IBM Cloud and Cognitive Software", "CloudLabSvcs"),new OrgCode("DJ", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("DM", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("DO", "Finance and Operations", "BusArch Trans"),new OrgCode("DQ", "IBM Cognitive Applications", "Watson IoT"),new OrgCode("DT", "", ""),new OrgCode("DU", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("DV", "Global Business Services", "Cog Proc Trans"),new OrgCode("E0", "IBM Systems", "S&O - Micro"),new OrgCode("E4", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("E6", "IBM CHQ, General Counsel", "GenCounsel"),new OrgCode("E8", "Finance and Operations, CFO", "CFO"),new OrgCode("E9", "Finance and Operations", "BusArch Trans"),new OrgCode("EB", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("EF", "IBM Cognitive Applications", "Cog App Top"),new OrgCode("EI", "IBM Global Markets", "GLMKT Non Brand"),new OrgCode("EK", "Finance and Operations, CFO", "CFO"),new OrgCode("EL", "IBM Cloud", "Cloud Sales"),new OrgCode("EM", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("EN", "IBM CHQ, Communications", "Communications"),new OrgCode("EP", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("EX", "IBM Executive Staff", "GLMKT Non Brand"),new OrgCode("EZ", "IBM Watson Health", "WH_Provider"),new OrgCode("F0", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("F1", "Finance and Operations, CFO", "CFO"),new OrgCode("F4", "IBM Digital Sales", "Digital Sales"),new OrgCode("F5", "", ""),new OrgCode("F6", "IBM Systems", "GblBusPrtnrs"),new OrgCode("F7", "IBM Global Markets - Cloud Sales (WW)", "Cloud Sales"),new OrgCode("F8", "IBM Global Markets - Systems HW Sales (Geo)", "Sys HW Sales"),new OrgCode("FC", "IBM Global Markets, Financial Services Sector Global Staff", "GLMKT Non Brand"),new OrgCode("FD", "IBM Global Markets, Public Sector Global Staff", "GLMKT Non Brand"),new OrgCode("FE", "", ""),new OrgCode("FF", "Finance and Operations, CFO", "CFO"),new OrgCode("FG", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("FH", "IBM CHQ, Communications", "Communications"),new OrgCode("FI", "Finance and Operations, CFO", "CFO"),new OrgCode("FK", "IBM Software Sales", "Cloud Sales"),new OrgCode("FL", "", ""),new OrgCode("FN", "", ""),new OrgCode("FO", "Global Business Services", "Cog Proc Trans"),new OrgCode("FQ", "Global Business Services", "Bus Support"),new OrgCode("FR", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("FS", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("FV", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("FW", "IBM Security", "SecuritySols"),new OrgCode("FY", "IBM Industry Platform", "WatsonFSS"),new OrgCode("FZ", "IBM Cloud", "Cloud Sales"),new OrgCode("G0", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("G1", "IBM Global Markets, Communications Sector Industry Client Teams", "GLMKT Non Brand"),new OrgCode("G2", "IBM Global Markets, Distribution Sector Industry Client Teams", "GLMKT Non Brand"),new OrgCode("G3", "IBM Global Markets, Financial Services Sector Industry Client Teams", "GLMKT Non Brand"),new OrgCode("G4", "IBM Global Markets, Public Sector Industry Client Teams", "GLMKT Non Brand"),new OrgCode("G5", "", ""),new OrgCode("G6", "IBM Global Markets, Communications Sector Integrated Accounts", "GLMKT Non Brand"),new OrgCode("GA", "IBM Global Markets, Industrial Sector Integrated Accounts", "GLMKT Non Brand"),new OrgCode("GC", "Sales Management Support", "GLMKT Non Brand"),new OrgCode("GD", "IBM Global Markets", "GLMKT Non Brand"),new OrgCode("GE", "IBM Software Sales", "SysSWSales"),new OrgCode("GF", "IBM Global Markets - Systems HW Sales", "Sys HW Sales"),new OrgCode("GI", "IBM Cloud", "Cloud Dev"),new OrgCode("GJ", "GTS,Networking Services", "Networkg Svcs"),new OrgCode("GK", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("GN", "IBM Global Markets", "GLMKT Non Brand"),new OrgCode("GP", "IBM Systems", "Systems Top"),new OrgCode("GS", "IBM Global Markets", "GLMKT Non Brand"),new OrgCode("GU", "IBM Systems", "OpenPower"),new OrgCode("H0", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("H3", "IBM Security", "SecuritySvcs"),new OrgCode("H4", "IBM Global Markets - Systems SW Sales (WW)", "SysSWSales"),new OrgCode("H5", "IBM Global Markets (Geo)", "GLMKT Non Brand"),new OrgCode("H7", "IBM Research", "Res Geo"),new OrgCode("H8", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("HC", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("HF", "Finance and Operations", "EntSvQ2C"),new OrgCode("HH", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("HI", "IBM Security", "SecuritySvcs"),new OrgCode("HJ", "Global Technology Services", "Cloud Solutions"),new OrgCode("HK", "IBM Systems", "StDev_Ar&Tech"),new OrgCode("HL", "IBM Software Sales", "CSUIP Sales"),new OrgCode("HM", "IBM CHQ, Marketing", "Marketing"),new OrgCode("HO", "Global Business Services", "Cog Proc Trans"),new OrgCode("HQ", "IBM Systems", "StDev_ClntAdv"),new OrgCode("HR", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("HS", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("HT", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("HU", "IBM Global Financing, Business Support Operations", "BusSuppOps"),new OrgCode("HZ", "IBM Global Markets - Systems SW Sales", "SysSWSales"),new OrgCode("I1", "Finance and Operations", "CIO"),new OrgCode("ID", "Finance and Operations", "GlblProcurement"),new OrgCode("II", "IBM Research", "ResMathSci"),new OrgCode("IJ", "IBM Research", "ResBusDev"),new OrgCode("IK", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("IP", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("IQ", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("IR", "IBM Industry Platform", "WatsonFSS"),new OrgCode("IS", "IBM Industry Platform", "WatsonFSS"),new OrgCode("IT", "IBM Global Markets", "GLMKT Non Brand"),new OrgCode("IU", "IBM Industry Platform", "WatsonFSS"),new OrgCode("IV", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("IX", "IBM Industry Platform", "WatsonFSS"),new OrgCode("IZ", "IBM Industry Platform", "WatsonFSS"),new OrgCode("J2", "IBM CHQ, Communications", "Communications"),new OrgCode("J3", "Finance and Operations, CFO", "CFO"),new OrgCode("J4", "Finance and Operations, CFO", "CFO"),new OrgCode("J5", "Finance and Operations, CFO", "CFO"),new OrgCode("J6", "IBM CHQ, General Counsel", "GenCounsel"),new OrgCode("J7", "IBM CHQ, Marketing", "Marketing"),new OrgCode("J8", "IBM CHQ, Strategy", "Strategy"),new OrgCode("J9", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JA", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JB", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JC", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JD", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JE", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JF", "", ""),new OrgCode("JG", "Finance and Operations", "ClientAdvOff"),new OrgCode("JJ", "IBM CHQ, General Counsel", "GenCounsel"),new OrgCode("JK", "IBM CHQ, Executive Offices", "Chair/CEO"),new OrgCode("JL", "IBM CHQ, IP", "T&IP"),new OrgCode("JM", "Finance and Operations", "CIO"),new OrgCode("JO", "Global Business Services", "Cog Proc Trans"),new OrgCode("JP", "Finance and Operations, CFO", "CFO"),new OrgCode("JR", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JS", "IBM CHQ, Human Resources", "Human Resources"),new OrgCode("JT", "IBM Cognitive Applications", "Watson M&W"),new OrgCode("JU", "Global Business Services", "Bus Support"),new OrgCode("JV", "Global Business Services", "Cog Proc Trans"),new OrgCode("JW", "IBM CHQ, General Counsel", "GenCounsel"),new OrgCode("JX", "Finance and Operations, CFO", "CFO"),new OrgCode("JY", "Finance and Operations", "ChiefAnalytOfcr"),new OrgCode("JZ", "IBM Software Sales", "Cloud Sales"),new OrgCode("KF", "", ""),new OrgCode("KJ", "", ""),new OrgCode("KK", "", ""),new OrgCode("KL", "Global Business Services", "Bus Support"),new OrgCode("KN", "Global Business Services", "Dig Strat & iX"),new OrgCode("KO", "IBM Cloud and Cognitive Software", "CloudLabSvcs"),new OrgCode("KP", "Global Business Services", "Cloud App Innov"),new OrgCode("KQ", "Global Business Services", "Cloud App Innov"),new OrgCode("KS", "Global Business Services", "Dig Strat & iX"),new OrgCode("KT", "Global Business Services", "Dig Strat & iX"),new OrgCode("KU", "IBM Systems", "Power"),new OrgCode("KV", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("KW", "Global Business Services", "Cog Proc Trans"),new OrgCode("KX", "Global Business Services", "Dig Strat & iX"),new OrgCode("KY", "Global Business Services", "Cloud App Innov"),new OrgCode("KZ", "Global Business Services", "Dig Strat & iX"),new OrgCode("L1", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("L3", "IBM Cognitive Applications", "CSU Services"),new OrgCode("L7", "IBM Industry Platform", "Strategy&Mktng"),new OrgCode("L9", "IBM CHQ, Marketing", "Marketing"),new OrgCode("LA", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("LC", "IBM Cloud and Cognitive Software", "Cloud WW Sales"),new OrgCode("LE", "IBM CHQ, General Counsel", "GenCounsel"),new OrgCode("LF", "IBM Software Sales", "Cloud Sales"),new OrgCode("LG", "IBM Cloud and Cognitive Software", "Cloud WW Sales"),new OrgCode("LH", "IBM Cloud and Cognitive Software", "Cloud WW Sales"),new OrgCode("LK", "", ""),new OrgCode("LL", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("LM", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("LN", "Finance and Operations, CFO", "CFO"),new OrgCode("LO", "", ""),new OrgCode("LP", "IBM Watson Health", "WH_Development"),new OrgCode("LT", "IBM Cognitive Applications", "Watson M&W"),new OrgCode("LU", "Global Business Services", "Bus Support"),new OrgCode("LW", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("LX", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("LY", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("LZ", "IBM Cloud and Cognitive Software", "Cloud WW Sales"),new OrgCode("M3", "Global Business Services", "Cog Proc Trans"),new OrgCode("M4", "Global Business Services", "Cloud App Innov"),new OrgCode("M7", "Global Business Services", "Cog Proc Trans"),new OrgCode("M8", "Global Business Services", "Cog Proc Trans"),new OrgCode("M9", "Global Business Services", "Cog Proc Trans"),new OrgCode("MA", "IBM CHQ, Marketing", "Marketing"),new OrgCode("MR", "IBM Security", "SecuritySvcs"),new OrgCode("MU", "GTS, GTS Japan Geo", "GTS Japan Geo"),new OrgCode("MV", "IBM Security", "SecuritySvcs"),new OrgCode("MY", "Global Business Services", "Dig Strat & iX"),new OrgCode("N0", "GTS, Optimized Services", "OptimizedSvcs"),new OrgCode("N1", "IBM Global Financing, Finance", "Fin GF"),new OrgCode("N2", "IBM Global Financing, Finance", "Fin GF"),new OrgCode("N4", "IBM Global Financing, Legal", "Legal GF"),new OrgCode("N8", "IBM Global Financing", "BusTrans GF"),new OrgCode("N9", "IBM Global Financing", "BusTrans GF"),new OrgCode("NB", "GTS, GTS Europe Geo", "GTS Europe Geo"),new OrgCode("NG", "IBM Global Financing", "Fin GF"),new OrgCode("NH", "Global Business Services", "Dig Strat & iX"),new OrgCode("NJ", "Global Business Services", "Cloud App Innov"),new OrgCode("NK", "Global Business Services", "Cloud App Innov"),new OrgCode("NL", "Global Business Services", "Cloud App Innov"),new OrgCode("NM", "Global Business Services", "Cog Proc Trans"),new OrgCode("NO", "GTS, GTS AP Geo", "GTS AP Geo"),new OrgCode("NP", "IBM Global Financing, Marketing & Strategy", "Mktg&Strategy"),new OrgCode("NQ", "GTS, GTS NA Geo", "GTS NA Geo"),new OrgCode("NS", "Global Business Services", "Cog Proc Trans"),new OrgCode("NV", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("NW", "", ""),new OrgCode("O4", "Global Business Services", "Cog Proc Trans"),new OrgCode("O5", "GTS,Solutioning", "Solutioning"),new OrgCode("O7", "IBM Global Markets", "GLMKT Non Brand"),new OrgCode("O9", "IBM Global Markets, Enterprise", "GLMKT Non Brand"),new OrgCode("OD", "Global Business Services", "Cog Proc Trans"),new OrgCode("OG", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("OH", "IBM CHQ, Marketing", "Marketing"),new OrgCode("OI", "", ""),new OrgCode("OM", "IBM Hybrid Cloud", "Data and AI"),new OrgCode("ON", "IBM Systems, Storage", "Storage"),new OrgCode("OR", "IBM Systems", "Storage"),new OrgCode("OS", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("OW", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("OX", "Global Business Services", "Dig Strat & iX"),new OrgCode("OY", "IBM Systems", "Power"),new OrgCode("OZ", "IBM Systems", "Power"),new OrgCode("P1", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("P2", "Finance and Operations", "GlblProcurement"),new OrgCode("P3", "Finance and Operations", "GlblProcurement"),new OrgCode("P4", "Finance and Operations", "GlblProcurement"),new OrgCode("P5", "Finance and Operations", "GlblProcurement"),new OrgCode("P9", "Finance and Operations", "GlblProcurement"),new OrgCode("PA", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("PD", "Finance and Operations", "GlblProcurement"),new OrgCode("PE", "IBM Cloud", "Cloud Sales"),new OrgCode("PG", "IBM Systems Software Sales", "SysSWSales"),new OrgCode("PJ", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("PK", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("PL", "IBM Systems", "StDev_ClntAdv"),new OrgCode("PN", "IBM Systems", "StDev_ShdTechOp"),new OrgCode("PO", "SO, Cross Competency Mgmt", "CrsCompMgmt"),new OrgCode("PT", "", ""),new OrgCode("PU", "Global Technology Services", "GTS Leadership"),new OrgCode("PW", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("PX", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("PY", "IBM Cloud and Cognitive Software", "CloudLabSvcs"),new OrgCode("PZ", "IBM Systems", "IBM Z"),new OrgCode("Q2", "IBM Systems", "IBM Z"),new OrgCode("Q3", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("Q4", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("Q6", "IBM Cloud and Cognitive Software", "Cloud Mktg"),new OrgCode("Q7", "IBM Cognitive Applications", "CSU Services"),new OrgCode("Q8", "IBM Cognitive Applications", "CSU Services"),new OrgCode("QB", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("QC", "", ""),new OrgCode("QD", "IBM Cognitive Applications", "DBG Top"),new OrgCode("QE", "IBM Systems", "Power"),new OrgCode("QF", "IBM Cloud and Cognitive Software", "Business Dev"),new OrgCode("QG", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("QH", "IBM Cloud and Cognitive Software", "Cloud WW Sales"),new OrgCode("QK", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("QL", "IBM Cognitive Applications", "TalentMgmtSolns"),new OrgCode("QP", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("QR", "IBM Software Sales", "SysSWSales"),new OrgCode("QS", "IBM Systems", "Sys SWS L2"),new OrgCode("QW", "IBM Systems", "GblBusPrtnrs"),new OrgCode("QX", "IBM Watson Health", "WH_Imaging"),new OrgCode("QZ", "IBM Industry Platform", "Blockchain"),new OrgCode("R0", "GTS, GTS GCG Geo", "GTS GCG Geo"),new OrgCode("R2", "IBM Research", "TechOps"),new OrgCode("R3", "IBM Research", "IPGovPgm"),new OrgCode("R4", "IBM Research", "Almaden"),new OrgCode("R5", "IBM Research", "PhySci&GovProg"),new OrgCode("R6", "", ""),new OrgCode("R7", "IBM Research", "DivTop"),new OrgCode("R9", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("RA", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("RB", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("RC", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("RD", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("RE", "Finance and Operations", "Real Estate Svc"),new OrgCode("RF", "IBM Systems", "StDev_ClntAdv"),new OrgCode("RG", "IBM Systems", "Strategy&Dev"),new OrgCode("RJ", "IBM CHQ, Marketing", "Marketing"),new OrgCode("RL", "IBM Systems", "Power"),new OrgCode("RM", "IBM Watson Health", "WH_LS_Onc&Gen"),new OrgCode("RN", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("RP", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("RQ", "IBM Cognitive Applications", "Watson M&W"),new OrgCode("RU", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("RW", "GTS, GTS LA Geo", "GTS LA Geo"),new OrgCode("S1", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("S2", "IBM Cognitive Applications", "Watson IoT"),new OrgCode("S7", "IBM Systems", "IBM Z"),new OrgCode("S9", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("SA", "IBM Systems", "Storage"),new OrgCode("SB", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("SD", "IBM Systems", "Storage"),new OrgCode("SF", "IBM Cloud and Cognitive Software", "IBM Design"),new OrgCode("SG", "IBM Cognitive Applications", "DigitalPlatform"),new OrgCode("SH", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("SI", "IBM Systems", "StDev_TechStrat"),new OrgCode("SJ", "IBM Cognitive Applications", "DEG"),new OrgCode("SK", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("SL", "IBM Cloud and Cognitive Software", "Cloud Top"),new OrgCode("SM", "IBM Cognitive Applications", "Watson IoT"),new OrgCode("SR", "IBM Systems, Middleware", "MW Other"),new OrgCode("SS", "IBM Systems, Middleware", "MW Other"),new OrgCode("SU", "Global Business Services", "Bus Support"),new OrgCode("SV", "Global Business Services", "Bus Support"),new OrgCode("SY", "IBM Research", "CaaS"),new OrgCode("SZ", "IBM Cognitive Applications", "Watson M&W"),new OrgCode("T2", "IBM Systems, Middleware", "MW Other"),new OrgCode("T3", "IBM Security", "SecuritySvcs"),new OrgCode("T4", "IBM Cognitive Applications", "Watson IoT"),new OrgCode("T6", "IBM Research", "Res Geo"),new OrgCode("T7", "IBM Cloud and Cognitive Software", "Cld Integration"),new OrgCode("T8", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("T9", "IBM Hybrid Cloud", "Cloud Platform"),new OrgCode("TC", "IBM Cognitive Applications", "Watson IoT"),new OrgCode("TD", "", ""),new OrgCode("TE", "IBM Cloud and Cognitive Software", "CloudLabSvcs"),new OrgCode("TF", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("TG", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("TH", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("TI", "IBM Cognitive Applications", "Watson IoT"),new OrgCode("TJ", "IBM Cloud and Cognitive Software", "Cloud Platform"),new OrgCode("TK", "IBM Watson Health", "WtsonHealth_L_S"),new OrgCode("TL", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("TN", "IBM Cognitive Applications", "Watson M&W"),new OrgCode("TO", "Global Business Services", "Cog Proc Trans"),new OrgCode("TP", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("TR", "IBM Software", "CSUIP Sales"),new OrgCode("TS", "IBM Global Markets, Systems HW Sales", "Sys HW Sales"),new OrgCode("TT", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("TU", "Global Business Services", "Cog Proc Trans"),new OrgCode("TX", "IBM Security", "SecClientSuc"),new OrgCode("TY", "IBM CHQ, Marketing", "Marketing"),new OrgCode("TZ", "IBM Research", "CaaS"),new OrgCode("U4", "GTS, GTS NA Geo", "GTS NA Geo"),new OrgCode("U5", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("U6", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("U8", "Finance and Operations", "CST"),new OrgCode("UA", "IBM Systems", "StDev_TechStrat"),new OrgCode("UC", "IBM Systems", "IBM Z"),new OrgCode("UE", "IBM Systems", "StDev_ClntAdv"),new OrgCode("UG", "IBM Systems", "Sys HWS L2"),new OrgCode("UH", "IBM Systems", "Storage"),new OrgCode("UI", "IBM Systems", "StDev_EntSys"),new OrgCode("UK", "IBM Systems", "Power"),new OrgCode("UQ", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("UR", "IBM Cognitive Applications", "Collab Sols"),new OrgCode("UT", "", ""),new OrgCode("UV", "Global Business Services", "Cog Proc Trans"),new OrgCode("V0", "Global Business Services", "Bus Support"),new OrgCode("VJ", "IBM Global Financing, Finance", "Fin GF"),new OrgCode("VK", "IBM Global Financing, Finance", "Fin GF"),new OrgCode("VN", "IBM Global Financing, Finance", "Fin GF"),new OrgCode("VO", "IBM Global Financing, Sales", "Sales"),new OrgCode("VP", "IBM Global Financing, Sales", "Sales"),new OrgCode("VQ", "IBM Global Financing, Sales", "Sales"),new OrgCode("VT", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("VU", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("VV", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("VW", "Global Business Services", "Cloud App Innov"),new OrgCode("VX", "Global Business Services", "Cloud App Innov"),new OrgCode("W9", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("WA", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("WH", "Finance and Operations", "GlblProcurement"),new OrgCode("WK", "Finance and Operations", "EntSvQ2C"),new OrgCode("WU", "Global Business Services", "Cog Proc Trans"),new OrgCode("WV", "IBM Cloud and Cognitive Software", "Data and AI"),new OrgCode("WY", "IBM Industry Platform", "WatsonFSS"),new OrgCode("X8", "IBM OS Deals", "CHQ / Other"),new OrgCode("X9", "IBM IP", "CHQ / Other"),new OrgCode("XA", "Transformation and Operations", "EntSvQ2C"),new OrgCode("XB", "Transformation and Operations", "EntSvQ2C"),new OrgCode("XC", "Transformation and Operations", "EntSvQ2C"),new OrgCode("XG", "Finance and Operations", "EntSvGblSCQ&I"),new OrgCode("XH", "", ""),new OrgCode("XI", "Transformation and Operations", "EntSvQ2C"),new OrgCode("XJ", "Finance and Operations", "EntSvBusInt&Tns"),new OrgCode("XM", "Finance and Operations", "EntSvBusInt&Tns"),new OrgCode("XO", "Finance and Operations", "EntSvBusInt&Tns"),new OrgCode("XR", "Finance and Operations", "EntSvBusInt&Tns"),new OrgCode("XS", "Finance and Operations", "EntSvQ2C"),new OrgCode("XT", "Finance and Operations", "EntSvQ2C"),new OrgCode("XU", "Finance and Operations", "CDO"),new OrgCode("XV", "IBM Watson Health", "WH_Top"),new OrgCode("XY", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("Y2", "GTS, Solutions, Del & Transf", "SolsDel&Trans"),new OrgCode("Y3", "IBM Education", "Education"),new OrgCode("Y4", "Finance and Operations", "EntSvBusInt&Tns"),new OrgCode("Y8", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("Y9", "Transformation and Operations", "EntSvQ2C"),new OrgCode("YA", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("YD", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("YH", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("YJ", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("YK", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("YM", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("YS", "IBM Systems", "StDev_Mfg&GE"),new OrgCode("YW", "", ""),new OrgCode("Z1", "Global Business Services", "Bus Support"),new OrgCode("Z9", "Finance and Operations", "BusArch Trans"),new OrgCode("ZA", "Global Business Services", "Cloud App Innov"),new OrgCode("ZB", "IBM Digital Sales", "Digital Sales"),new OrgCode("ZF", "GTS, Resiliency Services", "ResiliencySvcs"),new OrgCode("ZI", "IBM Global Markets, Works Council", "Works Council"),new OrgCode("ZJ", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("ZL", "IBM Cognitive Applications", "WatsonCustEngag"),new OrgCode("ZM", "IBM Global Financing, PC Vend Fin", "Fin GF"),new OrgCode("ZT", "Global Business Services", "Cog Proc Trans"),new OrgCode("ZW", "Global Business Services", "Cog Proc Trans"),new OrgCode("ZX", "Finance and Operations", "EntSvQ2C"));
		//appDao.deleteAllOrgCode();
		int sum=0;
		for(OrgCode entity:list) {
			int i=appDao.saveInLocalDB(entity.getHrOrgCode(), entity.getHrOrgDisplay(), entity.getHrUnitId());
			sum+=i;
		}
		System.out.println(sum+" inserted successful!");
	}
	@Override
	public List<OrgCode> getAllOrgCode() {
		return appDao.getAllOrgCode();
	}
	@Override
	public List<EntityInfo> buildEntityInfo(String serialCcs,Map<String, Object> maps,List<OrgCode> orgCodes) {
		List<EntityInfo> list=new ArrayList<EntityInfo>();
		List<EntityInfoChain> chainList=new ArrayList<EntityInfoChain>();
		
		Map<String, String> mgrMap=new HashMap<String, String>();
		Map<String, String> funLeaderMap=new HashMap<String, String>();
		Map<String, Chain> chainMap=new HashMap<String, Chain>();
		Map<String, OrgCode> orgCodeMap=new HashMap<String,OrgCode>();
		Map<String, String> jobResponses=new HashMap<String, String>();
//		Map<String, String> jobResponses=new HashMap<String, String>();

		mgrMap=(Map<String, String>) maps.get("mgrMap");
		funLeaderMap=(Map<String, String>) maps.get("funLeaderMap");
		chainMap=(Map<String, Chain>) maps.get("allEntity");
		jobResponses=(Map<String, String>) maps.get("jopResponses");
		System.out.println(",,,,,,,,,,"+jobResponses);
		
		orgCodeMap=utils.listToMap((Collection<OrgCode>) orgCodes);
		String[] uIdStr=serialCcs.split(",");

		//prepare the data for Sheet 1
		for(String serialCc:uIdStr) {
			Chain chain=chainMap.get(serialCc);
			if(chain!=null) {
				OrgCode orgCode=orgCodeMap.get(chain.getBpOrgCode());											
				list.add(new EntityInfo(serialCc, chain.getUserNotes(), chain.getFunLeaderNotes(), chain.getFunLeader(), chain.getMgrNotes(),
									//set jobResponse as ""
				chain.getManager(), jobResponses.get(serialCc), chain.getBpOrgCode(), orgCode.getHrOrgDisplay(), orgCode.getHrUnitId()));
			}else {
//				throw new RuntimeException("The serial&cc not match...");
				list.add(new EntityInfo(serialCc, "", "", "", "", "","", ""));
				System.out.println("The serial&cc not match.....");
			}
		}
		
		return list;
	}
	@Override
	public Map<String, String> getJobResponses(List<String> serialCcs) {
		return httpClient.requestBluePageForMap(serialCcs);
	}
	
	
	
}
