package com.esp.library.utilities.customcontrols

import android.content.Context
import android.view.View
import android.widget.TextView

class ESP_LIB_myBadgeView(context: Context, target: View) : androidx.appcompat.widget.AppCompatTextView(context) {

    private var target: View? = null

    init {
        init(context, target)
    }

    private fun init(context: Context, target: View) {
        this.target = target
    }

    fun updateTabBadge(badgeNumber: Int) {
        if (badgeNumber > 0) {
            target!!.visibility = View.VISIBLE
            (target as TextView).text = Integer.toString(badgeNumber)
        } else {
            target!!.visibility = View.GONE
        }
    }
}
