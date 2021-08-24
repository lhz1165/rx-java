package com.lhz.reactordemo.demo2;

import java.util.concurrent.Flow;
import java.util.function.Consumer;

/**
 * Date: 2021/8/24
 * Description:
 * arranges that items be requested and processed
 * 控制 item被请求和执行的
 * @author hz.lai
 */
public class SampleSubscriber<T> implements Flow.Subscriber<T> {
    final Consumer<? super T> consumer;
    Flow.Subscription subscription;
    final long bufferSize;
    long count;

    SampleSubscriber(long bufferSize, Consumer<? super T> consumer) {
        this.bufferSize = bufferSize;
        this.consumer = consumer;
    }
    //在为给定的subscription,在onSubscribe方法之前,调用任何其他Subscriber方法，(一般在subscribe方法后执行)
    //一般都是在实现类里，subscription.request来接收数据
    public void onSubscribe(Flow.Subscription subscription) {
        long initialRequestSize = bufferSize;
        count = bufferSize - bufferSize / 2; // re-request when half consumed
        (this.subscription = subscription).request(initialRequestSize);
    }
    //发布者调用这个方法传递数据给订阅者
    public void onNext(T item) {
        if (--count <= 0)
            subscription.request(count = bufferSize - bufferSize / 2);
        consumer.accept(item);
    }

    public void onError(Throwable ex) {
        ex.printStackTrace();
    }

    public void onComplete() {
    }

}