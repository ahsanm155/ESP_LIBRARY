package com.esp.library.exceedersesp.controllers.fieldstype.other

import android.content.Context
import com.esp.library.utilities.customevents.EventOptions
import org.greenrobot.eventbus.EventBus
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO

object ESP_LIB_CalculatedMappedRequestTrigger {
    @JvmStatic
    fun submitCalculatedMappedRequest(mContext: Context?, isViewOnly: Boolean, ESPLIBDynamicFormSectionFieldDAO: ESP_LIB_DynamicFormSectionFieldDAO?) {
        if (isViewOnly) return
        /* Intent intent = new Intent("getcalculatedvalues");
        intent.putExtra("dynamicFormSectionFieldDAO",dynamicFormSectionFieldDAO);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/

        EventBus.getDefault().post(EventOptions.EventTriggerController())
    }
}