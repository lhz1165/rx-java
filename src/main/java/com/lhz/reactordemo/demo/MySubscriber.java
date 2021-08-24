package com.lhz.reactordemo.demo;

import java.util.concurrent.Flow;

/**
 * @author lhzlhz
 * @create 2021/8/23
 */
public class MySubscriber<T> implements Flow.Subscriber<T>{
	private String name;

	//生产者和消费者的联系
	private Flow.Subscription subscription;

	long count;
	long bufferSize;

	public Flow.Subscription getSubscription() {
		return subscription;
	}

	public MySubscriber(long bufferSize, String name) {
		this.bufferSize = bufferSize;
		this.name = name;
	}

	public String getName() {
		return name;
	}



	@Override
	public void onSubscribe(Flow.Subscription subscription) {
		this.subscription = subscription;
		subscription.request(bufferSize);
		System.out.println("开始 onSubscribe 订阅");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNext(T item) {
		System.out.println("#####" + Thread.currentThread().getName() + " name: " + name + " item: " + item + " #####");
		//System.out.println(name+" receive "+item);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onError(Throwable throwable) {
		throwable.printStackTrace();

	}

	@Override
	public void onComplete() {
		System.out.println("onComplete");
	}
}
