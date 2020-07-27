package utilities.adapters.setup.applications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.applications.filters.ESP_LIB_FilterActivity
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO
import utilities.interfaces.ESP_LIB_CheckFilterSelection

class ESP_LIB_ListApplicationCategoryAdapter(private val mApplications: List<ESP_LIB_CategoryAndDefinationsDAO>?, con: ESP_LIB_BaseActivity) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListApplicationCategoryAdapter.ParentViewHolder>() {


    private var context: ESP_LIB_BaseActivity
    var filterSelectionListenerESPLIB: ESP_LIB_CheckFilterSelection? = null
    var previousPosition: Int = 0
    var categoryAndDefinationsDAOFilteredList = ArrayList<ESP_LIB_CategoryAndDefinationsDAO>()

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var ivChecked: ImageView
        internal var tvMultiSelectionFilter: TextView
        internal var llFilterCont: LinearLayout

        init {
            ivChecked = itemView.findViewById(R.id.ivChecked)
            tvMultiSelectionFilter = itemView.findViewById(R.id.tvMultiSelectionFilter)
            llFilterCont = itemView.findViewById(R.id.llFilterCont)


        }

    }


    init {
        context = con
        filterSelectionListenerESPLIB = context as ESP_LIB_CheckFilterSelection

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_row_filter_multi_selection, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList
        val categoryAndDefinationsDAO = mApplications!![position]

        holder.tvMultiSelectionFilter.text = categoryAndDefinationsDAO.name

        defualtButton(holder,categoryAndDefinationsDAO.isChecked,categoryAndDefinationsDAO)

        holder.llFilterCont.setOnClickListener {

            if (categoryAndDefinationsDAO.isChecked) {
                categoryAndDefinationsDAO.isChecked = false
                defualtButton(holder, false,categoryAndDefinationsDAO)

            } else {

                categoryAndDefinationsDAO.isChecked = true
                defualtButton(holder, true,categoryAndDefinationsDAO)
            }

            filterSelectionListenerESPLIB?.checkFilterSelection(mApplications)
        }


    }//End Holder Class


    private fun defualtButton(holder: ActivitiesList, checked: Boolean,
                              ESPLIBCategoryAndDefinationsDAO: ESP_LIB_CategoryAndDefinationsDAO) {

        if(checked)
        {
            categoryAndDefinationsDAOFilteredList.add(ESPLIBCategoryAndDefinationsDAO)
            holder.ivChecked.visibility = View.VISIBLE
            holder.llFilterCont.background = ContextCompat.getDrawable(context, R.drawable.esp_lib_drawable_draw_bg_solid_green)
            holder.tvMultiSelectionFilter.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_white))
        }
        else {
            categoryAndDefinationsDAOFilteredList.remove(ESPLIBCategoryAndDefinationsDAO)
            holder.ivChecked.visibility = View.GONE
            holder.llFilterCont.background = ContextCompat.getDrawable(context, R.drawable.esp_lib_drawable_draw_bg_green_stroke)
            holder.tvMultiSelectionFilter.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }

       // AddApplicationCategoryAndDefinationsFragment.categoryAndDefinationsDAOFilteredList = categoryAndDefinationsDAOFilteredList
        ESP_LIB_FilterActivity.tempFilterSelectionValues = categoryAndDefinationsDAOFilteredList
    }

    override fun getItemCount(): Int {
        return mApplications?.size ?: 0

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    companion object {

        private val LOG_TAG = "ListApplicationCategoryAdapter"


    }


}
