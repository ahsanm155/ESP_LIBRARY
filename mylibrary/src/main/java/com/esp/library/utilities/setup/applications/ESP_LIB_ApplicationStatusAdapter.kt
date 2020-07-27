package utilities.adapters.setup.applications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Enums
import java.util.*


class ESP_LIB_ApplicationStatusAdapter(val statusList: List<String>?, con: Context)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ApplicationStatusAdapter.ViewHolder>() {

    var TAG = "ApplicationStatusAdapter"
    private var context: Context? = null
    internal var imm: InputMethodManager? = null

    init {
        context = con
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var txtstatus: TextView = itemView.findViewById(R.id.txtstatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_application_status_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var status = statusList?.get(position)
        statusColor(holder, status)


    }//End Holder Class

    private fun statusColor(holder: ViewHolder, status: String?) {


        when(status?.toLowerCase(Locale.getDefault())) {
            ESP_LIB_Enums.newstatus.toString()->holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_list_draft))
            ESP_LIB_Enums.locked.toString()->holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_list_draft))
            ESP_LIB_Enums.invited.toString()->holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_invited))
            ESP_LIB_Enums.pending.toString()->holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
            ESP_LIB_Enums.accepted.toString()->holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted))
            ESP_LIB_Enums.rejected.toString()->holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
            else->holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
        }

      /*  if (status!!.equals(context?.getString(R.string.new_satus), ignoreCase = true)) {
            holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.status_list_draft))
        } else if (status.equals(context?.getString(R.string.locked), ignoreCase = true)) {
            holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.status_list_draft))
        } else if (status.equals(context?.getString(R.string.invited), ignoreCase = true)) {
            holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.status_invited))
        } else if (status.equals(context?.getString(R.string.pending), ignoreCase = true)) {
            holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.status_pending))
        } else if (status.equals(context?.getString(R.string.accepted), ignoreCase = true)) {
            holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.status_accepted))
        } else if (status.equals(context?.getString(R.string.rejected), ignoreCase = true)) {
            holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.status_rejected))
        } else {
            holder.txtstatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.status_pending))
        }*/
    }

    override fun getItemCount(): Int {
        if (statusList != null)
            return statusList.size
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
