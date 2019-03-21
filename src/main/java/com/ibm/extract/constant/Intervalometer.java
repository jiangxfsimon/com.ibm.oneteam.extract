package com.ibm.extract.constant;

/**
 * @author FuDu
 * @date 2019-03-20
 * @desc 定时器时间设置
 */
public class Intervalometer {
	/* 每天中午的 12 点 */
	public final static String everyNoon = "0 0 12 * * ?";
	/* 每天晚上的 23 点 59 分*/
	public final static String everyMidNight = "0 59 23 * * ?";
	/* 每周六中午 12 点 */
	public final static String everyWeekendSat = "0 0 12 ? * SAT";
	/* 每周日中午 12 点 */
	public final static String everyWeekendSun = "0 0 12 ? * SUN";
}
