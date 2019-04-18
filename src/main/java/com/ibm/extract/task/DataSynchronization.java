package com.ibm.extract.task;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ibm.extract.constant.Intervalometer;

/**
 * @author FuDu
 * @date 2019-03-20
 * @desc 定时同步 BP 数据到本地 sqlite 数据库
 */
@Component
public class DataSynchronization {
	private static Logger log = LoggerFactory.getLogger(DataSynchronization.class);

	@Scheduled(cron = Intervalometer.everySecond)
	public void task() {
		log.info("data synchronization start ...");
		System.out.println(new Date() + ", testing ...");
		log.info("data synchronization complete ...");
	}
}
