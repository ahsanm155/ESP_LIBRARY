package utilities.adapters.setup.applications

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_FragmentProfileImage
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import kotlinx.android.synthetic.main.esp_lib_sectionlistrow.view.*
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO
import utilities.interfaces.ESP_LIB_Itemclick


class ESP_LIB_SectionListAdapter(private val sectionESPLIBS: List<ESP_LIB_DynamicFormSectionDAO>, private val contxt: Context, ESPLIBFragmentProfileImage: ESP_LIB_FragmentProfileImage) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_SectionListAdapter.ParentViewHolder>() {

    private val TAG = "SectionListAdapter"
    internal var ESPLIBItemclick: ESP_LIB_Itemclick? = null

    var pref: ESP_LIB_SharedPreference? = null


    init {
        ESPLIBItemclick = ESPLIBFragmentProfileImage
    }

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var etxtsectionname: TextView
        internal var etxttime: TextView
        internal var rlclick: RelativeLayout
        internal var ivwarning: ImageView


        init {
            pref = ESP_LIB_SharedPreference(contxt)
            etxtsectionname = itemView.findViewById(R.id.etxtsectionname)
            etxttime = itemView.findViewById(R.id.etxttime)
            rlclick = itemView.findViewById(R.id.rlclick)
            ivwarning = itemView.findViewById(R.id.ivwarning)
            setGravity(v)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_sectionlistrow, parent, false)

        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {
        val holder = holder_parent as ActivitiesList

        val dynamicFormSectionDAO = sectionESPLIBS[position]
        holder.etxtsectionname.text = dynamicFormSectionDAO.defaultName
        if (dynamicFormSectionDAO.lastUpdatedOn.equals("01 Jan, 0001", ignoreCase = true))
            holder.etxttime.text = contxt.getString(R.string.esp_lib_text_lastupdatedon)
        else {
            val text = contxt.getString(R.string.esp_lib_text_lastupdatedon) + " " + dynamicFormSectionDAO.lastUpdatedOn
            val colorString = dynamicFormSectionDAO.lastUpdatedOn.toString();

            val ssBuilder = SpannableStringBuilder(text)


            ssBuilder.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    text.indexOf(colorString),
                    text.indexOf(colorString) + colorString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.etxttime.text = ssBuilder
        }

        if (dynamicFormSectionDAO.isShowError)
            holder.ivwarning.visibility = View.VISIBLE
        else
            holder.ivwarning.visibility = View.GONE

        holder.rlclick.setOnClickListener { v ->
            if (ESPLIBItemclick != null)
                ESPLIBItemclick!!.itemclick(dynamicFormSectionDAO)
        }


    }//End Holder Class

    private fun setGravity(v: View) {
        if (pref?.language.equals("ar", ignoreCase = true)) {
            v.ivarrow.setImageResource(R.drawable.ic_left_arrow)


        } else {
            v.ivarrow.setImageResource(R.drawable.ic_arrow_right)

        }
    }

    override fun getItemCount(): Int {
        return sectionESPLIBS.size


    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
