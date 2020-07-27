package utilities.adapters.setup

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import utilities.data.setup.ESP_LIB_OrganizationPersonaDao
import utilities.data.setup.ESP_LIB_TokenDAO
import java.util.*


class ESP_LIB_ListofOrganizationSectionsAdapter(internal var sections: List<ESP_LIB_OrganizationPersonaDao>,
                                                bContext: ESP_LIB_BaseActivity, private val personas: ESP_LIB_TokenDAO) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListofOrganizationSectionsAdapter.ParentViewHolder>() {

    private val TAG = javaClass.simpleName
    private val context: ESP_LIB_BaseActivity

    init {
        this.context = bContext
    }


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList @SuppressLint("RestrictedApi")
    constructor(v: View) : ParentViewHolder(v) {

        internal var org_list: androidx.recyclerview.widget.RecyclerView
        internal var txtlabel: TextView

        init {
            org_list = itemView.findViewById(R.id.org_list)
            txtlabel = itemView.findViewById(R.id.txtlabel)

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_organization_section_row, parent, false)
        return ActivitiesList(v)
    }

    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList
        val organizationPersonaDao = sections[position]
        //recycler view for fields
        holder.org_list.setHasFixedSize(true)
        holder.org_list.isNestedScrollingEnabled = false

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        holder.org_list.layoutManager = linearLayoutManager

        holder.txtlabel.text = organizationPersonaDao.name
        val personaDAOListESPLIB: MutableList<ESP_LIB_OrganizationPersonaDao.Personas> = ArrayList()
        if (organizationPersonaDao.persoans.size > 1) {
            for (j in organizationPersonaDao.persoans.indices) {
                val personas = organizationPersonaDao.persoans[j]
                if (!personas.type.toLowerCase().equals("app", ignoreCase = true)) {
                    personaDAOListESPLIB.add(personas)
                }

            }
        } else {
            for (j in organizationPersonaDao.persoans.indices) {
                val personas = organizationPersonaDao.persoans[j]
                personaDAOListESPLIB.add(personas)
            }
        }

        val adapter = ESP_LIB_ListPersonaDAOAdapter(personaDAOListESPLIB, organizationPersonaDao, context, personas)
        holder.org_list.adapter = adapter


    }//End Holder Class


    override fun getItemCount(): Int {
        return sections.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}