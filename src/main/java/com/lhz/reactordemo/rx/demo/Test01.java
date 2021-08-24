package com.lhz.reactordemo.rx.demo;

import reactor.core.publisher.Flux;

/**
 * Date: 2021/8/24
 * Description:
 *
 * @author hz.lai
 */
public class Test01 {
    public static void main(String[] args) {
        Flux<Integer> ints = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) return i;
                    throw new RuntimeException("Got to 4");
                });
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error   : " + error));
    }
}
