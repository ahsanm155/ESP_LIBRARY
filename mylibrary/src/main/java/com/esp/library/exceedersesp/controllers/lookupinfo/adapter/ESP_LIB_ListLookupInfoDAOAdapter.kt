package com.esp.library.exceedersesp.controllers.lookupinfo.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_SharedPreference

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.lookupinfo.ESP_LIB_LoopUpItemDetailList

import java.util.ArrayList

import utilities.data.lookup.ESP_LIB_LookupInfoListDAO

class ESP_LIB_ListLookupInfoDAOAdapter(ESPLIBLookupInfoList: List<ESP_LIB_LookupInfoListDAO>, context: ESP_LIB_BaseActivity) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListLookupInfoDAOAdapter.ViewHolder>() {

    private val TAG = "ListLookupInfoDAOAdapter"
    internal var pref: ESP_LIB_SharedPreference
    internal var ESPLIBLookupInfoList: List<ESP_LIB_LookupInfoListDAO> = ArrayList()
    private val context: ESP_LIB_BaseActivity
    init {
        this.context = context
        this.ESPLIBLookupInfoList = ESPLIBLookupInfoList
        pref = ESP_LIB_SharedPreference(context)
    }

    inner class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        internal var txtname: TextView
        internal var ivlogo: ImageView
        internal var lllayout: LinearLayout

        init {
            txtname = itemView.findViewById(R.id.txtname)
            ivlogo = itemView.findViewById(R.id.ivlogo)
            lllayout = itemView.findViewById(R.id.lllayout)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_lookup_info_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val lookupInfoListDAO = ESPLIBLookupInfoList[position]

        holder.txtname.text = lookupInfoListDAO.name
        holder.lllayout.setOnClickListener {
            val i = Intent(context, ESP_LIB_LoopUpItemDetailList::class.java)
            i.putExtra("lookupid", lookupInfoListDAO.id)
            i.putExtra("toolbar_heading", lookupInfoListDAO.name)
            context.startActivity(i)
        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return ESPLIBLookupInfoList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }




}
