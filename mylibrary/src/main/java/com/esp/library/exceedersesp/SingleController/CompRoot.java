package com.esp.library.exceedersesp.SingleController;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.utilities.common.ESP_LIB_Constants;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utilities.data.apis.ESP_LIB_APIs;

public class CompRoot {
    private Retrofit retrofit;

    public ESP_LIB_APIs getService(Context context) {
        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(ESP_LIB_Constants.base_url)
                    .client(provideOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(ESP_LIB_APIs.class);
    }

    private OkHttpClient provideOkHttpClient(Context context) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        if (ESP_LIB_Constants.WRITE_LOG) {
            httpClient.addInterceptor(logging);
        }

      /*  String access_token = null;
        if (ESP_LIB_ESPApplication.getInstance().getAccess_token() != null)
            access_token = ESP_LIB_ESPApplication.getInstance().getAccess_token();
        else if (ESP_LIB_ESPApplication.getInstance().getUser() != null && ESP_LIB_ESPApplication.getInstance().getUser().getLoginResponse() != null)
            access_token = ESP_LIB_ESPApplication.getInstance().getUser().getLoginResponse().getAccess_token();*/

        //String finalAccess_token = access_token;
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = null;
            requestBuilder = original.newBuilder()
                    .header("locale", ESP_LIB_Shared.getInstance().getLanguageSimpleContext(context))
                    .header("Authorization", "bearer " + ESP_LIB_ESPApplication.getInstance().getAccess_token());
            Request request = null;
            if (requestBuilder != null)
                request = requestBuilder.build();

            return chain.proceed(request);
        });

        /*if (access_token == null) {


            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = null;
                requestBuilder = original.newBuilder()
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguageSimpleContext(context))
                        .header("Authorization", "bearer " + access_token);
                Request request = null;
                if (requestBuilder != null)
                    request = requestBuilder.build();

                return chain.proceed(request);
            });
        } else {

            String finalAccess_token = access_token;
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = null;
                requestBuilder = original.newBuilder()
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguageSimpleContext(context))
                        .header("Authorization", "bearer " + finalAccess_token);

                Request request = null;
                if (requestBuilder != null)
                    request = requestBuilder.build();

                return chain.proceed(request);
            });
        }*/


        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(new ConnectionInterceptor() {
            @Override
            public boolean isInternetAvailable() {
                return CompRoot.this.isNetworkAvailable(context);
            }

            @Override
            public void onInternetUnavailable() {
                ConnectionListener.getInstance().notifyNetworkChange(false);
            }
        });
        return httpClient.build();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
