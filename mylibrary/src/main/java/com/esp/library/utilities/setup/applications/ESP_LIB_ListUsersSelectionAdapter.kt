package utilities.adapters.setup.applications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView
import utilities.data.applicants.ESP_LIB_UsersListDAO
import utilities.interfaces.ESP_LIB_UserListClickListener


class ESP_LIB_ListUsersSelectionAdapter(private val userslist: List<ESP_LIB_UsersListDAO>?, con: ESP_LIB_BaseActivity)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListUsersSelectionAdapter.ParentViewHolder>() {


    private var context: ESP_LIB_BaseActivity
    var userItemClick: ESP_LIB_UserListClickListener? = null

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {


        internal var ivuser: NameInitialsCircleImageView
        internal var txtusername: TextView
        internal var txtemail: TextView
        internal var cbuser: AppCompatCheckBox
        internal var rlitem: RelativeLayout


        init {

            ivuser = itemView.findViewById(R.id.ivuser)
            cbuser = itemView.findViewById(R.id.cbuser)
            txtusername = itemView.findViewById(R.id.txtusername)
            txtemail = itemView.findViewById(R.id.txtemail)
            rlitem = itemView.findViewById(R.id.rlitem)
        }

    }


    init {
        context = con
    }

    fun getUserList(): List<ESP_LIB_UsersListDAO>? {
        return userslist
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_acitivity_user_selection_row, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val userslistDAO = userslist?.get(position)
        val holder = holder_parent as ActivitiesList
        holder.txtusername.text = userslistDAO?.fullName
        holder.txtemail.text = userslistDAO?.email

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

        holder.cbuser.setOnCheckedChangeListener(null);
        holder.cbuser.isChecked = userslistDAO.isChecked

        holder.cbuser.setOnCheckedChangeListener { buttonView, isChecked ->
            userslistDAO.isChecked = isChecked
        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return userslist?.size ?: 0

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


}
