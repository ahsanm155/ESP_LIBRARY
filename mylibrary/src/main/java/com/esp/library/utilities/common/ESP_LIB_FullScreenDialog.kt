package com.esp.library.utilities.common

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.esp.library.R
import com.esp.library.utilities.customevents.EventOptions
import com.esp.library.utilities.customevents.EventOptions.EventRefreshData
import kotlinx.android.synthetic.main.esp_lib_activity_fullscreen_popup.*
import org.greenrobot.eventbus.EventBus


class FullScreenDialogExample() : DialogFragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.esp_lib_activity_fullscreen_popup, container, false)
    }


    override fun getTheme(): Int {
        return R.style.ESP_LIB_DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = arguments?.getString("message")
        txtmessage.text = message
        btok.setOnClickListener {
            dismiss()
        }
        EventBus.getDefault().post(EventOptions.RefreshDataOnBack())

        val handler = Handler()
        handler.postDelayed({ dismiss() }, 5000) // 5 sec

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // if (!response.body().isSubmissionAllowed()) {
        EventBus.getDefault().post(EventOptions.PopUpDismissEvent())
    }

}