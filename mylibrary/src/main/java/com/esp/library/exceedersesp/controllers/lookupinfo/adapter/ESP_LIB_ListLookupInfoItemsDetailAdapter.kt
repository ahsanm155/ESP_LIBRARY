package com.esp.library.exceedersesp.controllers.lookupinfo.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import okhttp3.OkHttpClient
import okhttp3.Request
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO
import java.io.File
import java.io.IOException
import java.util.*

class ESP_LIB_ListLookupInfoItemsDetailAdapter(lookupInfoList: List<ESP_LIB_DynamicFormSectionFieldDAO>, context: ESP_LIB_BaseActivity) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListLookupInfoItemsDetailAdapter.ViewHolder>() {

    private val TAG = "ListLookupInfoItemsDetailAdapter"
    internal var pref: ESP_LIB_SharedPreference
    internal var lookupInfoDetailList: List<ESP_LIB_DynamicFormSectionFieldDAO> = ArrayList()
    private val context: ESP_LIB_BaseActivity

    init {
        this.context = context
        this.lookupInfoDetailList = lookupInfoList
        pref = ESP_LIB_SharedPreference(context)
    }

    inner class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        internal var txtlabel: TextView
        internal var txtvalue: TextView
        internal var is_file_downloaded: ImageView
        internal var ivicon: ImageView
        internal var progressbar: ProgressBar
        internal var progresslayout: RelativeLayout

        init {
            txtlabel = itemView.findViewById(R.id.txtlabel)
            txtvalue = itemView.findViewById(R.id.txtvalue)
            is_file_downloaded = itemView.findViewById(R.id.is_file_downloaded)
            ivicon = itemView.findViewById(R.id.ivicon)
            progressbar = itemView.findViewById(R.id.progressbar)
            progresslayout = itemView.findViewById(R.id.progresslayout)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_lookup_item_detail_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dynamicFormValuesDAO = lookupInfoDetailList[position]
        holder.ivicon.visibility = View.GONE
        holder.txtlabel.text = dynamicFormValuesDAO.label
        if (dynamicFormValuesDAO.type == 7) {
            holder.txtvalue.text = dynamicFormValuesDAO.details?.name
            if (dynamicFormValuesDAO.details?.name != null)
                downloadImage(holder, dynamicFormValuesDAO.details!!, dynamicFormValuesDAO.details?.name!!)
        } else if (dynamicFormValuesDAO.type == 4) {
            val displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, dynamicFormValuesDAO.value, true)
            holder.txtvalue.text = displayDate
        } else if (dynamicFormValuesDAO.type == 17) {

            if (dynamicFormValuesDAO.value == null || dynamicFormValuesDAO.value?.replace("\\s", "")?.length == 0)
                holder.txtvalue.visibility = View.GONE
            else
                holder.txtvalue.text = dynamicFormValuesDAO.value
            holder.ivicon.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivicon.setImageDrawable(context.getDrawable(R.drawable.esp_lib_drawable_ic_show_rollup))
            }
        } else if (dynamicFormValuesDAO.type == 18) {
            if (dynamicFormValuesDAO.value == null || dynamicFormValuesDAO.value?.replace("\\s", "")?.length == 0)
                holder.txtvalue.visibility = View.GONE
            else
                holder.txtvalue.text = dynamicFormValuesDAO.value?.trim()

            holder.ivicon.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivicon.setImageDrawable(context.getDrawable(R.drawable.esp_lib_drawable_ic_show_calculated))
            }
        } else {
            holder.txtvalue.text = dynamicFormValuesDAO.value
        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return lookupInfoDetailList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun downloadImage(holder: ViewHolder, getdetails: ESP_LIB_DyanmicFormSectionFieldDetailsDAO, uploadedFileName: String) {
        val getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(uploadedFileName)!!.path
        val isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, context)
        ESP_LIB_CustomLogs.displayLogs("$TAG getOutputMediaFile: $getOutputMediaFile isFileExist: $isFileExist")
        //if (Shared.getInstance().isFileExist(Constants.FOLDER_PATH + "/" + Constants.FOLDER_NAME, mApplications.get(position).getFieldvalue(), context)) {
        if (isFileExist) {
            holder.is_file_downloaded.visibility = View.GONE
        } else {
            holder.is_file_downloaded.visibility = View.VISIBLE
        }


        if (getdetails.isFileDownling) {
            holder.progressbar.visibility = View.VISIBLE
            holder.is_file_downloaded.visibility = View.GONE
        } else {
            holder.progressbar.visibility = View.GONE

            // if (Shared.getInstance().isFileExist(Constants.FOLDER_PATH + "/" + Constants.FOLDER_NAME, mApplications.get(position).getFieldvalue(), context)) {
            if (isFileExist) {
                holder.is_file_downloaded.visibility = View.GONE
            } else {
                holder.is_file_downloaded.visibility = View.VISIBLE
            }
        }


        holder.txtvalue.setOnClickListener {
            if (getdetails.isFileDownloaded) {

                //  if (Shared.getInstance().isFileExist(Constants.FOLDER_PATH + "/" + Constants.FOLDER_NAME, mApplications.get(position).getFieldvalue(), context)) {
                if (isFileExist) {
                    //OpenFile(Constants.FOLDER_PATH + "/" + Constants.FOLDER_NAME + "/" + mApplications.get(position).getFieldvalue());

                    OpenImage(getOutputMediaFile)

                } else {

                    if (getdetails.downloadUrl!!.length > 0) {
                        downloadAttachment(holder, getdetails, uploadedFileName)
                    }

                }

            } else {

                if (isFileExist) {
                    //OpenFile(Constants.FOLDER_PATH + "/" + Constants.FOLDER_NAME + "/" + mApplications.get(position).getFieldvalue());
                    OpenImage(getOutputMediaFile)

                } else if (getdetails.downloadUrl != null && getdetails.downloadUrl!!.length > 0) {
                    downloadAttachment(holder, getdetails, uploadedFileName)
                }
            }

            //Shared.getInstance().messageBox(mApplications.get(position).getPhoto_detail().getDownloadUrl(),context);
        }
    }

    private fun downloadAttachment(holder: ViewHolder, getdetails: ESP_LIB_DyanmicFormSectionFieldDetailsDAO, uploadedFileName: String) {
        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            holder.is_file_downloaded.visibility = View.GONE
            holder.progressbar.visibility = View.VISIBLE
            DownloadAttachment(holder, getdetails, uploadedFileName)
        } else {

            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_internet_error_heading), context.getString(R.string.esp_lib_text_internet_connection_error), context)
        }
    }

    private fun OpenImage(filePath: String?) {
        val file = File(filePath)
        var filePath = filePath
        try {
            filePath = "file://" + filePath!!
            ESP_LIB_CustomLogs.displayLogs("$TAG OpenFile filePath: $filePath")
            if (filePath != null) {

                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())

                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val apkURI = FileProvider.getUriForFile(Objects.requireNonNull(context),
                            "com.exceedersesp.provider", file)
                    val myMime = MimeTypeMap.getSingleton()
                    val mimeType = myMime.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(apkURI.toString())) //It will return the mimetype
                    intent.setDataAndType(apkURI, mimeType)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(intent)
                } catch (e: java.lang.Exception) {
                    ESP_LIB_Shared.getInstance().messageBox(context.getString(R.string.esp_lib_text_noapp_open_attachmet), context as Activity?)
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(Uri.parse(filePath), "*/*")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }


        } catch (e: Exception) {
            // Shared.getInstance().messageBox("Open this file, Application is not available", (Activity) mContext);
            e.printStackTrace()
        }

    }

    private fun DownloadAttachment(holder: ViewHolder, attachment: ESP_LIB_DyanmicFormSectionFieldDetailsDAO, uploadedFileName: String) {

        val client = OkHttpClient()
        var imgURL = ""

        if (attachment.downloadUrl != null && attachment.downloadUrl!!.length > 0) {
            imgURL = attachment.downloadUrl!!
        }

        ESP_LIB_CustomLogs.displayLogs("$TAG imgURL: $imgURL")

        val request = Request.Builder()
                .url(imgURL)
                .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {


            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

                if (!response.isSuccessful) {
                    attachment.isFileDownloaded = false
                    attachment.isFileDownling = false
                    attachment.isFileDownloaded = false
                    holder.is_file_downloaded.visibility = View.VISIBLE
                    holder.progressbar.visibility = View.GONE
                } else {

                    val isRefresh = ESP_LIB_Shared.getInstance().downloadImage(response.body()?.byteStream(), attachment, uploadedFileName)

                    val handler = Handler(Looper.getMainLooper()) // write in onCreate function

                    handler.post {
                        if (isRefresh) {
                            attachment.isFileDownloaded = true
                            attachment.isFileDownling = false

                        } else {
                            attachment.isFileDownling = false
                            attachment.isFileDownloaded = false
                        }
                        notifyDataSetChanged()
                    }


                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {

                attachment.isFileDownloaded = false
                attachment.isFileDownling = false
                attachment.isFileDownloaded = false
            }

        })


    }//End Download


}
