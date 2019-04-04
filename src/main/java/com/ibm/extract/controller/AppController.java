package com.ibm.extract.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ibm.extract.service.AppService;
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
	public ResponseEntity<byte[]> getUpLineChain(@RequestParam("serialCcs")String serialCcs,@RequestParam("appCode")String appCode,HttpSession session) throws IOException{
		Instant start=Instant.now();
		List<List<Object>> lists=new ArrayList<>();
		
		// 1. 将 serialCC 转化为 list
//		List<String> serialccList = Arrays.asList(serialCcs.split(","));
//		// 2. 拆分 list
//		int size = serialccList.size();
//		// 最多启用 10 个线程
//		int maxNumber = 9, minSize = 20;
//		Map<String, String> resultMap = new HashMap<String, String>();
//		if(size > minSize) {
//			int partNumber = size/maxNumber;
//			System.out.println("partNumber: "+partNumber);
//			List<List<String>> seriaLists = Lists.partition(serialccList, partNumber);
//			System.out.println("^^^^^^^^^^^ "+seriaLists.size());
//			CountDownLatch latch = new CountDownLatch(seriaLists.size());
//			System.out.println("将启动 : " + seriaLists.size() + " 个线程");
//			for(int i = 0; i < seriaLists.size(); i++) {
//				BatchThread batchThread = new BatchThread(latch, httpClient, resultMap, seriaLists.get(i));
//				new Thread(batchThread).start();
//			}
//			latch.await();
//		} else {
//			resultMap = httpClient.requestBluePageForMap(serialccList);
//		}
//		
//		System.out.println(resultMap);
//		System.out.println("******************" + resultMap.size());
		
		
		System.out.println("appCode: "+appCode);
		
		Map<String, Object> maps=appService.getUpLineChain(serialCcs);
		
//		utils.startBatchThread(AppService.getAllUID());
		System.out.println("AllUID: "+AppService.getAllUID());
		
		List<OrgCode> chains=appService.getAllOrgCode();
		
		
		
		
		List<EntityInfo> entityInfos=appService.buildEntityInfo(serialCcs,maps,chains);
		for(EntityInfo entity:entityInfos) {
			System.out.println(entity);
		}
		System.out.println("=========================================================");
		Map<String, Chain> chainMap=(Map<String, Chain>) maps.get("allEntity");
		for(Map.Entry<String, Chain> entry:chainMap.entrySet()) {
			System.out.println(entry.getValue());
		}
		
		//get the upline's linked list
		List<LinkedHashSet<String>> mgrLink=utils.buildMgrLink(entityInfos,maps);
		System.out.println(mgrLink);
		List<LinkedHashSet<String>> funLeaderLink=utils.buildFunLeaderLink(entityInfos,maps);
		System.out.println(funLeaderLink);
		
		List<EntityInfoChain> entityInfoChains=utils.buildEntityInfoChain(mgrLink, funLeaderLink, maps,chains);
		System.out.println(entityInfoChains);
		
//		lists.addAll(entityInfos);
		
		utils.ExcelWriter(appCode,entityInfos,entityInfoChains);
		System.out.println("Spent time: "+Duration.between(start, Instant.now()));
		
		 ServletContext context=session.getServletContext();
         InputStream in=new FileInputStream("C:\\Users\\XiaoFengJiang\\Desktop\\"+appCode+".xlsx");
         byte[] body=new byte[in.available()];
         in.read(body);
         HttpHeaders headers=new HttpHeaders();
         headers.add("Content-Disposition", "attachment;filename="+appCode+".xlsx");
         HttpStatus status=HttpStatus.OK;
         ResponseEntity<byte[]> response=new ResponseEntity<byte[]>(body, headers, status);
		return response;
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
