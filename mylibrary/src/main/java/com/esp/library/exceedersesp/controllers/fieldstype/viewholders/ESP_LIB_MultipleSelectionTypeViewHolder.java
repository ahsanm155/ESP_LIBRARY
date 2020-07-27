package com.esp.library.exceedersesp.controllers.fieldstype.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.esp.library.R;
import com.esp.library.utilities.customcontrols.ESP_LIB_BodyText;


public class ESP_LIB_MultipleSelectionTypeViewHolder extends RecyclerView.ViewHolder {


    public ESP_LIB_BodyText tValueLabel;
    public LinearLayout llcheckbox;

    public ESP_LIB_MultipleSelectionTypeViewHolder(View itemView) {
        super(itemView);

        tValueLabel = itemView.findViewById(R.id.tValueLabel);
        llcheckbox = itemView.findViewById(R.id.llcheckbox);
    }
}
