package com.lv.Utils;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by q on 2015/5/18.
 */
public class TestRxJava {
    Observable<String> myObservable;

    Subscriber<String> mySubscriber = new Subscriber<String>() {
        @Override
        public void onNext(String s) { System.out.println(s); }

        @Override
        public void onCompleted() { }

        @Override
        public void onError(Throwable e) { }
    };

    public TestRxJava() {
        myObservable = Observable.create((sub)->{
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                }
        ).map(s->s+"-dan");

        myObservable.subscribe(mySubscriber);
    }
}
