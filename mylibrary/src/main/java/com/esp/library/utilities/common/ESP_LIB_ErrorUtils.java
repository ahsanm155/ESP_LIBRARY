package com.esp.library.utilities.common;

import com.esp.library.utilities.data.ESP_LIB_APIError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ESP_LIB_ErrorUtils {

    public static ESP_LIB_APIError parseError(Response<?> response) {
        Converter<ResponseBody, ESP_LIB_APIError> converter =
                ESP_LIB_ServiceGenerator.retrofit()
                        .responseBodyConverter(ESP_LIB_APIError.class, new Annotation[0]);

        ESP_LIB_APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ESP_LIB_APIError();
        }

        return error;
    }
}
