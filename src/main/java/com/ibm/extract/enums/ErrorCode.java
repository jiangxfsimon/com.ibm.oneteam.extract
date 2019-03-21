package com.ibm.extract.enums;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 全局异常具体信息类
 */
public enum ErrorCode {
	fileUploadError(100, "upload file failed"),
	fileIsNotExistError(101, "file is not exist"),
	closeIOError(102, "close io failed"),
	unsupportEncodeError(103, "unsupport encode UTF-8"),
	requestUrlError(104, "remote request failed"),
	requestBluePageError(105, "request blue page failed");
	private int code;
	private String message;
	private ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
	public int code() {
		return this.code;
	}
	public String message() {
		return this.message;
	}
	public String value() {
		return "code: " + code + ", message: " + message;
	}
}
