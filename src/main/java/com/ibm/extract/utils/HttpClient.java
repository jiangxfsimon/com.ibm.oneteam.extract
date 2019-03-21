package com.ibm.extract.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javax.annotation.Resource;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.springframework.stereotype.Component;
import com.ibm.extract.config.Configure;
import com.ibm.extract.enums.ErrorCode;
import com.ibm.extract.enums.Request;
import com.ibm.extract.exception.CommonException;

/**
 * @author FuDu
 * @date 2019-03-20
 * @desc 远程请求地址
 */
@Component
public class HttpClient {
	@Resource
	private Configure configure;

	/**
	 * @param url
	 *            请求的地址
	 * @param type
	 *            请求类型, 支持 POST 或 GET
	 * @return connection, 如果 getResponseCode() == 200 表示访问成功
	 */
	public String request(String url, Request type) {
		String result = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod(type.toString());
			connection.setConnectTimeout(1500000);
			connection.setReadTimeout(600000);
			connection.connect();
			if (connection.getResponseCode() == Request.OK.code()) {
				result = readData(connection);
			}
		} catch (MalformedURLException e) {
			throw new CommonException(ErrorCode.requestUrlError);
		} catch (IOException e) {
			throw new CommonException(ErrorCode.closeIOError);
		}
		return result;
	}

	/**
	 * @param connection
	 *            HttpURLConnection
	 * @return 通过输入流获取数据
	 */
	public String readData(HttpURLConnection connection) {
		StringBuffer stringBuffer = new StringBuffer();
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(connection.getInputStream(), configure.getDefaultEncoding());
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String temp = null;
			while ((temp = bufferedReader.readLine()) != null) {
				stringBuffer.append(temp);
			}
		} catch (UnsupportedEncodingException e) {
			throw new CommonException(ErrorCode.unsupportEncodeError);
		} catch (IOException e) {
			throw new CommonException(ErrorCode.closeIOError);
		}
		return stringBuffer.toString();
	}

	/**
	 * @return Ladap DirContext
	 */
	public DirContext initDirContext() {
		DirContext dirContext = null;
		try {
			Properties properties = new Properties();
			properties.put(configure.getBluepageReferral(), configure.getBluepageReferralValue());
			properties.put(configure.getBluePageInitial(), configure.getBluePageInitialValue());
			properties.put(configure.getBluePageLdap(), configure.getBluePageLdapValue());
			properties.put(configure.getBluePageUrl(), configure.getBluePageUrlValue());
			dirContext = new InitialDirContext(properties);
		} catch (NamingException e) {
			throw new CommonException(ErrorCode.requestBluePageError);
		}
		return dirContext;
	}

	/**
	 * @param filter
	 *            条件表达式 * @param key 想要获取的用户信息的关键字
	 * @return 从 BP 同步过来的用户信息
	 */
	public String requestBluePage(String filter, String key) {
		String value = null;
		try {
			SearchControls getFields = new SearchControls();
			getFields.setSearchScope(SearchControls.SUBTREE_SCOPE);
			DirContext dirContext = initDirContext();
			NamingEnumeration<SearchResult> searchResults = dirContext.search(configure.getBluepageLdapBase(), filter,
					getFields);
			if (searchResults.hasMore()) {
				SearchResult searchResult = (SearchResult) searchResults.next();
				System.out.println(searchResult);
				Attributes attributes = searchResult.getAttributes();
				Attribute attribute = attributes.get(key);
				value = attribute.get().toString();
			}
		} catch (NamingException e) {
			throw new CommonException(ErrorCode.requestBluePageError);
		}
		return value;
	}

	/**
	 * @param notes
	 *            从 bluePage 取回的 notes
	 * @return 格式化后的 notes
	 */
	public String formatNotes(String notes) {
		String tempNotesname = notes;
		int index = 0;
		if ((index = tempNotesname.indexOf("CN=")) >= 0) {
			tempNotesname = tempNotesname.substring(index + 3);
		}
		while ((index = tempNotesname.indexOf("OU=")) >= 0) {
			tempNotesname = tempNotesname.substring(0, index) + tempNotesname.substring(index + 3);
		}
		if ((index = tempNotesname.indexOf("O=")) >= 0) {
			tempNotesname = tempNotesname.substring(0, index) + tempNotesname.substring(index + 2);
		}
		if ((index = tempNotesname.indexOf("@")) >= 0) {
			tempNotesname = tempNotesname.substring(0, index);
		}
		return tempNotesname;
	}
}
