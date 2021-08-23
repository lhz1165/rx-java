package com.lhz.reactordemo.demo;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

/**
 * @author lhzlhz
 * @create 2021/8/23
 * 类似于电影院
 */
public class MyPublisher<T> implements Flow.Publisher<T>,AutoCloseable{
	private  ExecutorService executor;
	//生产者不直接从subscriber获取数据  而是通过Subscription做一个连接
	CopyOnWriteArrayList<MySubscription<T>> list = new CopyOnWriteArrayList<>();

	public MyPublisher(ExecutorService executor) {
		this.executor = executor;
	}

	public void submit(T item) {
		System.out.println("**************开始发布元素**************");
		list.forEach(e->{
			e.future=executor.submit(()->{
				e.subscriber.onNext(item);
			});
		});
	}

	@Override
	public void subscribe(Flow.Subscriber<? super T> subscriber) {
		subscriber.onSubscribe(new MySubscription(subscriber,executor));
		list.add(new MySubscription(subscriber, executor));
	}

	@Override
	public void close() throws Exception {

		list.forEach(e->{
			e.future=executor.submit(()->{
				e.subscriber.onComplete();
			});
		});
	}

	/**
	 * 电影院和制片绑定在一起
	 * @param <T>
	 */
	static class MySubscription<T> implements Flow.Subscription{
		private Flow.Subscriber<T> subscriber;
		private ExecutorService executor;
		private Future<?> future;
		private T item;
		private boolean completed;

		public MySubscription(Flow.Subscriber<T> subscriber, ExecutorService executor) {
			this.subscriber = subscriber;
			this.executor = executor;
		}

		@Override
		public void request(long n) {
			if (n != 0 && !completed) {
				if (n < 0) {
					executor.execute(() -> subscriber.onError(new IllegalArgumentException()));
				}else {
					future = executor.submit(()->{
						subscriber.onNext(item);
					});
				}
			}else {
				subscriber.onComplete();

			}
		}

		@Override
		public void cancel() {
			completed = true;
			if (future != null && !future.isCancelled()) {
				this.future.cancel(true);
			}
		}
	}
}
