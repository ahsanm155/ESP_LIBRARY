package com.esp.library.exceedersesp.controllers.signature

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_AttachmentItem
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_AttachmentImageDownload
import com.esp.library.ipaulpro.afilechooser.utils.FileUtils
import com.esp.library.utilities.common.ESP_LIB_RealPathUtil
import com.esp.library.utilities.common.ESP_LIB_Shared
import kotlinx.android.synthetic.main.esp_lib_activity_attachment_row.view.*
import kotlinx.android.synthetic.main.esp_lib_activity_upload_fragment.view.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import utilities.common.ESP_LIB_CommonMethodsKotlin
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import java.io.File
import java.io.IOException
import java.util.*

class ESP_LIB_UploadFragment : Fragment() {

    val TAG = javaClass.simpleName
    private val REQUEST_CHOOSER = 12345
    var ESPLIBDyanmicFormSectionFieldDetailsDAO: ESP_LIB_DyanmicFormSectionFieldDetailsDAO? = null
    internal var pDialog: android.app.AlertDialog? = null

    companion object {
        fun newInstance(): ESP_LIB_UploadFragment {
            val fragment = ESP_LIB_UploadFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.esp_lib_activity_upload_fragment, container, false)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(requireContext())
        view.llattachment.setOnClickListener {
            val getContentIntent = Intent(Intent.ACTION_GET_CONTENT)
            getContentIntent.type = "*/*"
            getContentIntent.addCategory(Intent.CATEGORY_OPENABLE)
            val intent = Intent.createChooser(getContentIntent, getString(R.string.esp_lib_text_selectafile))
            startActivityForResult(intent, REQUEST_CHOOSER)
        }

       /* view.rlattachmentdetails.setOnClickListener {
            // AttachmentImageDownload.getInstance().OpenImage(dyanmicFormSectionFieldDetailsDAO?.path, context)

            if (ESPLIBDyanmicFormSectionFieldDetailsDAO?.path.isNullOrEmpty()) {
                val getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(ESPLIBDyanmicFormSectionFieldDetailsDAO?.name)!!.path
                val isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, context)
                if (isFileExist)
                    ESP_LIB_AttachmentImageDownload.getInstance().OpenImage(getOutputMediaFile, context)
                else {
                    view.progressbar.visibility=View.VISIBLE
                    DownloadAttachment(ESPLIBDyanmicFormSectionFieldDetailsDAO, ESPLIBDyanmicFormSectionFieldDetailsDAO?.name!!)
                }
            } else
                ESP_LIB_AttachmentImageDownload.getInstance().OpenImage(ESPLIBDyanmicFormSectionFieldDetailsDAO?.path, context)


        }*/

        view.ivdots.setOnClickListener { v -> showRemoveMenu(v) }

        view.btsave.setOnClickListener{
            val fontFamily = ""
            val file = ESP_LIB_Shared.getInstance().prepareFilePart(ESPLIBDyanmicFormSectionFieldDetailsDAO?.uri, requireContext())
            ESP_LIB_CommonMethodsKotlin.upLoadSignature(requireContext(),file,getString(R.string.esp_lib_text_upload),"", fontFamily,pDialog)
        }

        return view
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHOOSER && data != null) {
                val uri = data.data
                if (uri != null) {

                    val path = ESP_LIB_RealPathUtil.getPath(requireContext(), uri)
                    val file = File(path)
                    val attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file)
                    view?.txtacctehmentname?.text = file.getName()
                    val extension = file.getName().substring(file.getName().lastIndexOf("."))
                    val fileSize = extension.replaceFirst(".".toRegex(), "").toUpperCase(Locale.getDefault()) + ", " + attachmentFileSize
                    view?.txtextensionsize?.text = fileSize
                    ESP_LIB_AttachmentItem.getInstance().setIconBasedOnMimeType(extension, view?.attachtypeicon)


                    ESPLIBDyanmicFormSectionFieldDetailsDAO = ESP_LIB_DyanmicFormSectionFieldDetailsDAO()
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.mimeType = FileUtils.getMimeType(file)
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.name = file.getName()
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.uri = uri
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.path = file.absolutePath
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.createdOn = ESP_LIB_Shared.getInstance().GetCurrentDateTime()
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.fileSize = attachmentFileSize
                    view?.attachmentlayout?.visibility = View.VISIBLE
                    view?.btsave?.setEnabled(true)
                    view?.btsave?.setAlpha(1f)
                    view?.btsave?.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green)
                    view?.llsignature?.visibility = View.VISIBLE
                    view?.txtpreview?.visibility = View.VISIBLE


                    Glide.with(requireContext())
                            .load(path)
                            .placeholder(R.drawable.esp_lib_drawable_default_profile_picture)
                            .into(view?.ivsignature!!)

                }
            }
        }
    }

    private fun DownloadAttachment(attachment: ESP_LIB_DyanmicFormSectionFieldDetailsDAO?,
                                   uploadedFileName: String) {

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
                    view?.progressbar?.visibility = View.GONE
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {

            }

        })


    }//End Download

    private fun showRemoveMenu(v: View) {


        val popup = PopupMenu(requireContext(), v)
        popup.inflate(R.menu.menu_list_item_add_application_fields)
        // val menuOpts = popup.menu
        popup.gravity = Gravity.CENTER

        popup.setOnMenuItemClickListener { menuItem ->

            val id = menuItem.itemId
            if (id == R.id.action_remove) {
                view?.llsignature?.visibility = View.GONE
                view?.txtpreview?.visibility = View.GONE
                view?.attachmentlayout?.visibility = View.GONE
                view?.btsave?.isEnabled = false
                view?.btsave?.alpha = 0.5f
                view?.btsave?.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button)
                ESPLIBDyanmicFormSectionFieldDetailsDAO = null
            }


            false
        }


        popup.show()

    }

}


