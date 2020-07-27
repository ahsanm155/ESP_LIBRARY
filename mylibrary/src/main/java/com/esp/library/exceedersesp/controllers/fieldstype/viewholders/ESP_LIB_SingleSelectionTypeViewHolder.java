package com.esp.library.exceedersesp.controllers.fieldstype.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;

import com.esp.library.R;
import com.esp.library.utilities.customcontrols.ESP_LIB_BodyText;


public class ESP_LIB_SingleSelectionTypeViewHolder extends RecyclerView.ViewHolder {


    public ESP_LIB_BodyText tValueLabel;
    public RadioGroup radioGroup;

    public ESP_LIB_SingleSelectionTypeViewHolder(View itemView) {
        super(itemView);

        tValueLabel = itemView.findViewById(R.id.tValueLabel);
        radioGroup = itemView.findViewById(R.id.radioGroup);
    }
}
