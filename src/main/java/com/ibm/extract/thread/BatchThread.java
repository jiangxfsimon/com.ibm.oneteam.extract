package com.ibm.extract.thread;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.ibm.extract.utils.HttpClient;

public class BatchThread implements Runnable {
	private CountDownLatch latch;
	private HttpClient httpClient;
	private Map<String, String> result;
	private List<String> seriaList;
	public BatchThread(CountDownLatch latch, HttpClient httpClient, Map<String, String> result, List<String> seriaList) {
		this.latch = latch;
		this.httpClient = httpClient;
		this.result = result;
		this.seriaList = seriaList;
	}
	public CountDownLatch getLatch() {
		return latch;
	}
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public Map<String, String> getResult() {
		return result;
	}
	public void setResult(Map<String, String> result) {
		this.result = result;
	}
	public List<String> getSeriaList() {
		return seriaList;
	}
	public void setSeriaList(List<String> seriaList) {
		this.seriaList = seriaList;
	}
	@Override
	public void run() {
		result.putAll(httpClient.requestBluePageForMap(seriaList));
		if(null != latch) {latch.countDown();}
	}
}
