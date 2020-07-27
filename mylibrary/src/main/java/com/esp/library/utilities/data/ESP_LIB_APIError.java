package com.esp.library.utilities.data;

import android.widget.EditText;
import android.widget.TextView;

public class ESP_LIB_APIError {

    private int statusCode;
    private String message;

    public ESP_LIB_APIError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
