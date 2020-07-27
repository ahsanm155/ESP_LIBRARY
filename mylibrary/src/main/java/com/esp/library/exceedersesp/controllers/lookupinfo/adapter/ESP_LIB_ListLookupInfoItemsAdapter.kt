package com.esp.library.exceedersesp.controllers.lookupinfo.adapter

import android.content.Intent
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.lookupinfo.ESP_LIB_LookupItemDetail
import com.esp.library.utilities.common.ESP_LIB_Enums
import org.json.JSONException
import org.json.JSONObject
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDAO
import utilities.data.lookup.ESP_LIB_LookupInfoListDetailDAO
import java.util.*

class ESP_LIB_ListLookupInfoItemsAdapter(lookupInfoList: List<ESP_LIB_DynamicFormValuesDAO>, internal var ESPLIBLookupInfoActualData: ESP_LIB_LookupInfoListDetailDAO, internal var isShowEmployeeName: Boolean, internal var employeeName: String, context: ESP_LIB_BaseActivity) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListLookupInfoItemsAdapter.ViewHolder>() {

    private val TAG = "ListLookupInfoItemsAdapter"
    internal var pref: ESP_LIB_SharedPreference
    internal var lookupInfoList: List<ESP_LIB_DynamicFormValuesDAO> = ArrayList()
    private val context: ESP_LIB_BaseActivity
    init {
        this.context = context
        this.lookupInfoList = lookupInfoList
        pref = ESP_LIB_SharedPreference(context)
    }

    inner class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        internal var txttitle: TextView
        internal var txtemployeename: TextView
        internal var txtemployee: TextView
        internal var txtfilters: TextView
        internal var cardview: androidx.cardview.widget.CardView

        init {
            txttitle = itemView.findViewById(R.id.txttitle)
            txtemployeename = itemView.findViewById(R.id.txtemployeename)
            txtemployee = itemView.findViewById(R.id.txtemployee)
            txtfilters = itemView.findViewById(R.id.txtfilters)
            cardview = itemView.findViewById(R.id.cardview)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_lookup_info_detail_item_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sb = SpannableStringBuilder()
        val dynamicFormValuesDAO = lookupInfoList[position]
        holder.txttitle.text = dynamicFormValuesDAO.value

        if (isShowEmployeeName && !ESP_LIB_ESPApplication.getInstance().user.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
            holder.txtemployeename.visibility = View.VISIBLE
            holder.txtemployee.visibility = View.VISIBLE
            holder.txtemployeename.text = employeeName
        } else {
            holder.txtemployeename.visibility = View.GONE
            holder.txtemployee.visibility = View.GONE
        }
        val textSize = context.resources.getDimensionPixelSize(R.dimen._10ssp)
        if (dynamicFormValuesDAO.filterLookup != null) {
            val splitString = dynamicFormValuesDAO.filterLookup!!.split("_:_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in splitString.indices) {
                val breakString = splitString[i].split("<:>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val spannable = SpannableString(breakString[0])
                spannable.setSpan(AbsoluteSizeSpan(textSize), 0, breakString[0].length, 0) // set size
                spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.esp_lib_color_mid_grey)), 0, breakString[0].length, 0)
                sb.append(spannable)
                sb.append("\n")

                try {
                    if (ESP_LIB_Shared.getInstance().isJSONValid(breakString[1])) {
                        try {
                            val jsonObject = JSONObject(breakString[1])
                            if (jsonObject.has(pref.language)) {
                                val text = jsonObject.getString(pref.language)
                                sb.append(displayValue(text))
                                //sb.append(text);
                            }

                        } catch (e: JSONException) {
                         //   e.printStackTrace()
                        }

                    } else
                        sb.append(displayValue(breakString[1]))


                } catch (e: Exception) {
                   // e.printStackTrace()
                }

                if (i < splitString.size - 1)
                    sb.append("\n")
            }

            holder.txtfilters.setText(sb, TextView.BufferType.SPANNABLE)
            holder.txtfilters.visibility = View.VISIBLE
        } else
            holder.txtfilters.visibility = View.GONE


        holder.cardview.setOnClickListener { v ->

            val i = Intent(context, ESP_LIB_LookupItemDetail::class.java)
            i.putExtra("id", dynamicFormValuesDAO.itemid)
            i.putExtra("toolbar_heading", dynamicFormValuesDAO.value)
            context.startActivity(i)

        }


    }//End Holder Class


    fun displayValue(input: String): String? {
        var displayDValue: String? = ESP_LIB_Shared.getInstance().getDisplayDate(context, input, true)
        if (displayDValue == null || displayDValue.replace("\\s".toRegex(), "").length == 0)
            displayDValue = input

        return displayDValue
    }

    override fun getItemCount(): Int {
        return lookupInfoList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }




}
