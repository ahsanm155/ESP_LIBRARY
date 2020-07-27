package com.esp.library.utilities.common;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ESP_LIB_ServiceGenerator {

  //  public static final String BASE_URL = "https://api.github.com/";

    public static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ESP_LIB_Constants.base_url)
                    .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit = builder.build();

    public static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public static Retrofit retrofit() {
        return builder.build();
    }
}
