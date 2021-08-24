package com.lhz.reactordemo.demo2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * Date: 2021/8/24
 * Description:
 *
 * @author hz.lai
 */
public class OneShotPublisher implements Flow.Publisher<Boolean> {
    private final ExecutorService executor = ForkJoinPool.commonPool(); // daemon-based
    private boolean subscribed; // true after first subscribe

    //2 在subscribe方法里面，实例化（1）的Subscription，然以后把他传入subscriber
    public synchronized void subscribe(Flow.Subscriber<? super Boolean> subscriber) {
        if (subscribed)
            subscriber.onError(new IllegalStateException()); // only one allowed
        else {
            subscribed = true;
            //subscriber调用onSubscribe，然后用request方法来请求数据
            subscriber.onSubscribe(new OneShotSubscription(subscriber, executor));
        }
    }
    //1.实现一个Subscription
    static class OneShotSubscription implements Flow.Subscription {
        private final Flow.Subscriber<? super Boolean> subscriber;
        private final ExecutorService executor;
        private Future<?> future; // to allow cancellation
        private boolean completed;
        OneShotSubscription(Flow.Subscriber<? super Boolean> subscriber,
                            ExecutorService executor) {
            this.subscriber = subscriber;
            this.executor = executor;
        }
        //3 每当需要的时候，publisher就发布一个item给一个subscriber
        public synchronized void request(long n) {
            if (!completed) {
                completed = true;
                if (n <= 0) {
                    IllegalArgumentException ex = new IllegalArgumentException();
                    executor.execute(() -> subscriber.onError(ex));
                } else {
                    future = executor.submit(() -> {
                        subscriber.onNext(Boolean.TRUE);
                        subscriber.onComplete();
                    });
                }
            }
        }
        public synchronized void cancel() {
            completed = true;
            if (future != null) future.cancel(false);
        }
    }
}
