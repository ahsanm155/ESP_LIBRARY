package com.esp.library.exceedersesp.SingleController;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
public class ConnectionListener {
    private static ConnectionListener mInstance;
    public static ConnectionListener getInstance() {
        if (mInstance == null) {
            mInstance = new ConnectionListener();
        }
        return mInstance;
    }
    private ConnectionListener() {
    }
    //this how to create our bus
    private BehaviorSubject<Boolean> publisher = BehaviorSubject.create();
    public void notifyNetworkChange(Boolean isConnected) {
        publisher.onNext(isConnected);
    }
    // Listen should return an Observable
    public Observable<Boolean> listenNetworkChange() {
        return publisher;
    }
}
