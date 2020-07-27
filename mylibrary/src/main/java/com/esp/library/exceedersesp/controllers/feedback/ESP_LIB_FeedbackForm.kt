package com.esp.library.exceedersesp.controllers.feedback

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ActivityStageDetails
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_AttachmentItem
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_AttachmentImageDownload
import com.esp.library.ipaulpro.afilechooser.utils.FileUtils
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_RealPathUtil
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.google.gson.Gson
import kotlinx.android.synthetic.main.esp_lib_activity_attachment_row.*
import kotlinx.android.synthetic.main.esp_lib_activity_feedback_form.*
import kotlinx.android.synthetic.main.esp_lib_feedback_add_section.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.data.CriteriaRejectionfeedback.ESP_LIB_FeedbackDAO
import utilities.data.applicants.addapplication.ESP_LIB_PostApplicationsStatusDAO
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.interfaces.ESP_LIB_FeedbackConfirmationListener
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ESP_LIB_FeedbackForm : ESP_LIB_BaseActivity(), ESP_LIB_FeedbackConfirmationListener {


    var ESPLIBDyanmicFormSectionFieldDetailsDAO: ESP_LIB_DyanmicFormSectionFieldDetailsDAO? = null
    var context: ESP_LIB_BaseActivity? = null
    private val REQUEST_CHOOSER = 12345
    internal var pDialog: AlertDialog? = null
    val feedbackList = ArrayList<ESP_LIB_FeedbackDAO>()
    var actualResponseESPLIB: ESP_LIB_DynamicResponseDAO? = null
    var pref: ESP_LIB_SharedPreference? = null
    var isApproveClick: Boolean = false
    var isVisibleToApplicant: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_feedback_form)
        initialize()


        if (pref?.selectedUserRole.equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true))
            cbvisvibeapplicant.visibility = View.GONE


        llattachment.setOnClickListener {
            val getContentIntent = Intent(Intent.ACTION_GET_CONTENT)
            getContentIntent.type = "*/*"
            getContentIntent.addCategory(Intent.CATEGORY_OPENABLE)
            val intent = Intent.createChooser(getContentIntent, getString(R.string.esp_lib_text_selectafile))
            startActivityForResult(intent, REQUEST_CHOOSER)
        }

        ivdots.setOnClickListener { v -> showRemoveMenu(v) }
        ivcross.setOnClickListener { onBackPressed() }



        txtcomment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {

                val outputedText = editable.toString()

                when (outputedText.isNotEmpty()) {
                    true -> {
                        if (!isApproveClick) {
                            btcancel.isEnabled = true
                            btcancel.alpha = 1f
                        }
                    }
                    false -> {
                        if (!isApproveClick) {
                            btcancel.isEnabled = false
                            btcancel.alpha = 0.5f
                        }
                    }

                }

            }
        })

        cbvisvibeapplicant.setOnCheckedChangeListener { buttonView, isChecked ->
            isVisibleToApplicant = isChecked
        }


        rlattachmentdetails.setOnClickListener {

            if (ESPLIBDyanmicFormSectionFieldDetailsDAO?.path.isNullOrEmpty()) {
                val getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(ESPLIBDyanmicFormSectionFieldDetailsDAO?.name)!!.path
                val isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, context)
                if (isFileExist)
                    ESP_LIB_AttachmentImageDownload.getInstance().OpenImage(getOutputMediaFile, context)
                else {
                    progressbar.visibility = View.VISIBLE
                    DownloadAttachment(ESPLIBDyanmicFormSectionFieldDetailsDAO, ESPLIBDyanmicFormSectionFieldDetailsDAO?.name!!)
                }
            } else
                ESP_LIB_AttachmentImageDownload.getInstance().OpenImage(ESPLIBDyanmicFormSectionFieldDetailsDAO?.path, context)

        }



        btconfirm.setOnClickListener {
            if (isApproveClick)
                actionOnUpload()
            else
                actionOnCancel()
        }

        btcancel.setOnClickListener {

            if (!isApproveClick)
                actionOnUpload()
            else
                actionOnCancel()
        }


    }


    private fun initialize() {
        context = this
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(bContext)
        pref = ESP_LIB_SharedPreference(context)
        isApproveClick = intent.getBooleanExtra("isAccepted", false)
        rlattachmentdetails.visibility = View.GONE

        if (!isApproveClick) {
            txtheading.text = getString(R.string.esp_lib_text_rejectcriteria)
            btcancel.isEnabled = false
            btcancel.alpha = 0.5f
            btcancel.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green)
            btconfirm.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green_stroke)
            btcancel.text = getString(R.string.esp_lib_text_reject)
            btconfirm.text = getString(R.string.esp_lib_text_cancel)
            btconfirm.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            btcancel.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_white))
        } else if (intent.getBooleanExtra("isAddCriteria", false)) {
            txtheading.text = getString(R.string.esp_lib_text_add_comment)
            btconfirm.text = getString(R.string.esp_lib_text_add_comment)
        }

        val actualResponseJson = intent.getStringExtra("actualResponseJson")
        actualResponseESPLIB = Gson().fromJson<ESP_LIB_DynamicResponseDAO>(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)

    }

    private fun actionOnUpload() {

        when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            true -> {
                if (ESPLIBDyanmicFormSectionFieldDetailsDAO != null)
                    updateLoadImageForField(ESPLIBDyanmicFormSectionFieldDetailsDAO?.uri!!)
                else {
                    UpLoadFile(null)
                }
            }
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }

    }


    private fun actionOnCrieria() {
        val dynamicStagesCriteriaListDAO = intent.getSerializableExtra("criteriaListDAO") as ESP_LIB_DynamicStagesCriteriaListDAO
        val criteriaFormValues = getCriteriaFormValues(dynamicStagesCriteriaListDAO)
        val post = ESP_LIB_PostApplicationsStatusDAO()
        post.isAccepted = isApproveClick
        post.applicationId = actualResponseESPLIB?.applicationId!!
        post.assessmentId = dynamicStagesCriteriaListDAO.assessmentId

        post.comments = ""
        post.isVisibletoApplicant = isVisibleToApplicant
        post.stageId = dynamicStagesCriteriaListDAO.stageId
        post.values = criteriaFormValues


        when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            true -> stagefeedbackSubmitForm(post)
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }
    }

    private fun actionOnCancel() {
        onBackPressed()
        /*ESPLIBDyanmicFormSectionFieldDetailsDAO = null
        rlattachmentdetails.visibility = View.GONE
        txtcomment.setText("")
        rvCommentsList.visibility = View.VISIBLE
        btaddcomment.text = getString(R.string.esp_lib_text_addcomment)
        txtheading.text = getString(R.string.esp_lib_text_confirmfeedback)*/
    }

    private fun getCriteriaFormValues(criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO): List<ESP_LIB_DynamicFormValuesDAO> {
        var sectionId = 0
        val formValuesList = java.util.ArrayList<ESP_LIB_DynamicFormValuesDAO>()
        val form = criteriaListDAOESPLIB.form
        if (form.sections != null) {
            for (sections in form.sections!!) {
                sectionId = sections.id
                if (sections.fields != null) {
                    for (dynamicFormSectionFieldDAO in sections.fields!!) {
                        val value = ESP_LIB_DynamicFormValuesDAO()
                        value.sectionCustomFieldId = dynamicFormSectionFieldDAO.sectionCustomFieldId
                        value.type = dynamicFormSectionFieldDAO.type
                        value.value = dynamicFormSectionFieldDAO.value
                        value.sectionId = sectionId
                        value.details = value.details
                        if (dynamicFormSectionFieldDAO.type == 11) {
                            var finalValue = value.value
                            if (finalValue != null && !finalValue.isEmpty())
                                finalValue += ":" + dynamicFormSectionFieldDAO.selectedCurrencyId + ":" + dynamicFormSectionFieldDAO.selectedCurrencySymbol

                            value.value = finalValue
                        }
                        formValuesList.add(value)
                    }
                }
            }
        }

        return formValuesList
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
                    progressbar.visibility = View.GONE
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {

            }

        })


    }//End Download

    private fun showRemoveMenu(v: View) {


        val popup = PopupMenu(context!!, v)
        popup.inflate(R.menu.menu_list_item_add_application_fields)
        // val menuOpts = popup.menu
        popup.gravity = Gravity.CENTER

        popup.setOnMenuItemClickListener { menuItem ->

            val id = menuItem.itemId
            if (id == R.id.action_remove) {
                rlattachmentdetails.visibility = View.GONE
                llattachment.visibility = View.VISIBLE
                ESPLIBDyanmicFormSectionFieldDetailsDAO = null
            }


            false
        }


        popup.show()

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHOOSER && data != null) {
                val uri = data.data
                if (uri != null) {
                    val path = ESP_LIB_RealPathUtil.getPath(bContext, uri)
                    val file = File(path)
                    val attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file)
                    txtacctehmentname.text = file.getName()
                    val extension = file.getName().substring(file.getName().lastIndexOf("."))
                    val fileSize = extension.replaceFirst(".".toRegex(), "").toUpperCase() + ", " + attachmentFileSize
                    txtextensionsize.text = fileSize
                    ESP_LIB_AttachmentItem.getInstance().setIconBasedOnMimeType(extension, attachtypeicon)


                    ESPLIBDyanmicFormSectionFieldDetailsDAO = ESP_LIB_DyanmicFormSectionFieldDetailsDAO()
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.mimeType = FileUtils.getMimeType(file)
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.name = file.getName()
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.uri = uri
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.path = file.absolutePath
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.createdOn = ESP_LIB_Shared.getInstance().GetCurrentDateTime()
                    ESPLIBDyanmicFormSectionFieldDetailsDAO?.fileSize = attachmentFileSize
                    rlattachmentdetails.visibility = View.VISIBLE
                    llattachment.visibility = View.GONE
                }
            }
        }
    }

    fun updateLoadImageForField(uri: Uri) {


        var body: MultipartBody.Part? = null

        try {

            body = ESP_LIB_Shared.getInstance().prepareFilePart(uri, bContext)
            UpLoadFile(body)

        } catch (e: Exception) {
            ESP_LIB_Shared.getInstance().errorLogWrite("FILE", e.message)
        }

    }

    private fun UpLoadFile(body: MultipartBody.Part?) {

        start_loading_animation()
        try {
            val dynamicStagesCriteriaListDAO = intent.getSerializableExtra("criteriaListDAO") as ESP_LIB_DynamicStagesCriteriaListDAO
            val UserComments = RequestBody.create(MediaType.parse("text/plain"), txtcomment.text.toString())
            //val assessmentId = RequestBody.create(MediaType.parse("text/plain"), dynamicStagesCriteriaListDAO.assessmentId)

            val call_upload = ESP_LIB_Shared.getInstance().retroFitObject(context).feedbackComments(body, actualResponseESPLIB?.applicationId!!,
                    UserComments, dynamicStagesCriteriaListDAO.assessmentId, isVisibleToApplicant)
            call_upload.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>?) {

                    if (response != null && response.body() != null) {
                        rlattachmentdetails.visibility = View.GONE
                        ESPLIBDyanmicFormSectionFieldDetailsDAO = null
                        // stop_loading_animation()
                        if (txtheading.text.toString().equals(getString(R.string.esp_lib_text_add_comment), ignoreCase = true)) {
                            stop_loading_animation()
                            ESP_LIB_ActivityStageDetails.isGoBAck = true
                            isComingFromFeedbackFrom = true
                            onBackPressed()
                        } else
                            actionOnCrieria()

                    } else {
                        stop_loading_animation()
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                    }

                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)

        }

    }//LoggedInUser end


    fun stagefeedbackSubmitForm(ESPLIBPost: ESP_LIB_PostApplicationsStatusDAO) {
        start_loading_animation()
        try {


            val status_call = ESP_LIB_Shared.getInstance().retroFitObject(context).AcceptRejectApplication(ESPLIBPost)


            status_call.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    stop_loading_animation()
                    ESP_LIB_ActivityStageDetails.isGoBAck = true
                    isComingFromFeedbackFrom = true
                    onBackPressed()

                }

                override fun onFailure(call: Call<Int>, t: Throwable?) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)

        }

    }

    private fun start_loading_animation() {

        if (!pDialog!!.isShowing())
            pDialog?.show()


    }

    private fun stop_loading_animation() {

        if (pDialog!!.isShowing())
            pDialog?.dismiss()


    }

    override fun isClickable(ESPLIBFeedbackList: List<ESP_LIB_FeedbackDAO>) {

        if (!isApproveClick)
            validateFields(ESPLIBFeedbackList)

    }

    companion object {

        var isComingFromFeedbackFrom: Boolean = false


    }


    override fun editComment(ESPLIBFeedbackDAO: ESP_LIB_FeedbackDAO) {

        txtcomment.setText(ESPLIBFeedbackDAO.comment)
        txtheading.text = getString(R.string.esp_lib_text_edit_feedback)


        /*val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )

        btconfirm.setPadding(30,0,30,0)
        btconfirm.layoutParams = params;*/


        val fileName = ESPLIBFeedbackDAO.attachemntDetails?.name
        txtacctehmentname.text = fileName
        val extension = fileName?.substring(fileName.lastIndexOf("."))
        ESP_LIB_AttachmentItem.getInstance().setIconBasedOnMimeType(extension, attachtypeicon)
        val extensionToShow = extension?.replaceFirst(".".toRegex(), "")?.toUpperCase()
        txtextensionsize.text = extensionToShow

        ESPLIBDyanmicFormSectionFieldDetailsDAO = ESP_LIB_DyanmicFormSectionFieldDetailsDAO()
        ESPLIBDyanmicFormSectionFieldDetailsDAO?.mimeType = ESPLIBFeedbackDAO.attachemntDetails?.mimeType
        ESPLIBDyanmicFormSectionFieldDetailsDAO?.name = ESPLIBFeedbackDAO.attachemntDetails?.name
        ESPLIBDyanmicFormSectionFieldDetailsDAO?.uri = ESPLIBFeedbackDAO.attachemntDetails?.uri
        ESPLIBDyanmicFormSectionFieldDetailsDAO?.path = ESPLIBFeedbackDAO.attachemntDetails?.path
        ESPLIBDyanmicFormSectionFieldDetailsDAO?.createdOn = ESP_LIB_Shared.getInstance().GetCurrentDateTime()
        ESPLIBDyanmicFormSectionFieldDetailsDAO?.fileSize = ESPLIBFeedbackDAO.attachemntDetails?.fileSize
        rlattachmentdetails.visibility = View.VISIBLE

    }


    private fun validateFields(ESPLIBFeedbackList: List<ESP_LIB_FeedbackDAO>) {
        for (i in 0 until this.feedbackList.size) {
            if (this.feedbackList.get(i).isCheck) {
                btconfirm.isEnabled = true
                btconfirm.alpha = 1.0f
                break
            } else {
                btconfirm.isEnabled = false
                btconfirm.alpha = 0.5f
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        ESP_LIB_Shared.getInstance().hideKeyboard(context)
    }

}
