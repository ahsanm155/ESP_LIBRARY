package com.esp.library.utilities.customcontrols;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;

import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.customevents.ESP_LIB_OnEnableListener;


public class ESP_LIB_BodyEditText extends androidx.appcompat.widget.AppCompatEditText {

    private ESP_LIB_OnEnableListener _enableListner;
    private String fontName;

    public ESP_LIB_BodyEditText(Context context, String customFont) {
        super(context);
        if (!isInEditMode()) {
            setFont(context, null);
        }


    }

    public ESP_LIB_BodyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);


    }

    private void initialize(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            String packageName = "http://schemas.android.com/apk/res-auto";
            fontName = attrs.getAttributeValue(packageName, "customfont");
            setFont(context, fontName);
        }

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (_enableListner != null) {
            _enableListner.onEnable(enabled);
        }
    }


    public void setOnEnableListner(ESP_LIB_OnEnableListener enableListner) {
        _enableListner = enableListner;
    }

    public void setFont(Context context, String fname) {
        if (ESP_LIB_ESPApplication.getInstance().isSetFont())
            fontName = ESP_LIB_ESPApplication.getInstance().getApplicationTextFont();
        else
            fontName = ESP_LIB_Shared.getInstance().fontName(context, fontName);


        Typeface tf = Typeface.createFromAsset(context.getAssets(), "font/" + fontName);
        setTypeface(tf);
    }


}
