package com.ibm.extract.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.ibm.extract.config.Configure;
import com.ibm.extract.enums.DateFormat;
import com.ibm.extract.enums.ErrorCode;
import com.ibm.extract.exception.CommonException;
import com.ibm.extract.model.Chain;
import com.ibm.extract.model.EntityInfo;
import com.ibm.extract.model.EntityInfoChain;
import com.ibm.extract.model.OrgCode;
import com.ibm.extract.thread.BatchThread;
import com.ibm.extract.thread.FetchBpByMultiThread;


/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 公共工具类
 */
@Component
public class Utils {
	@Resource
	private Configure configure;
	@Resource
	private HttpClient httpClient;
	
	private static final int threadNum=10;
	
	
	public static final String[] AccessFor= {"APP Name","CNUM","Employee LN ID","Employee LN ID(GET)","Employee LN ID(BP)","JobResponsibilities:","HrOrganizationDisplay:","HrUnit ID:"};
	public static final String[] UplineFor= {"APP Name","CNUM","Employee LN ID","Employee LN ID(GET)","Employee LN ID(BP)","Chain","JobResponsibilities:","HrOrganizationDisplay:","HrUnit ID:"};
	
	public void ExcelWriter(String appCode,List<EntityInfo> list1,List<EntityInfoChain> list2)  {
		Instant start=Instant.now();
		int[] sheet1CellLength=new int[AccessFor.length];
		int[] sheet2CellLength=new int[UplineFor.length];
		SXSSFWorkbook wb=null;
		FileOutputStream out =null;
		
		try {
			wb = new SXSSFWorkbook(200);
			out= new FileOutputStream("C:\\Users\\XiaoFengJiang\\Desktop\\"+appCode+".xlsx");
			
			SXSSFSheet sh = wb.createSheet("AccessFor"+appCode);
			sh.createFreezePane(0,1);
//			sh.trackAllColumnsForAutoSizing();
			
			
			CellStyle style=wb.createCellStyle();
			style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			//write the title
			Row row0=sh.createRow(0);
			for(int i=0;i<AccessFor.length;i++) {
				Cell cell =row0.createCell(i);
				cell.setCellValue(AccessFor[i]);
				cell.setCellStyle(style);
			}
			
			EntityInfo entityInfo=null;
			for(int rownum = 0; rownum < list1.size(); rownum++){
				entityInfo=(EntityInfo) list1.get(rownum);
				
			    Row row = sh.createRow(rownum+1);
			    
			    for(int cellnum = 0; cellnum < AccessFor.length; cellnum++){
//			    	sh.autoSizeColumn(cellnum);
			        Cell cell = row.createCell(cellnum);
//			        cell.setCellValue(appCode);
			        
			        if(cellnum==0) {
			        	cell.setCellValue(appCode);
			        	sh.setColumnWidth(0, appCode.length()*256);
			        }else {
//			        	sheet1CellLength
			        	System.out.println(entityInfo.getAttributeByIndex(cellnum).length()*256);
				        cell.setCellValue(entityInfo.getAttributeByIndex(cellnum));
				        sh.setColumnWidth(cellnum, entityInfo.getAttributeByIndex(cellnum).length()*256);
			        }
			    }
			}
			
			
			SXSSFSheet sh1 = wb.createSheet("UplineFor"+appCode);
			sh1.createFreezePane(0,1);
			
		
			
			//write the title
			Row nextRow0=sh1.createRow(0);
			for(int i=0;i<UplineFor.length;i++) {
				Cell cell =nextRow0.createCell(i);
				cell.setCellValue(UplineFor[i]);
				cell.setCellStyle(style);
			}
			EntityInfoChain entityInfoChain=null;
			for(int rownum = 0; rownum < list2.size(); rownum++){
				entityInfoChain= (EntityInfoChain) list2.get(rownum);
				
			    Row row = sh1.createRow(rownum+1);
			    System.out.println("Entity: "+entityInfoChain);
			    for(int cellnum = 0; cellnum < UplineFor.length; cellnum++){
//			    	sh.autoSizeColumn(cellnum);
			        Cell cell = row.createCell(cellnum);
//			        cell.setCellValue(appCode);
			        
			        if(cellnum==0) {
			        	cell.setCellValue(appCode);
//			        	sh.setColumnWidth(0, appCode.length()*256);
			        }else {
//			        	System.out.println(":: "+entityInfoChain.getAttributeByIndex(cellnum));
//			        	System.out.println(entityInfoChain.getAttributeByIndex(cellnum).length()*256);
				        cell.setCellValue(entityInfoChain.getAttributeByIndex(cellnum));
//				        sh.setColumnWidth(cellnum, entityInfoChain.getAttributeByIndex(cellnum).length()*256);
			        }
			    }
			}
			
			wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(wb!=null) {
				// dispose of temporary files backing this workbook on disk
		        wb.dispose();
		        try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println(Duration.between(start, Instant.now()));
			System.out.println("Done");
		}
        
        
	}
	public Map<String, String> startBatchThread(String allSerialCcs) {
		allSerialCcs=allSerialCcs.replaceAll("'", "");
		// 1. 将 serialCC 转化为 list
		List<String> serialccList = Arrays.asList(allSerialCcs.split(","));
		// 2. 拆分 list
		int size = serialccList.size();
		// 最多启用 10 个线程
		int maxNumber = 9, minSize = 20;
		Map<String, String> resultMap = new HashMap<String, String>();
		if(size > minSize) {
			int partNumber = size/maxNumber;
			System.out.println("partNumber: "+partNumber);
			List<List<String>> seriaLists = Lists.partition(serialccList, partNumber);
			System.out.println("^^^^^^^^^^^ "+seriaLists.size());
			CountDownLatch latch = new CountDownLatch(seriaLists.size());
			System.out.println("将启动 : " + seriaLists.size() + " 个线程");
			for(int i = 0; i < seriaLists.size(); i++) {
				BatchThread batchThread = new BatchThread(latch, httpClient, resultMap, seriaLists.get(i));
				new Thread(batchThread).start();
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			resultMap = httpClient.requestBluePageForMap(serialccList);
		}
		
//		System.out.println(resultMap);
		System.out.println("******************" + resultMap.size());
		return resultMap;
	}
	
	
	public Map<String, String> enableThread(String serialCcs) {
		FetchBpByMultiThread fetchBp=null;
		Map<String, String> map=new HashMap<String, String>();
		ExecutorService pool = Executors.newFixedThreadPool(threadNum);
//		List<FutureTask<Map<String,String>>> result=List<FutureTask<Map<String,String>>>
		List<Future<Map<String,String>>> futures=new ArrayList<Future<Map<String,String>>>();
		List<String> serialCcList=Arrays.asList(serialCcs.split(","));
		List<List<String>> lists=SplitList(serialCcList, threadNum);
		
		for(int i=0;i<threadNum;i++) {
			Future<Map<String,String>> future = pool.submit(
					fetchBp=new FetchBpByMultiThread(httpClient, lists.get(i))
			);
			futures.add(future);
		}
		
		for (Future future : futures) {
			try {
				map.putAll((Map<String,String>) future.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		pool.shutdown();
		return map; 
		 
	}
	
	public static <T> List<List<T>> SplitList(List<T> source, int n) {
		List<List<T>> result = new ArrayList<List<T>>();
		int remaider = source.size() % n; // (先计算出余数)
		int number = source.size() / n; // 然后是商
		int offset = 0;// 偏移量
		for (int i = 0; i < n; i++) {
			List<T> value = null;
			if (remaider > 0) {
				value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
				remaider--;
				offset++;
			} else {
				value = source.subList(i * number + offset, (i + 1) * number + offset);
			}
			result.add(value);
		}
		return result;
	}
	public List<String> iteration(LinkedHashSet<String> link){
		List<String> list=new ArrayList<>();
		Iterator<String> it = link.iterator();
		while(it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
	
	public List<List<String>> linkedSetToArray(List<LinkedHashSet<String>> mgrLink,List<LinkedHashSet<String>> funLeaderLink){
		List<List<String>> mergedList=new ArrayList<>();
		if(mgrLink.size()==funLeaderLink.size()) {
//			List<String> mgrSingleLink
			for(int i=0;i<mgrLink.size();i++) {
				LinkedHashSet<String> link1=mgrLink.get(i);
				List<String> list1=iteration(link1);
				LinkedHashSet<String> link2=funLeaderLink.get(i);
				List<String> list2=iteration(link2);
				mergedList.add(list1);
				mergedList.add(list2);
			}
		}else {
			throw new RuntimeException("The mgrLink size is not equal funLeaderLink size");
		}
		
		return mergedList;
	}
	
	public List<EntityInfoChain> buildEntityInfoChain(List<LinkedHashSet<String>> mgrLink,List<LinkedHashSet<String>> funLeaderLink,Map<String, Object> maps,List<OrgCode> orgCodes){
		List<EntityInfoChain> list=new ArrayList<EntityInfoChain>();
//		System.out.println("mgrLink: "+mgrLink.size()+"  funLeaderLink: "+funLeaderLink.size());
		Map<String, Chain> chainMap=(Map<String, Chain>) maps.get("allEntity");
		Map<String, OrgCode> orgCodeMap=listToMap((Collection<OrgCode>) orgCodes);
		Map<String, String> jobResponses=(Map<String, String>) maps.get("jopResponses");

		List<List<String>> mergedList=linkedSetToArray(mgrLink, funLeaderLink);
		for(int i=0;i<mergedList.size();i++) {
			int prefix=i/2+1;
			String suffix="";
			if(i%2==0) {
				suffix="BP";
			}else {
				suffix="GET";
			}
			List<String> link=mergedList.get(i);
			EntityInfoChain elEntityInfoChain=null;
			for(int y=0;y<link.size();y++) {
				String serialCc=link.get(y);
				//The manager or function leader is INVALD, so it should not put in elEntityInfoChain
				if(serialCc.startsWith("INVALD")) continue;
				
				Chain chain=chainMap.get(serialCc);
				elEntityInfoChain=new EntityInfoChain(chain.getUser(), chain.getUserNotes(), chain.getFunLeaderNotes(), chain.getFunLeader(), chain.getMgrNotes(), 
				chain.getManager(), jobResponses.get(serialCc), chain.getBpOrgCode(), orgCodeMap.get(chain.getBpOrgCode()).getHrOrgDisplay(),  orgCodeMap.get(chain.getBpOrgCode()).getHrUnitId());
				if(y==0) {
					elEntityInfoChain.setChain(prefix+" ");
				}else {
					elEntityInfoChain.setChain(prefix+" "+(y+1)+suffix);
				}
				list.add(elEntityInfoChain);
			}
		}
		
		
		
//		for(LinkedHashSet<String> link: mgrLink) {
//			int linkElementNum=2;
//			Iterator<String> it = link.iterator();
//			EntityInfoChain elEntityInfoChain=null;
//			while(it.hasNext()) {
//				Chain chain=chainMap.get(it.next());
//				elEntityInfoChain=new EntityInfoChain(chain.getUser(), chain.getUserNotes(), chain.getFunLeaderNotes(), chain.getFunLeader(), chain.getMgrNotes(), 
//				chain.getManager(), "", chain.getBpOrgCode(), orgCodeMap.get(chain.getBpOrgCode()).getHrOrgDisplay(),  orgCodeMap.get(chain.getBpOrgCode()).getHrUnitId());
//				elEntityInfoChain.setChain(linkNum+" "+linkElementNum+"BP");
//				list.add(elEntityInfoChain);
//				linkElementNum++;
//			}
//			linkNum++;
//		}
		
		return list;
	}
	
	
	//get the BP LinkedHashSet
	public List<LinkedHashSet<String>> buildMgrLink(List<EntityInfo> list,Map<String, Object> maps) {
		List<LinkedHashSet<String>> mgrLink=new ArrayList<LinkedHashSet<String>>();
		
		Map<String, String> mgrMap=new HashMap<String, String>();
		mgrMap=(Map<String, String>) maps.get("mgrMap");
		for(EntityInfo entity:list) {
			LinkedHashSet<String> mgrChain=new LinkedHashSet<String>();
			if(entity.getCnum().equals(entity.getBpMgrCnum())) {
				mgrChain.add(entity.getCnum());
				continue;
			}
			mgrChain.add(entity.getCnum());
			String bpMgrCnum=entity.getBpMgrCnum();
			mgrChain.add(bpMgrCnum);
			while(true) {
				String nextBpMgrCnum=mgrMap.get(bpMgrCnum);
//				System.out.println("nextBpMgrCnum: "+nextBpMgrCnum);
				if(null==nextBpMgrCnum||nextBpMgrCnum.equals(bpMgrCnum) || nextBpMgrCnum.equals("")) break;
				mgrChain.add(nextBpMgrCnum);
				bpMgrCnum=nextBpMgrCnum;
			}
			mgrLink.add(mgrChain);
		}
		return mgrLink;
	}
	//get the function leader LinkedHashSet
	public List<LinkedHashSet<String>> buildFunLeaderLink(List<EntityInfo> list,Map<String, Object> maps) {
		List<LinkedHashSet<String>> funLeaderLink=new ArrayList<LinkedHashSet<String>>();
		
		Map<String, String> funLeaderMap=(Map<String, String>) maps.get("funLeaderMap");
		Map<String, Chain> chainMap=(Map<String, Chain>) maps.get("allEntity");
		Map<String, String> mgrMap=(Map<String, String>) maps.get("mgrMap");

//		System.out.println("chainMap: "+chainMap);
		
		for(EntityInfo entity:list) {
//			System.out.println("The user's serial&Cc： "+entity.getCnum());
			LinkedHashSet<String> funLeaderChain=new LinkedHashSet<String>();
			if(entity.getCnum().equals(entity.getFunLeaderCnum())) {
				funLeaderChain.add(entity.getCnum());
				continue;
			}
			funLeaderChain.add(entity.getCnum());
			String funLeaderCnum=entity.getFunLeaderCnum();
			funLeaderChain.add(funLeaderCnum);
			while(true) {
//				System.out.println("funLeaderCnum: "+funLeaderCnum);
				Chain chain=chainMap.get(funLeaderCnum);
				if(chain!=null) {
					if(chain.getUser().equals(chain.getFunLeader())) break;
					funLeaderChain.add(chain.getFunLeader());
					funLeaderCnum=chain.getFunLeader();
				}else {
					System.out.println("The Function Leader is not found....");
					break;
				}
//				System.out.println("nextFunLeader: "+nextFunLeaderCnum);
//				if(nextFunLeaderCnum.equals(funLeaderCnum) || nextFunLeaderCnum.equals("")) break;
//				funLeaderChain.add(nextFunLeaderCnum);
//				funLeaderCnum=nextFunLeaderCnum;
			}
			funLeaderLink.add(funLeaderChain);
		}
		return funLeaderLink;
	}
	
	public <T> Map<String, T> listToMap(Collection<T> collection){
		Map<String, T> map=new HashMap<String,T>();
//			 Chain chain=(Chain)collection;
			 for(T t:collection) {
					if(t instanceof Chain) {
						Chain chainT=(Chain)t;
						map.put(chainT.getUser(), (T) chainT);
					}else if(t instanceof OrgCode) {
						OrgCode orgCodeT=(OrgCode)t;
						map.put(orgCodeT.getHrOrgCode(), (T) orgCodeT);
					}
				}
		return map;
	}
	
	public Set<String> mapToSet(Map<String, String> arg0,Map<String, String>arg1){
		Set<String> allUId=new HashSet<String>();
		allUId.addAll(arg0.keySet());
		allUId.addAll(arg0.values());
		allUId.addAll(arg1.keySet());
		allUId.addAll(arg1.values());
		System.out.println("All Uid: "+allUId);
		return allUId;
	}
	
	public OrgCode stringToOrgCode(String orgCode,String result) {
		OrgCode org=null;
		Pattern count=Pattern.compile("\"count\": (\\d)");
		Matcher matcher = count.matcher(result);
		Pattern elements=Pattern.compile("\"hrOrganizationDisplay\", \"value\": \\[ \"(.*?)\" \\] \\},\\{ \"name\": \"hrunitid\", \"value\": \\[ \"(.*?)\" \\]");
		Matcher elementsMatch=elements.matcher(result);
		matcher.find();
		if(Integer.parseInt(matcher.group(1))!=0) {
			while(elementsMatch.find()) {
				org=new OrgCode(orgCode, elementsMatch.group(1), elementsMatch.group(2));
			}
		}else {
			org=new OrgCode(orgCode, "", "");
		}
		return org;
	}
	
	
	public String removeSpecialEmp(List<String> originalList,String regex) {
		String origialString=listToString(originalList);
		origialString=origialString.replaceAll(regex, "");
		String formattedStr=origialString.replaceAll(",{2,}", ",");
		formattedStr=formattedStr.replaceAll("(^,|,$)", "");
		return formattedStr;
	}
	
	public String listToString(Collection<String> list) {
		String string=null;
		if(list!=null && list.size()!=0) {
			string=String.join("','",list);
		}
		return "'"+string+"'";
	}
	
	/**
	 * @param date
	 *            日期
	 * @param pattern
	 *            格式
	 * @return 格式化日期后的字符串
	 */
	public String dateFormat(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * @param pattern
	 *            格式
	 * @return 格式化当前日期的字符串
	 */
	public String dateFormat(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
	}

	/**
	 * @param filePath
	 *            文件完整路径
	 * @return 文件名
	 */
	public String getFileNameFromPath(String filePath) {
		return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
	}

	/**
	 * @param fileName
	 *            文件名
	 * @return 文件的格式
	 */
	public String getFileSuffix(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."), fileName.length());
	}

	/**
	 * @param file
	 *            前端的文件对象
	 * @return 上传文件前后的信息
	 */
	public Map<String, Object> upload(MultipartFile file) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String fileOriginName = file.getOriginalFilename();
			String fileSuffix = getFileSuffix(fileOriginName);
			String fileCurrentName = dateFormat(DateFormat.dateFormat1.value()) + fileSuffix;
			String fileRealPath = configure.getUploadFilePath() + "/" + fileCurrentName;

			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(fileRealPath));

			result.put("originalName", fileOriginName);
			result.put("currentName", fileCurrentName);
			result.put("fileSuffix", fileSuffix);
			result.put("realPath", fileRealPath);
		} catch (IOException e) {
			throw new CommonException(ErrorCode.fileUploadError);
		}
		return result;
	}

	/**
	 * @param filePath
	 *            文件完整路径
	 * @param response
	 *            HttpServletResponse
	 */
	public void downloadLocal(String filePath, HttpServletResponse response) {
		InputStream inStream;
		try {
			String fileName = getFileNameFromPath(filePath);
			inStream = new FileInputStream(filePath);
			response.reset();
			response.setContentType("bin");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			byte[] b = new byte[1024];
			int len;
			while ((len = inStream.read(b)) > 0) {
				response.getOutputStream().write(b, 0, len);
			}
			inStream.close();
		} catch (FileNotFoundException e) {
			throw new CommonException(ErrorCode.fileIsNotExistError);
		} catch (IOException e) {
			throw new CommonException(ErrorCode.closeIOError);
		}
	}
	
	/**
	 * @param ios
	 *            io 流
	 * @desc 关闭一个或多个 IO 流
	 */
	public void closeIO(Closeable... ios) {
		try {
			for (Closeable io : ios) {
				if (null != io) {
					io.close();
				}
			}
		} catch (IOException e) {
			throw new CommonException(ErrorCode.closeIOError);
		}
	}
}
