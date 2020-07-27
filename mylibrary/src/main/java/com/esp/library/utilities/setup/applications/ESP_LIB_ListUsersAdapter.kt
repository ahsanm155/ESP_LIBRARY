package utilities.adapters.setup.applications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.utilities.common.ESP_LIB_RoundedImageView
import com.esp.library.utilities.common.ESP_LIB_Shared
import utilities.data.applicants.ESP_LIB_UsersListDAO
import utilities.interfaces.ESP_LIB_UserListClickListener


class ESP_LIB_ListUsersAdapter(private val userslist: List<ESP_LIB_UsersListDAO>?, con: ESP_LIB_BaseActivity, internal var searched_text: String)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListUsersAdapter.ParentViewHolder>(), Filterable {


    private var context: ESP_LIB_BaseActivity
    var ESPLIBUserItemClick: ESP_LIB_UserListClickListener? = null
    var ESPLIBUsersListFiltered: List<ESP_LIB_UsersListDAO>? = null
    var ESPLIBUsersList: List<ESP_LIB_UsersListDAO>? = null


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {


        internal var ivuser: ESP_LIB_RoundedImageView
        internal var txtusername: TextView
        internal var txtuseremail: TextView
        internal var rlitem: RelativeLayout


        init {

            ivuser = itemView.findViewById(R.id.ivuser)
            txtuseremail = itemView.findViewById(R.id.txtuseremail)
            txtusername = itemView.findViewById(R.id.txtusername)
            rlitem = itemView.findViewById(R.id.rlitem)
        }

    }


    init {
        context = con
        ESPLIBUsersList = userslist
        ESPLIBUsersListFiltered = userslist
        ESPLIBUserItemClick = context as ESP_LIB_UserListClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_acitivity_user_row, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val userslistDAO = ESPLIBUsersListFiltered?.get(position)
        val holder = holder_parent as ActivitiesList

        if (searched_text.length > 0)
            holder.txtusername.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, userslistDAO?.fullName, context)
        else
            holder.txtusername.text = userslistDAO?.fullName

        holder.txtuseremail.text = userslistDAO?.email
        Glide.with(context).load(userslistDAO?.pictureUrl).placeholder(R.drawable.esp_lib_drawable_default_profile_picture)
                .error(R.drawable.esp_lib_drawable_default_profile_picture).into(holder.ivuser)


        holder.rlitem.setOnClickListener {
            ESPLIBUserItemClick?.userClick(ESPLIBUsersListFiltered?.get(position))

        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return ESPLIBUsersListFiltered?.size ?: 0

    }


    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    companion object {

        private val LOG_TAG = "ListUsersAdapter"


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    ESPLIBUsersListFiltered = ESPLIBUsersList
                } else {
                    val filteredList = ArrayList<ESP_LIB_UsersListDAO>()

                    for (i in 0 until ESPLIBUsersList!!.size) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        searched_text = charString.toLowerCase()
                        if (ESPLIBUsersList?.get(i)?.fullName?.toLowerCase()!!.contains(charString.toLowerCase())) {
                            filteredList.add(ESPLIBUsersList!!.get(i))
                        }
                    }

                    ESPLIBUsersListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = ESPLIBUsersListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                ESPLIBUsersListFiltered = filterResults.values as ArrayList<ESP_LIB_UsersListDAO>
                notifyDataSetChanged()
            }
        }
    }



}
