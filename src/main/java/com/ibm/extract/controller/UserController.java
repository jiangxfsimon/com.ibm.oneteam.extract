package com.ibm.extract.controller;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibm.extract.config.Configure;
import com.ibm.extract.enums.ErrorCode;
import com.ibm.extract.enums.Request;
import com.ibm.extract.exception.CommonException;
import com.ibm.extract.model.App;
import com.ibm.extract.model.User;
import com.ibm.extract.service.impl.IUserService;
import com.ibm.extract.utils.HttpClient;

@Controller
@RequestMapping("/user")
public class UserController {
	@Resource
	private IUserService userService;
	@Resource
	private HttpClient httpClient;
	@Resource
	private Configure configure;

	@GetMapping("/user/{username}")
	@ResponseBody
	public User getUserByName(@PathVariable("username") String username) {
		return userService.getUserByName(username);
	}

	@GetMapping("/exception")
	public String testException() {
		throw new CommonException(ErrorCode.fileUploadError);
	}

	@GetMapping("/http/email")
	@ResponseBody
	public String testHttpClient(String email) {
		String url = String.format(configure.getBluePageApi(), email);
		String result = httpClient.request(url, Request.GET);
		JSONObject o = JSON.parseObject(result);
		JSONObject j = o.getJSONObject("search");
		JSONArray array = j.getJSONArray("entry");
		JSONObject user = array.getJSONObject(0);
		JSONArray attributes = user.getJSONArray("attribute");
		for (int i = 0; i < attributes.size(); i++) {
			JSONObject userInfo = attributes.getJSONObject(i);
			System.out.println(userInfo.getString("name") + " --- " + userInfo.getJSONArray("value").getString(0));
		}
		return result;
	}

	@GetMapping("/http/ladp")
	@ResponseBody
	public String testLadpBluePage(String type) {
		String result = null;
		if (type.equals("email")) {
			String filter = String.format(Request.filterEmail.value(), "cddufu@cn.ibm.com");
			result = httpClient.formatNotes(httpClient.requestBluePage(filter, Request.notesMail.value()));
		} else if (type.equals("cnum")) {
			String filter = String.format(Request.filterCnum.value(), "066383", "672");
			result = httpClient.requestBluePage(filter, Request.emailAddress.value());
		}
		return result;
	}
	
	@GetMapping("/app")
	@ResponseBody
	public App getApp() {
		return userService.getApp();
	}
}
