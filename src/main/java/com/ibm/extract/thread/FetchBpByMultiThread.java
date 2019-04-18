package com.ibm.extract.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ibm.extract.utils.HttpClient;

public class FetchBpByMultiThread implements Callable<Map<String, String>>{
	private HttpClient httpClient;
	private List<String> serialCcs;
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public List<String> getSerialCcs() {
		return serialCcs;
	}
	public void setSerialCcs(List<String> serialCcs) {
		this.serialCcs = serialCcs;
	}
	public FetchBpByMultiThread(HttpClient httpClient, List<String> serialCcs) {
		super();
		this.httpClient = httpClient;
		this.serialCcs = serialCcs;
	}


	@Override
	public Map<String, String> call() throws Exception {
		Map<String, String> map=new HashMap<String, String>();
		Map<String, String>  get=httpClient.requestBluePageForMap(serialCcs);
//		System.out.println("get: "+get);
		map.putAll(get);
		return map;
	}

}
