package com.lhz.reactordemo.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author lhzlhz
 * @create 2021/8/23
 */
public class MyApp {
	public static void main(String[] args) {
		cistomPublisher();
	}
	private static void demoSubscrib(MyPublisher<Integer> publisher,String name){
		MySubscriber<Integer> subscriber = new MySubscriber<>(4L,name);
		publisher.subscribe(subscriber);
	}

	public static void cistomPublisher() {
		ExecutorService executorService = ForkJoinPool.commonPool();
		try (MyPublisher<Integer> publisher = new MyPublisher<>(executorService)){
			demoSubscrib(publisher, "publisher1 ONE");
			demoSubscrib(publisher, "publisher2 TOW");
			demoSubscrib(publisher, "publisher3 THREE");
			IntStream.range(1,5).forEach(publisher::submit);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				executorService.shutdown();
				int sec = 1;
				System.out.println("------等待 "+sec+"秒 over----------");
				executorService.awaitTermination(sec, TimeUnit.SECONDS);
			} catch (Exception exception) {
				System.out.println("error");
			}finally {
				System.out.println("服务结束");
			}
		}

	}
}
