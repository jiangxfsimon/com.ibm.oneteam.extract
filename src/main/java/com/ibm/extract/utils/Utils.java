package com.ibm.extract.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ibm.extract.config.Configure;
import com.ibm.extract.enums.DateFormat;
import com.ibm.extract.enums.ErrorCode;
import com.ibm.extract.exception.CommonException;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 公共工具类
 */
@Component
public class Utils {
	@Resource
	private Configure configure;

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
