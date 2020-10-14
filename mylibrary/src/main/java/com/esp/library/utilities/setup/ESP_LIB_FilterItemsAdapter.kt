package com.esp.library.utilities.setup

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ActivitySubmissionRequests
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_AddApplicationCategoryAndDefinationsFragment
import utilities.data.applicants.addapplication.ESP_LIB_DefinationsDAO
import utilities.interfaces.ESP_LIB_DeleteFilterListener


class ESP_LIB_FilterItemsAdapter(internal var filtersList: MutableList<ESP_LIB_DefinationsDAO>, bContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_FilterItemsAdapter.ParentViewHolder>() {

    private val TAG = javaClass.simpleName
    private val context: Context
    var ESPLIBDeleteFilterListener:ESP_LIB_DeleteFilterListener?=null

    init {
        this.context = bContext

    }


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList @SuppressLint("RestrictedApi")
    constructor(v: View) : ParentViewHolder(v) {

        internal var rlfilterbutton: RelativeLayout
        internal var txtcatName: TextView

        init {
            rlfilterbutton = itemView.findViewById(R.id.rlfilterbutton)
            txtcatName = itemView.findViewById(R.id.txtcatName)

        }

    }

    fun setActivitContext(ESPLIBAddApplicationCategoryAndDefinationsFragment: ESP_LIB_AddApplicationCategoryAndDefinationsFragment)
    {
        //addApplicationCategoryAndDefinationsFragment= addApplicationCategoryAndDefinationsFragment
        ESPLIBDeleteFilterListener= ESPLIBAddApplicationCategoryAndDefinationsFragment
    }

    fun setActivitContext(ESPLIBActivitySubmissionRequests: ESP_LIB_ActivitySubmissionRequests)
    {
        ESPLIBDeleteFilterListener= ESPLIBActivitySubmissionRequests
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_filter_item_row, parent, false)
        return ActivitiesList(v)
    }

    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList
        val filtersList = filtersList[position]
        holder.txtcatName.text = filtersList.name

        holder.rlfilterbutton.setOnClickListener {
            ESPLIBDeleteFilterListener?.deleteFilters(filtersList)
        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return filtersList.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}