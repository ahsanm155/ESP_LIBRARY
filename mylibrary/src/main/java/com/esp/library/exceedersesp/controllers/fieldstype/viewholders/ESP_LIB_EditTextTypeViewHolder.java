package com.esp.library.exceedersesp.controllers.fieldstype.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.esp.library.R;
import com.esp.library.utilities.customcontrols.ESP_LIB_BodyText;
import com.google.android.material.textfield.TextInputLayout;


public class ESP_LIB_EditTextTypeViewHolder extends RecyclerView.ViewHolder {

    public TextInputLayout tilFieldLabel;
    public TextInputLayout tilFieldDisableLabel;
    public com.esp.library.utilities.customcontrols.ESP_LIB_BodyEditText etValue;
    public com.esp.library.utilities.customcontrols.ESP_LIB_BodyEditText etvalueDisable;
    public ESP_LIB_BodyText tValue;
    public ESP_LIB_BodyText tValueLabel;
    public ImageView ivicon;
    public View onlyviewlayout;
    public LinearLayout llparent;

    public ESP_LIB_EditTextTypeViewHolder(View itemView) {
        super(itemView);

        llparent = itemView.findViewById(R.id.llparent);
        tilFieldLabel = itemView.findViewById(R.id.tilFieldLabel);
        tilFieldDisableLabel = itemView.findViewById(R.id.tilFieldDisableLabel);
        etValue = itemView.findViewById(R.id.etValue);
        tValue = itemView.findViewById(R.id.tValue);
        tValueLabel = itemView.findViewById(R.id.tValueLabel);
        etvalueDisable = itemView.findViewById(R.id.etvalueDisable);
        ivicon = itemView.findViewById(R.id.ivicon);
        onlyviewlayout = itemView.findViewById(R.id.onlyviewlayout);

    }
}
