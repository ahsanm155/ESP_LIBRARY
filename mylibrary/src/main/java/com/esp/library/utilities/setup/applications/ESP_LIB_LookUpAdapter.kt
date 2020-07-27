package utilities.adapters.setup.applications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO




class ESP_LIB_LookUpAdapter(val allFields: List<ESP_LIB_LookUpDAO>, con: ESP_LIB_BaseActivity, search: String, internal var fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO?) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_LookUpAdapter.ViewHolder>() {
    internal var imm: InputMethodManager? = null
    internal var search_text = ""
    private var context: ESP_LIB_BaseActivity
    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var lookup_row: LinearLayout
        internal var lookup_name: TextView
        internal var cross_icon: ImageView


        init {
            lookup_name = itemView.findViewById(R.id.lookup_name)
            lookup_row = itemView.findViewById(R.id.lookup_row)
            cross_icon = itemView.findViewById(R.id.cross_icon)
        }

    }

    init {
        context = con
        search_text = search
        imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_lookup_choose, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (allFields.size == 0) {
            return
        }
        if (search_text.length > 0) {

            holder.lookup_name.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(search_text, allFields[position].name,context)
        } else {
            holder.lookup_name.text = allFields[position].name
        }

        if (fieldDAOESPLIB != null) {
            if (fieldDAOESPLIB!!.value != null && fieldDAOESPLIB!!.value!!.length > 0) {
                if (fieldDAOESPLIB!!.id == allFields[position].id) {
                  //  holder.cross_icon.visibility = View.VISIBLE
                    holder.cross_icon.setImageResource(R.drawable.esp_lib_drawable_ic_icons_controls_radio_checked)
                    holder.lookup_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                } else {
                  //  holder.cross_icon.visibility = View.GONE
                    holder.cross_icon.setImageResource(R.drawable.esp_lib_drawable_ic_icons_controls_radio_unchecked)
                    holder.lookup_name.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_dark_grey))
                }
            } else {
              //  holder.cross_icon.visibility = View.GONE
                holder.cross_icon.setImageResource(R.drawable.esp_lib_drawable_ic_icons_controls_radio_unchecked)
                holder.lookup_name.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_dark_grey))
            }
        } else {
           // holder.cross_icon.visibility = View.GONE
            holder.cross_icon.setImageResource(R.drawable.esp_lib_drawable_ic_icons_controls_radio_unchecked)
            holder.lookup_name.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_dark_grey))
        }


        holder.lookup_row.setOnClickListener {
            //selected
            val bnd = Bundle()
            bnd.putSerializable(ESP_LIB_DynamicFormSectionFieldDAO.BUNDLE_KEY, fieldDAOESPLIB)
            bnd.putSerializable(ESP_LIB_LookUpDAO.BUNDLE_KEY, allFields[position])
            val intent = Intent()
            intent.putExtras(bnd)
            context.setResult(Activity.RESULT_OK, intent)
            context.finish()
        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return allFields.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }



    companion object {
        private val LOG_TAG = "LookUpAdapter"

    }

}
