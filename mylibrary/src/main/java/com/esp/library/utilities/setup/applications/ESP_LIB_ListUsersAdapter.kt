package utilities.adapters.setup.applications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView
import utilities.data.applicants.ESP_LIB_UsersListDAO
import utilities.interfaces.ESP_LIB_UserListClickListener


class ESP_LIB_ListUsersAdapter(private val userslist: List<ESP_LIB_UsersListDAO>?, con: ESP_LIB_BaseActivity,
                               internal var searched_text: String, private val rvUsersList: RecyclerView)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListUsersAdapter.ParentViewHolder>(), Filterable {


    private var context: ESP_LIB_BaseActivity
    var ESPLIBUserItemClick: ESP_LIB_UserListClickListener? = null
    var ESPLIBUsersListFiltered: List<ESP_LIB_UsersListDAO>? = null
    var ESPLIBUsersList: List<ESP_LIB_UsersListDAO>? = null
    var previousPosition: Int = -1
    private var sClickListener: SingleClickListener? = null
    private var selectedId = 0

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {


        internal var ivuser: NameInitialsCircleImageView
        internal var txtusername: TextView
        internal var txtuseremail: TextView
        internal var userselection: AppCompatCheckBox
        internal var rlitem: RelativeLayout


        init {

            ivuser = itemView.findViewById(R.id.ivuser)
            txtuseremail = itemView.findViewById(R.id.txtuseremail)
            txtusername = itemView.findViewById(R.id.txtusername)
            userselection = itemView.findViewById(R.id.checkbox)
            rlitem = itemView.findViewById(R.id.rlitem)
        }

        fun onClick(view: View?) {
            previousPosition = adapterPosition
            sClickListener?.onItemClickListener(adapterPosition, view)
        }

    }


    init {
        context = con
        ESPLIBUsersList = userslist
        ESPLIBUsersListFiltered = userslist
        ESPLIBUserItemClick = context as ESP_LIB_UserListClickListener
    }

    fun getList(): List<ESP_LIB_UsersListDAO>? {
        return ESPLIBUsersListFiltered
    }

    fun setOnItemClickListener(clickListener: SingleClickListener) {
        sClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_acitivity_user_row, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val userslistDAO = ESPLIBUsersListFiltered?.get(position)
        val holder = holder_parent as ActivitiesList

        if (userslistDAO != null) {
            holder.userselection.isChecked = ESPLIBUsersListFiltered?.get(position)?.isChecked!!
        }


        /*if (previousPosition == -1) {
            holder.userselection.isChecked = false
        } else {
            holder.userselection.isChecked = previousPosition == position
        }*/

        if (searched_text.length > 0)
            holder.txtusername.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, userslistDAO?.fullName, context)
        else
            holder.txtusername.text = userslistDAO?.fullName

        holder.txtuseremail.text = userslistDAO?.email
        /* Glide.with(context).load(userslistDAO?.pictureUrl).placeholder(R.drawable.esp_lib_drawable_default_profile_picture)
                 .error(R.drawable.esp_lib_drawable_default_profile_picture).into(holder.ivuser)*/

        val initials = userslistDAO?.fullName!!
                .split(' ')
                .mapNotNull { it.firstOrNull()?.toString() }
                .reduce { acc, s -> acc + s }

        val imageInfo: NameInitialsCircleImageView.ImageInfo = NameInitialsCircleImageView.ImageInfo
                .Builder(initials)
                .setImageUrl(userslistDAO.pictureUrl)
                .setCircleBackgroundColorRes(R.color.esp_lib_color_status_draft_background)
                .setTextColor(R.color.esp_lib_color_very_grey_dark)
                .build()
        holder.ivuser.setImageInfo(imageInfo)


        if (previousPosition == position) {
            holder.userselection.setChecked(true);
        } else {
            holder.userselection.setChecked(false);
        }

        holder.rlitem.setOnClickListener {

            if (previousPosition != position) {
                if (previousPosition != -1) {
                    ESPLIBUsersListFiltered?.get(previousPosition)?.isChecked = false
                    if (previousPosition != position)
                        ESPLIBUsersListFiltered?.get(position)?.isChecked = true

                } else
                    ESPLIBUsersListFiltered?.get(position)?.isChecked = true

                try {
                    notifyDataSetChanged()
                } catch (e: Exception) {
                }
                previousPosition = position
                ESPLIBUserItemClick?.userClick(ESPLIBUsersListFiltered?.get(position))

            }

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

    interface SingleClickListener {
        fun onItemClickListener(position: Int, view: View?)
    }


}
