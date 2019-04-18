package com.ibm.extract.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibm.extract.config.Configure;
import com.ibm.extract.model.App;
import com.ibm.extract.model.Chain;
import com.ibm.extract.model.EntityInfo;
import com.ibm.extract.model.EntityInfoChain;
import com.ibm.extract.model.OrgCode;
import com.ibm.extract.service.impl.IAppService;
import com.ibm.extract.utils.HttpClient;
import com.ibm.extract.utils.Utils;

@Controller
public class AppController {
	@Resource
	private IAppService appService;
	@Resource
	private HttpClient httpClient;
	@Resource
	private Configure configure;
	@Resource
	private Utils utils;
	
	
	@RequestMapping("/listapp")
	public String listApp(Map<String,Object> map) {
		List<App> list=appService.getAllApp();
		map.put("applications", list);
		System.out.println(list);
		return "listapp";
	}
	@RequestMapping("/listuser")
	public String getRequests(String appCode,Model model) {
//		List<String> serialCcs=appService.getRequests(appCode);
		List<String> serialCcs=appService.getRequestsBySpecificApp(appCode);
		System.out.println("serialCcs: "+serialCcs);
		if(serialCcs.size()!=0) {
			List<Chain> baseChains= appService.getChains(serialCcs);
			System.out.println("baseChains: "+baseChains);
			model.addAttribute("baseChains", baseChains);
			model.addAttribute("appCode", appCode);
		}else {
			model.addAttribute("emptyApp", "The app ["+appCode+"] does not contain any GRANTED STATUS request");
		}
		return "listuser";
	}
//	@ResponseBody
	@RequestMapping("/selectUsers")
	public String getUpLineChain(@RequestParam("serialCcs")String serialCcs,@RequestParam("appCode")String appCode) throws IOException{
		Instant start=Instant.now();
		
		System.out.println("appCode: "+appCode);
		
		Map<String, Object> maps=appService.getUpLineChain(serialCcs);
		
		List<OrgCode> chains=appService.getAllOrgCode();
		
		List<EntityInfo> entityInfos=appService.buildEntityInfo(serialCcs,maps,chains);
//		for(EntityInfo entity:entityInfos) {
//			System.out.println(entity);
//		}
//		System.out.println("=========================================================");
//		Map<String, Chain> chainMap=(Map<String, Chain>) maps.get("allEntity");
//		for(Map.Entry<String, Chain> entry:chainMap.entrySet()) {
//			System.out.println(entry.getValue());
//		}
		
		//get the upline's linked list
		List<LinkedHashSet<String>> mgrLink=utils.buildMgrLink(entityInfos,maps);
		System.out.println(mgrLink);
		List<LinkedHashSet<String>> funLeaderLink=utils.buildFunLeaderLink(entityInfos,maps);
		System.out.println(funLeaderLink);
		
		List<EntityInfoChain> entityInfoChains=utils.buildEntityInfoChain(mgrLink, funLeaderLink, maps,chains);
		System.out.println(entityInfoChains);
		
		
		utils.ExcelWriter(appCode,entityInfos);//,entityInfoChains
		utils.ExcelWriter(appCode, entityInfoChains);
		System.out.println("Spent time: "+Duration.between(start, Instant.now()));
		 
		return "return";
	}
	@RequestMapping("/multiAppSelected")
	public String getMultiapp(@RequestParam("multiApp")String multiApp) {
		Instant start=Instant.now();

		Map<String, List<String>> appmaps=new HashMap<>();
		Set<String> allUsers=new HashSet<>();
		
		
		
		appmaps=appService.getRequestsByMultiApp(multiApp);
		allUsers=utils.getMultiAppUsers(appmaps);
		String serialCcs=String.join(",", allUsers);
		System.out.println(serialCcs);
		
		Map<String, Object> maps=appService.getUpLineChain(serialCcs);
		List<OrgCode> chains=appService.getAllOrgCode();
		
		
		
		for(Map.Entry<String, List<String>> entry:appmaps.entrySet()) {
			String singleApp=entry.getKey();
			String appSerialCc=String.join(",", entry.getValue());
			
			List<EntityInfo> entityInfos=appService.buildEntityInfo(appSerialCc,maps,chains);
			List<LinkedHashSet<String>> mgrLink=utils.buildMgrLink(entityInfos,maps);
			System.out.println(mgrLink);
			List<LinkedHashSet<String>> funLeaderLink=utils.buildFunLeaderLink(entityInfos,maps);
			System.out.println(funLeaderLink);
			
			List<EntityInfoChain> entityInfoChains=utils.buildEntityInfoChain(mgrLink, funLeaderLink, maps,chains);
			System.out.println(entityInfoChains);
			
			utils.ExcelWriter(singleApp,entityInfos);//,entityInfoChains
			utils.ExcelWriter(singleApp, entityInfoChains);
			
			
		}
		
		
		
		System.out.println("Spent time: "+Duration.between(start, Instant.now()));

		return "return";
	}
	
	@GetMapping("/getBpCode")
	@ResponseBody
	public String testHttpClient() {
		List<OrgCode> list = appService.getBpCodeByApi();
		appService.saveInLocalDB(list);
//		List<OrgCode> list=appService.getAllOrgCode();
//		System.out.println(list);
		return "return";
	}
}
