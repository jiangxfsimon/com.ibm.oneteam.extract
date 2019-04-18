package com.ibm.extract.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestThreadPool {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		//1.创建线程池
		ExecutorService pool=Executors.newFixedThreadPool(5);
		List<Future<Integer>> list=new ArrayList<>();
		
		for(int i=0;i<10;i++) {
		Future<Integer> future=pool.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int sum=0;
					for(int i=0;i<=100;i++) {
						sum+=i;
					}
					System.out.println("sum: "+sum);
					Thread.sleep(200);
					return sum;
				}
			});
			list.add(future);
			System.out.println("--------------");
		}
		for(int i=0;i<100;i++) {
			System.out.println("===============");
		}
		for(Future<Integer> f:list) {
			System.out.println(f.get());
		}
		
		/*ThreadPoolDemo tpd=new ThreadPoolDemo();
		 * //2.为线程池中的线程分配任务
		for(int i=0;i<10;i++) {
			pool.submit(tpd);
		}*/
		//3.关闭线程池
		pool.shutdown();
	}
}
