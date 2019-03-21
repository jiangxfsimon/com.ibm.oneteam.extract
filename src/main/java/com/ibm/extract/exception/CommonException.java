package com.ibm.extract.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.extract.enums.ErrorCode;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 自定义全局公共异常
 */
public class CommonException extends RuntimeException {
	private static Logger log = LoggerFactory.getLogger(CommonException.class);
	private static final long serialVersionUID = 1L;
	
	private ErrorCode errorCode;
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public CommonException() {
		super();
	}
	
	public CommonException(Throwable cause) {
		super(cause);
	}
	
	public CommonException(String message) {
		super(message);
		log.error(message);
	}
	
	public CommonException(ErrorCode errorCode) {
		super(errorCode.value());
		this.errorCode = errorCode;
		log.error(errorCode.value());
	}
	
	public CommonException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.value(), cause);
		this.errorCode = errorCode;
		log.error(errorCode.value());
	}
	
	public CommonException(String message, Throwable cause) {
		super(message, cause);
		log.error(message);
	}
	
	public CommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		log.error(message);
	}

}
