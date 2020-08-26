package utilities.adapters.setup.applications

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_AddApplicationsFromScreenActivity
import com.esp.library.utilities.common.ESP_LIB_Shared
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO
import java.io.IOException
import java.nio.charset.Charset


class ESP_LIB_ListApplicationCategoryAndDefinationAdapter(private val mApplications: List<ESP_LIB_CategoryAndDefinationsDAO>?,
                                                          con: ESP_LIB_BaseActivity, internal var list_type: String) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListApplicationCategoryAndDefinationAdapter.ParentViewHolder>(), Filterable {

    internal var mCat: CategorySelection? = null
    private var context: ESP_LIB_BaseActivity
    var mApplicationsFiltered: List<ESP_LIB_CategoryAndDefinationsDAO>? = null


    var search_text: String = "";

    interface CategorySelection {
        fun StatusChange(update: ESP_LIB_CategoryAndDefinationsDAO)
    }

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var btrequest: Button
        internal var cards: androidx.cardview.widget.CardView
        internal var name: TextView
        internal var description: TextView
        internal var txtcategory: TextView
        internal var icons: TextView

        init {
            cards = itemView.findViewById(R.id.cards)
            btrequest = itemView.findViewById(R.id.btrequest)
            name = itemView.findViewById(R.id.name)
            description = itemView.findViewById(R.id.description)
            txtcategory = itemView.findViewById(R.id.txtcategory)
            icons = itemView.findViewById(R.id.icons)


        }

    }


    init {
        context = con
        mApplicationsFiltered = mApplications
        try {
            mCat = context as CategorySelection
        } catch (e: ClassCastException) {
            throw ClassCastException("lisnter" + " must implement on Activity")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_add_def_category_list, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList
        val getmApplications = mApplicationsFiltered!!.get(position)

      /*  try {
            val obj = JSONObject(loadJSONFromAsset())
            val icon = obj.getString(mApplications!![position].iconName)
            holder.icons.text = icon
        } catch (e: JSONException) {
            e.printStackTrace()
        }*/


        if (search_text.length > 0)
            holder.name.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(search_text, getmApplications.name,context)
        else
        {
            holder.name.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(search_text, getmApplications.name,context)
            holder.name.text = getmApplications.name
        }

        holder.txtcategory.text = getmApplications.category
        holder.description.text = getmApplications.description

        if(TextUtils.isEmpty(getmApplications.category))
            holder.txtcategory.visibility=View.INVISIBLE


        if (list_type.equals(context.getString(R.string.esp_lib_text_categorysmall), ignoreCase = true)) {
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
        } else {
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.esp_lib_color_dark_grey))
        }

        holder.cards.setOnClickListener {
            if (list_type.equals(context.getString(R.string.esp_lib_text_categorysmall), ignoreCase = true)) {

                if (mCat != null) {
                    mCat!!.StatusChange(getmApplications)
                }

            } else {

                val bundle = Bundle()
                bundle.putSerializable(ESP_LIB_CategoryAndDefinationsDAO.BUNDLE_KEY, getmApplications)
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AddApplicationsFromScreenActivity::class.java, context, bundle, 2)

            }
        }


    }//End Holder Class

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val `is` = context.assets.open("definition_icons_info.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset: Charset = Charsets.UTF_8
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
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

        private val LOG_TAG = "ListApplicationCategoryAndDefinationAdapter"


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    mApplicationsFiltered = mApplications
                } else {
                    val filteredList = ArrayList<ESP_LIB_CategoryAndDefinationsDAO>()
                    for (row in mApplications!!) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match

                        search_text = charString.toLowerCase()
                        if (row.name?.toLowerCase()?.contains(charString.toLowerCase())!!) {
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
                mApplicationsFiltered = filterResults.values as ArrayList<ESP_LIB_CategoryAndDefinationsDAO>
                notifyDataSetChanged()
            }
        }
    }


}
