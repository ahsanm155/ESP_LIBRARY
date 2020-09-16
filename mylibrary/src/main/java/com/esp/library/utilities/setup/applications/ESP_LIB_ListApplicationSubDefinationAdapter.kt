package utilities.adapters.setup.applications

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_AddApplicationsFromScreenActivity
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Shared
import utilities.data.applicants.addapplication.ESP_LIB_DefinationsDAO


class ESP_LIB_ListApplicationSubDefinationAdapter(private val mApplications: List<ESP_LIB_DefinationsDAO>?,
                                                  con: Context,
                                                  subdefinationList: RecyclerView) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListApplicationSubDefinationAdapter.ParentViewHolder>(), Filterable {

    // internal var mCat: CategorySelection? = null
    private var context: Context
    var mApplicationsFiltered: List<ESP_LIB_DefinationsDAO>? = null
    var subDefinationList: RecyclerView? = null


    var search_text: String = "";

    interface CategorySelection {
        fun StatusChange(update: ESP_LIB_DefinationsDAO)
    }

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {


        internal var llparent: RelativeLayout
        internal var name: TextView
        internal var description: TextView
        internal var txtparentnum: TextView
        internal var txtrequestedby: TextView
        internal var txtparentnumvalue: TextView
        internal var txtrequestedbyvalue: TextView

        init {

            llparent = itemView.findViewById(R.id.llparent)
            name = itemView.findViewById(R.id.name)
            description = itemView.findViewById(R.id.description)
            txtparentnum = itemView.findViewById(R.id.txtparentnum)
            txtrequestedby = itemView.findViewById(R.id.txtrequestedby)
            txtparentnumvalue = itemView.findViewById(R.id.txtparentnumvalue)
            txtrequestedbyvalue = itemView.findViewById(R.id.txtrequestedbyvalue)


        }

    }


    init {
        context = con
        mApplicationsFiltered = mApplications
        subDefinationList = subdefinationList

        /*try {
            mCat = context as CategorySelection
        } catch (e: ClassCastException) {
            throw ClassCastException("lisnter" + " must implement on Activity")
        }*/

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_add_sub_def_category_list, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        ESP_LIB_CustomLogs.displayLogs("onBindViewHolder")
        val holder = holder_parent as ActivitiesList
        val getmApplications = mApplicationsFiltered!!.get(position)



        if (search_text.length > 0)
            holder.name.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(search_text, getmApplications.parentApplicationInfo?.titleFieldValue, context)
        else
            holder.name.text = getmApplications.parentApplicationInfo?.titleFieldValue

        holder.txtrequestedbyvalue.text = getmApplications.parentApplicationInfo?.applicantEmail
        holder.txtparentnumvalue.text = getmApplications.parentApplicationInfo?.mainApplicationNumber


        if (getmApplications.parentApplicationInfo?.descriptionFieldValue.isNullOrEmpty())
            holder.description.visibility = View.GONE
        else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = getmApplications.parentApplicationInfo?.descriptionFieldValue
        }


        /*if (list_type.equals(context.getString(com.esp.library.exceedersesp.R.string.categorysmall), ignoreCase = true)) {
            holder.name.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            holder.name.setTextColor(context.resources.getColor(R.color.dark_grey))
        }*/

        holder.llparent.setOnClickListener {
            callIntent(getmApplications)
        }


    }//End Holder Class

    fun callIntent(getmApplications: ESP_LIB_DefinationsDAO) {
        val bundle = Bundle()
        bundle.putSerializable(ESP_LIB_DefinationsDAO.BUNDLE_KEY, getmApplications)
        ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AddApplicationsFromScreenActivity::class.java, context as Activity, bundle, 2)
    }

    override fun getItemCount(): Int {
        return mApplicationsFiltered?.size ?: 0

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    companion object {

        private val LOG_TAG = "ListApplicationSubDefinationAdapter"


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    mApplicationsFiltered = mApplications
                } else {
                    val filteredList = ArrayList<ESP_LIB_DefinationsDAO>()
                    for (row in mApplications!!) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match

                        search_text = charString.toLowerCase()
                        if (row.parentApplicationInfo?.titleFieldValue?.toLowerCase()?.contains(charString.toLowerCase())!!) {
                            filteredList.add(row)
                        }
                    }

                    mApplicationsFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = mApplicationsFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mApplicationsFiltered = filterResults.values as ArrayList<ESP_LIB_DefinationsDAO>
                notifyDataSetChanged()
            }
        }
    }


}
