package com.esp.library.utilities.setup.applications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.esp.library.R
import com.esp.library.utilities.data.applicants.ESP_LIB_CardValuesDAO
import kotlinx.android.synthetic.main.esp_lib_activity_dynamic_list_applications_row.view.*


class ESP_LIB_ApplicationItemsAdapter(val itemsList: List<ESP_LIB_CardValuesDAO>?, con: Context)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ApplicationItemsAdapter.ViewHolder>() {

    var TAG = "ESP_LIB_ApplicationItemsAdapter"
    private var context: Context? = null
    internal var imm: InputMethodManager? = null


    init {
        context = con
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var txtlabel: TextView = itemView.findViewById(R.id.txtlabel)
        internal var txtvalue: TextView = itemView.findViewById(R.id.txtvalue)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_application_items_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val cardValuesDAO = itemsList?.get(position)
        holder.txtlabel.text=cardValuesDAO?.label+":"
        holder.txtvalue.text=cardValuesDAO?.value



    }//End Holder Class


    override fun getItemCount(): Int {
        if (itemsList != null)
            return itemsList.size
        else
            return 0
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }



}
