package com.esp.library.exceedersesp.SingleController;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
public abstract class ConnectionInterceptor implements Interceptor {
    public abstract boolean isInternetAvailable();
    public abstract void onInternetUnavailable();
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!isInternetAvailable()) {
            onInternetUnavailable();
        }
        return chain.proceed(request);
    }
}