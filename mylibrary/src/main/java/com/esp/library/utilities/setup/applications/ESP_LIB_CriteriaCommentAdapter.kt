package com.esp.library.utilities.setup.applications

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_AttachmentItem
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_AttachmentImageDownload
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import okhttp3.OkHttpClient
import okhttp3.Request
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaCommentsListDAO
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ESP_LIB_CriteriaCommentAdapter(con: Context, commentlist: ArrayList<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>)
    : RecyclerView.Adapter<ESP_LIB_CriteriaCommentAdapter.ParentViewHolder>() {

    var context: Context
    var commentList: ArrayList<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>? = null


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {
        internal var txtcomment: TextView
        internal var txtvisibletoapplicant: TextView
        internal var txtacctehmentname: TextView
        internal var txtextensionsize: TextView
        internal var rlattachmentdetails: RelativeLayout
        internal var llattachmentlayout: LinearLayout
        internal var progressbar: ProgressBar
        internal var attachtypeicon: ImageView
        internal var ivdots: ImageView

        init {
            txtcomment = itemView.findViewById(R.id.txtcomment)
            txtvisibletoapplicant = itemView.findViewById(R.id.txtvisibletoapplicant)
            rlattachmentdetails = itemView.findViewById(R.id.rlattachmentdetails)
            llattachmentlayout = itemView.findViewById(R.id.llattachmentlayout)
            progressbar = itemView.findViewById(R.id.progressbar)
            txtacctehmentname = itemView.findViewById(R.id.txtacctehmentname)
            attachtypeicon = itemView.findViewById(R.id.attachtypeicon)
            txtextensionsize = itemView.findViewById(R.id.txtextensionsize)
            ivdots = itemView.findViewById(R.id.ivdots)
        }

    }


    init {
        context = con
        commentList = commentlist
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_activity_criteria_comment_row, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {


        val getComment = commentList?.get(position)
        val holder = holder_parent as ActivitiesList


        holder.txtcomment.text = getComment?.comment

        if (getComment!!.isVisibletoApplicant)
            holder.txtvisibletoapplicant.visibility = View.VISIBLE



        holder.ivdots.visibility = View.GONE

        if (getComment.attachments == null) {
            holder.llattachmentlayout.visibility = View.GONE
        } else
            populateImageData(getComment, holder)


        holder.rlattachmentdetails.setOnClickListener {

            if (getComment.attachments?.get(0)?.path.isNullOrEmpty()) {
                val getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(getComment.attachments?.get(0)?.name)!!.path
                val isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, context)
                if (isFileExist)
                    ESP_LIB_AttachmentImageDownload.getInstance().OpenImage(getOutputMediaFile, context)
                else {
                    holder.progressbar.visibility = View.VISIBLE
                    DownloadAttachment(getComment.attachments?.get(0), getComment.attachments?.get(0)?.name!!, holder)
                }
            } else
                ESP_LIB_AttachmentImageDownload.getInstance().OpenImage(getComment.attachments?.get(0)?.path, context)

        }

    }//End Holder Class


    private fun populateImageData(comment: ESP_LIB_DynamicStagesCriteriaCommentsListDAO, holder: ActivitiesList) {
        val attachments = comment.attachments


        //Setting pre-filled Value. If Have
        var uploadedFileName: String? = ""
        if (attachments != null && attachments.get(0).name != null &&
                !TextUtils.isEmpty(attachments.get(0).name)) {
            uploadedFileName = attachments.get(0).name
        }

        val getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(attachments?.get(0)?.name)!!.path
        val isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, context)
        if(isFileExist)
        {
            val file = File(getOutputMediaFile)
            val attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file)
            attachments?.get(0)?.fileSize=attachmentFileSize
            holder.txtacctehmentname.text = file.getName()
        }

        if (!TextUtils.isEmpty(uploadedFileName)) {
            val extension: String? = uploadedFileName?.substring(uploadedFileName.lastIndexOf("."))
            var fileSize = extension?.replaceFirst(".".toRegex(), "")
            if (attachments?.get(0)?.fileSize != null)
                fileSize = extension?.replaceFirst(".".toRegex(), "")?.toUpperCase() + ", " + attachments.get(0).fileSize
            holder.txtextensionsize.setText(fileSize)
            ESP_LIB_AttachmentItem.getInstance().setIconBasedOnMimeType(extension, holder.attachtypeicon)
            holder.txtacctehmentname.setText(uploadedFileName)

        }

    }

    private fun DownloadAttachment(attachment: ESP_LIB_DyanmicFormSectionFieldDetailsDAO?,
                                   uploadedFileName: String,
                                   holder: ActivitiesList) {

        val client = OkHttpClient()
        var imgURL: String? = ""

        if (attachment?.downloadUrl != null && attachment.downloadUrl!!.length > 0) {
            imgURL = attachment.downloadUrl
        }

        val request = Request.Builder()
                .url(imgURL!!)
                .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {


            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

                if (response.isSuccessful) {
                    val attachmentsDAO = ESP_LIB_AttachmentImageDownload.getInstance().DownloadImage(response.body()?.byteStream(),
                            attachment, uploadedFileName, null, null, 0)
                }

                val handler = Handler(Looper.getMainLooper()) // write in onCreate function
                handler.post {
                    holder.progressbar.visibility = View.GONE
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {

            }

        })


    }//End Download


    override fun getItemCount(): Int {
        return commentList?.size ?: 0

    }


    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    companion object {

        private val LOG_TAG = "ESP_LIB_CriteriaCommentAdapter"


    }


}
