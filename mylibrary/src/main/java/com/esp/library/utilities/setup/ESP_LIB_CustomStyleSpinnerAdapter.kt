package com.esp.library.utilities.setup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.esp.library.R
import com.esp.library.utilities.common.GoogleFontsLibrary
import com.esp.library.utilities.data.applicants.ESP_LIB_StylesDAO

class ESP_LIB_CustomStyleSpinnerAdapter(private val mContext: Context, resource: Int, rawStyles: List<ESP_LIB_StylesDAO>) :
        ArrayAdapter<Any?>(mContext, resource, 0, rawStyles) {
    private val mInflater: LayoutInflater
    private val items: List<ESP_LIB_StylesDAO>
    private val mResource: Int


    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = mInflater.inflate(mResource, parent, false)
        val signatureTitle = view.findViewById<TextView>(R.id.tvText)
        val data = items[position]
        signatureTitle.text = data.signatureTitle
        if (data.fontStyle != 1122) {
            signatureTitle?.typeface = GoogleFontsLibrary.setGoogleFont(context, data.fontStyle);
        }
        return view
    }


    init {
        mInflater = LayoutInflater.from(mContext)
        mResource = resource
        items = rawStyles
    }
}