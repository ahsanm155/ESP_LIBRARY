package com.esp.library.exceedersesp.controllers.applications.filters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.data.filters.ESP_LIB_FilterDefinitionSortDAO
import com.esp.library.utilities.interfaces.ESP_LIB_FilterListener


class ESP_LIB_FilterSortByAdapter(val ESPLIBFilterSortByListSort: List<ESP_LIB_FilterDefinitionSortDAO>, con: Context)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_FilterSortByAdapter.ParentViewHolder>() {


    var TAG = "FilterDefinitionAdapter"
    private var context: Context
    var pref: ESP_LIB_SharedPreference? = null
    var filterListener: ESP_LIB_FilterListener? = null


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {


        internal var checkBox: AppCompatCheckBox
        internal var txtname: TextView
        internal var rlParentLayout: RelativeLayout

        init {

            rlParentLayout = itemView.findViewById(R.id.rlParentLayout)
            checkBox = itemView.findViewById(R.id.checkBox)
            txtname = itemView.findViewById(R.id.txtname)
        }
    }

    init {
        context = con
        pref = ESP_LIB_SharedPreference(context)
        filterListener = context as ESP_LIB_FilterListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_activity_filter_sort_row, parent, false)
        return ActivitiesList(v)

    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {
        val holder = holder_parent as ActivitiesList
        val filterSortByDAO = ESPLIBFilterSortByListSort[position]
        holder.txtname.text = filterSortByDAO.name
        holder.checkBox.isChecked = filterSortByDAO.isCheck

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            filterListener?.selectedSortValues(filterSortByDAO, ESPLIBFilterSortByListSort, position)

        }



        holder.rlParentLayout.setOnClickListener {

            holder.checkBox.performClick()
        }
    }

    companion object {

        var prevPos = 0
    }

    override fun getItemCount(): Int {
        return ESPLIBFilterSortByListSort.size
    }


}
