package com.esp.library.exceedersesp.controllers.applications

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.exceedersesp.controllers.feedback.ESP_LIB_FeedbackForm
import com.esp.library.utilities.common.*
import com.esp.library.utilities.customevents.EventOptions
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationCriteriaAdapter
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.esp_lib_activity_stage_detail.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import kotlinx.android.synthetic.main.esp_lib_statuswithicon.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.common.ESP_LIB_CommonMethodsKotlin
import utilities.data.applicants.ESP_LIB_CalculatedMappedFieldsDAO
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO
import utilities.data.applicants.addapplication.ESP_LIB_PostApplicationsStatusDAO
import utilities.data.applicants.dynamics.*
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener
import utilities.interfaces.ESP_LIB_FeedbackSubmissionClick
import java.io.File
import java.util.*


class ESP_LIB_ActivityStageDetails : ESP_LIB_BaseActivity(), ESP_LIB_CriteriaFieldsListener,
        ESP_LIB_FeedbackSubmissionClick, ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationDetailFieldsAdapterListener {

    var TAG: String = "ActivityStageDetails"

    var context: ESP_LIB_BaseActivity? = null
    var criteriaAdapterESPLIB: ESP_LIB_ApplicationCriteriaAdapter? = null;
    var dynamicStagesDAO: ESP_LIB_DynamicStagesDAO? = null
    var criteriaListCollections = ArrayList<ESP_LIB_DynamicStagesCriteriaListDAO?>()
    internal var fieldToBeUpdatedESPLIB: ESP_LIB_DynamicFormSectionFieldDAO? = null
    internal var pDialog: AlertDialog? = null
    private val REQUEST_CHOOSER = 12345
    private val REQUEST_LOOKUP = 2
    private val VERIFY_RESULT = 3
    var pref: ESP_LIB_SharedPreference? = null
    var actualResponseJson: String? = null
    var actualResponseJsonsubmitJsonESPLIB: ESP_LIB_DynamicResponseDAO? = null
    var actualResponseJsonsubmitJsonTempESPLIB: ESP_LIB_DynamicResponseDAO? = null
    internal var isCalculatedField: Boolean = false
    internal var isKeyboardVisible: Boolean = false
    var isAccepted: Boolean = false
    var criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(true)
        setContentView(R.layout.esp_lib_activity_stage_detail)
        initailize()
        updateTopView()
        setGravity()
        if (Build.VERSION.SDK_INT >= 24) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }// for camera above API 24
        if (!ESP_LIB_CommonMethodsKotlin.checkPermission(context!!)) {
            ESP_LIB_CommonMethodsKotlin.requestPermission(context!!)
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        //  criteriaListCollections.clear()
        for (i in 0 until dynamicStagesDAO?.criteriaList!!.size) {
            val getList = dynamicStagesDAO?.criteriaList?.get(i);
            val isArrayHasValue = criteriaListCollections.any { x -> x?.assessmentId == getList?.assessmentId }
            if (!isArrayHasValue) {
                criteriaListCollections.add(getList)
              /*  if (getList?.isEnabled!!)
                    criteriaListCollections.add(getList)*/
            }
        }

        when (criteriaListCollections.size == 0) {
            true -> txtcriteria.visibility = View.GONE
        }

        rvCrietrias.setHasFixedSize(true)
        rvCrietrias.isNestedScrollingEnabled = false
        rvCrietrias.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        criteriaAdapterESPLIB = ESP_LIB_ApplicationCriteriaAdapter(criteriaListCollections, context!!, rvCrietrias)
        criteriaAdapterESPLIB?.getStage(dynamicStagesDAO!!)
        criteriaAdapterESPLIB?.getActualResponse(intent.getStringExtra("actualResponseJson"))
        rvCrietrias.adapter = criteriaAdapterESPLIB

        /*start_loading_animation()
        val handler = Handler()
        handler.postDelayed({ stop_loading_animation() }, 3000)
*/
        ESP_LIB_KeyboardUtils.addKeyboardToggleListener(bContext) { isVisible -> isKeyboardVisible = isVisible }

        /* when (ESP_LIB_Shared.getInstance().isWifiConnected(this)) {
             true -> getSignature()
             false -> ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), this)
         }*/


    }

    private fun initailize() {
        context = this
        if (ESP_LIB_ApplicationDetailScreenActivity.criteriaWasLoaded)
            ESP_LIB_ApplicationDetailScreenActivity.criteriaWasLoaded = false
        pref = ESP_LIB_SharedPreference(context)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(bContext)
        actualResponseJson = intent.getStringExtra("actualResponseJson")
        actualResponseJsonsubmitJsonESPLIB = Gson().fromJson<ESP_LIB_DynamicResponseDAO>(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)
        setSupportActionBar(gradienttoolbar)
        ibToolbarBack.setOnClickListener { onBackPressed() }
        toolbarheading.text = getString(R.string.esp_lib_text_approvalstage)
    }


    private fun updateTopView() {
        dynamicStagesDAO = intent.getSerializableExtra("dynamicStagesDAO") as ESP_LIB_DynamicStagesDAO
        txtStagename.text = dynamicStagesDAO?.name
        sequencetextvalue.text = dynamicStagesDAO?.order.toString()

        if (dynamicStagesDAO?.type.equals(getString(R.string.esp_lib_text_link), ignoreCase = true)) {
            rlcondition.visibility = View.GONE
            rlacceptreject.visibility = View.GONE
        } else {
            if (dynamicStagesDAO != null && dynamicStagesDAO!!.isAll) {

                conditiontextvalue.text = context?.getString(R.string.esp_lib_text_all)
            } else {
                conditiontextvalue.text = context?.getString(R.string.esp_lib_text_any)
            }

            if (dynamicStagesDAO?.criteriaList != null && dynamicStagesDAO?.criteriaList!!.isNotEmpty()) {
                acceptencetextvalue.text = dynamicStagesDAO?.criteriaList!!.size.toString()
            } else {
                acceptencetextvalue.text = "0"
            }
        }

        val status = intent.getStringExtra("status")
        setStatusColor(status)

    }


    override fun validateCriteriaFields(ESPLIBDynamicStagesCriteriaList: ESP_LIB_DynamicStagesCriteriaListDAO) {

        var adapter_list: List<ESP_LIB_DynamicFormSectionFieldDAO>? = null
        if (criteriaAdapterESPLIB != null) {
            adapter_list = criteriaAdapterESPLIB?.getAllCriteriaFields()
        }
        var isAllFieldsValidateTrue = true

        val criteriaId = ESPLIBDynamicStagesCriteriaList.id

        for (i in 0 until ESPLIBDynamicStagesCriteriaList.form.sections!!.size) {
            val dynamicFormSectionDAO = ESPLIBDynamicStagesCriteriaList.form.sections!![i]

            for (k in 0 until dynamicFormSectionDAO.fields!!.size) {
                val id = dynamicFormSectionDAO.fields!![k].id

                if (adapter_list != null && adapter_list.size > 0) {

                    for (dynamicFormSectionFieldDAO in adapter_list) {

                        if (dynamicFormSectionFieldDAO.id == id) {

                            if (dynamicFormSectionFieldDAO.isRequired) {
                                if (!dynamicFormSectionFieldDAO.isValidate) {
                                    isAllFieldsValidateTrue = false
                                    break
                                }

                            }


                        }
                    }
                }
            }


        }

        for (q in criteriaAdapterESPLIB?.criteriaListESPLIB!!.indices) {
            val id = criteriaAdapterESPLIB?.criteriaListESPLIB?.get(q)?.id
            if (criteriaId == id) {
                criteriaAdapterESPLIB?.criteriaListESPLIB!!.get(q)?.isValidate = isAllFieldsValidateTrue
                try {
                    criteriaAdapterESPLIB?.notifyChangeIfAny(criteriaId,criteriaAdapterESPLIB?.criteriaListESPLIB!!.get(q))
                } catch (e: Exception) {

                }
            }

        }

    }

    override fun onFieldValuesChanged() {

    }


    override fun feedbackClick(isApproved: Boolean, criteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO?, ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO?, position: Int) {
        isAccepted = isApproved
        criteriaListDAOESPLIB = criteriaListDAO


        //showCmaeraVerificationAlert(getString(R.string.app_name), getString(R.string.esp_lib_text_need_to_verify), this)



        if (criteriaListDAO!!.isSigned)
            ESP_LIB_CommonMethodsKotlin.verificationPopUpPopulation(actualResponseJson, context)
        else
            startFeebform()

        /* var isApproved = isAccepted

        if (ESPLIBDynamicStagesDAO != null) {
            val count: Int = 0
            val dynamicStagesDAO1 = actualResponseJsonsubmitJsonESPLIB?.stages?.get(actualResponseJsonsubmitJsonESPLIB?.stages!!.size - 1)

            if (ESPLIBDynamicStagesDAO.id == dynamicStagesDAO1?.id) {
                val size = dynamicStagesDAO1.criteriaList!!.size
                if (ESPLIBDynamicStagesDAO.isAll) // if stage status is ALL then take feedback only on last criteria weather approve or reject
                {

                    //if last stage and last criteria then open feedback on approve button
                    //if last stage and any criteria then open feedback on reject button

                    val getCount = criteriaCount(dynamicStagesDAO1, count, size)
                    if (getCount == size - 1 && isApproved)
                        isApproved = false

                } else {

                    //if last stage and last criteria then open feedback on reject button
                    //if last stage and any criteria then open feedback on approve button

                    if (!isApproved) {
                        val getCount = criteriaCount(dynamicStagesDAO1, count, size)
                        if (getCount != size - 1)
                            isApproved = true
                    } else if (ESPLIBDynamicStagesDAO.id == dynamicStagesDAO1.id) {
                        isApproved = false
                    }
                }
            }


            if (ESPLIBDynamicStagesDAO.isLast)
                isApproved = false
        }




       isApproved=false

        if (!isApproved) {
            val intent = Intent(this, ESP_LIB_FeedbackForm::class.java)
            intent.putExtra("actualResponseJson", actualResponseJson)
            intent.putExtra("criteriaListDAO", criteriaListDAOESPLIB)
            intent.putExtra("isAccepted", isAccepted)
            startActivity(intent)
        } else {
            // CustomLogs.displayLogs(TAG+" Approved")

            SubmitStageRequest(isAccepted, criteriaListDAOESPLIB!!, position)
        }*/
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            VERIFY_RESULT ->
                if (resultCode == Activity.RESULT_OK) {
                    startFeebform()
                }
            REQUEST_LOOKUP->
                if (resultCode == Activity.RESULT_OK) {
                   // val dfs = data?.extras!!.getSerializable(ESP_LIB_DynamicFormSectionFieldDAO.BUNDLE_KEY) as ESP_LIB_DynamicFormSectionFieldDAO?
                    val lookup = data?.extras!!.getSerializable(ESP_LIB_LookUpDAO.BUNDLE_KEY) as ESP_LIB_LookUpDAO?
                    //val isCalculatedMappedField = data.extras!!.getBoolean("isCalculatedMappedField")
                    if (fieldToBeUpdatedESPLIB != null && lookup != null) {
                        SetUpLookUpValues(fieldToBeUpdatedESPLIB!!, lookup)
                    }
                }
        }


    }

    private fun startFeebform()
    {
        val intent = Intent(this, ESP_LIB_FeedbackForm::class.java)
        intent.putExtra("actualResponseJson", actualResponseJson)
        intent.putExtra("criteriaListDAO", criteriaListDAOESPLIB)
        intent.putExtra("isAccepted", isAccepted)
        startActivity(intent)
    }


    private fun criteriaCount(ESPLIBDynamicStagesDAO1: ESP_LIB_DynamicStagesDAO, countt: Int, size: Int): Int {
        var count = countt
        for (i in 0 until size) {
            if (ESPLIBDynamicStagesDAO1.criteriaList != null) {
                val dynamicStagesCriteriaListDAO = ESPLIBDynamicStagesDAO1.criteriaList!![i]

                if (dynamicStagesCriteriaListDAO.assessmentStatus != null && (dynamicStagesCriteriaListDAO.assessmentStatus!!.equals(getString(R.string.esp_lib_text_accepted), ignoreCase = true) || dynamicStagesCriteriaListDAO.assessmentStatus!!.equals(getString(R.string.esp_lib_text_rejected), ignoreCase = true))) {
                    count++
                }
            }
        }

        return count
    }

    fun getFormValues(criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO): ArrayList<ESP_LIB_DynamicFormValuesDAO> {
        var sectionId = 0
        val formValuesList = ArrayList<ESP_LIB_DynamicFormValuesDAO>()
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
                            if (!finalValue.isNullOrEmpty()) {
                                finalValue += ":" + dynamicFormSectionFieldDAO.selectedCurrencyId + ":" + dynamicFormSectionFieldDAO.selectedCurrencySymbol
                            }
                            value.value = finalValue

                        }

                        if (dynamicFormSectionFieldDAO.type == 11 && dynamicFormSectionFieldDAO.value != null && !dynamicFormSectionFieldDAO.value!!.contains(":"))
                            dynamicFormSectionFieldDAO.value = ""

                        formValuesList.add(value)
                    }
                }
            }
        }
        return formValuesList
    }

    fun SubmitStageRequest(isAccepted: Boolean, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO, position: Int) {

        //Shared.getInstance().CloneAddFormWithForm(actual_response);
        val formValuesList = getFormValues(criteriaListDAOESPLIB)
        // CustomLogs.displayLogs(ACTIVITY_NAME + " post.ApplicationSingleton(): " + ApplicationSingleton.getInstace().getApplication().getApplicationId());
        val post = ESP_LIB_PostApplicationsStatusDAO()

        criteriaListDAOESPLIB.formValues = formValuesList
        post.isAccepted = isAccepted
        post.applicationId = actualResponseJsonsubmitJsonESPLIB?.applicationId!!
        post.assessmentId = criteriaListDAOESPLIB.assessmentId
        post.comments = ""
        post.stageId = criteriaListDAOESPLIB.stageId
        post.values = formValuesList


        ESP_LIB_CustomLogs.displayLogs(TAG + " post.getApplicationStatus(): " + post.toJson() + " toString: " + post.toString())
        stagefeedbackSubmitForm(post, criteriaListDAOESPLIB, position)


    }//END SubmitRequest

    fun stagefeedbackSubmitForm(ESPLIBPost: ESP_LIB_PostApplicationsStatusDAO, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO, position: Int) {


        start_loading_animation()
        try {

            val apis = CompRoot()?.getService(this)
            val status_call = apis?.AcceptRejectApplication(ESPLIBPost)


            status_call?.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {


                    ESP_LIB_CustomLogs.displayLogs("$TAG stagefeedbackSubmitForm: $response")
                    //  onBackPressed()
                    GetApplicationDetail(ESPLIBPost.applicationId.toString(), criteriaListDAOESPLIB, position)


                }

                override fun onFailure(call: Call<Int>, t: Throwable?) {
                    stop_loading_animation()
                    if (t != null && bContext != null) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                    }
                }
            })

        } catch (e: Exception) {
            stop_loading_animation()

            ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)


        }

    }

    fun GetApplicationDetail(id: String, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO, position: Int) {

        start_loading_animation()
        try {
            val apis = CompRoot()?.getService(bContext)
            val detail_call = apis?.GetApplicationDetailv2(id,false,false)
            detail_call?.enqueue(object : Callback<ESP_LIB_DynamicResponseDAO> {
                override fun onResponse(call: Call<ESP_LIB_DynamicResponseDAO>, responseESPLIB: Response<ESP_LIB_DynamicResponseDAO>?) {

                    if (responseESPLIB != null && responseESPLIB.body() != null) {

                        stop_loading_animation()
                        val stages = responseESPLIB.body().stages
                        for (i in stages!!.indices) {
                            for (j in stages[i].criteriaList!!.indices) {

                                actualResponseJsonsubmitJsonESPLIB = responseESPLIB.body()

                                if (dynamicStagesDAO?.id == stages[i].id) {
                                    setStatusColor(stages[i].status)
                                }
                                val getCriteria = stages[i].criteriaList?.get(j);
                                if (getCriteria?.id == criteriaListDAOESPLIB.id) {

                                    for (k in criteriaAdapterESPLIB?.criteriaListESPLIB!!.indices) {
                                        val dynamicStagesCriteriaListDAO = criteriaAdapterESPLIB?.criteriaListESPLIB?.get(k)


                                        if (getCriteria.id == dynamicStagesCriteriaListDAO?.id) {
                                            setValues(dynamicStagesCriteriaListDAO, getCriteria)
                                        }
                                    }
                                    criteriaAdapterESPLIB?.notifyItemChanged(position)

                                }
                            }
                        }

                    } else {
                        stop_loading_animation()
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                    }
                }

                override fun onFailure(call: Call<ESP_LIB_DynamicResponseDAO>, t: Throwable) {
                    t.printStackTrace()
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                    return
                }
            })

        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
            return
        }

    }

    fun setValues(ESPLIBDynamicStagesCriteriaList: ESP_LIB_DynamicStagesCriteriaListDAO?, criteriaESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO): ESP_LIB_DynamicStagesCriteriaListDAO? {

        ESPLIBDynamicStagesCriteriaList?.assessmentStatus = criteriaESPLIB.assessmentStatus
        ESPLIBDynamicStagesCriteriaList?.formValues = criteriaESPLIB.formValues
        ESPLIBDynamicStagesCriteriaList?.form = criteriaESPLIB.form
        ESPLIBDynamicStagesCriteriaList?.isOwner = criteriaESPLIB.isOwner
        ESPLIBDynamicStagesCriteriaList?.isValidate = criteriaESPLIB.isValidate
        ESPLIBDynamicStagesCriteriaList?.approveText = criteriaESPLIB.approveText
        ESPLIBDynamicStagesCriteriaList?.assessmentId = criteriaESPLIB.assessmentId
        ESPLIBDynamicStagesCriteriaList?.rejectText = criteriaESPLIB.rejectText
        return ESPLIBDynamicStagesCriteriaList
    }

    override fun onAttachmentFieldClicked(fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO?, position: Int) {
        fieldToBeUpdatedESPLIB = fieldDAOESPLIB
        fieldToBeUpdatedESPLIB?.updatePositionAttachment = position

        var getAllowedValuesCriteria = fieldToBeUpdatedESPLIB?.allowedValuesCriteria
        getAllowedValuesCriteria = getAllowedValuesCriteria!!.replace("\\.".toRegex(), "")


        val values = getAllowedValuesCriteria.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

        val valuesList = ArrayList(Arrays.asList(*values))
        val refineValuesList = ArrayList<String>()
        for (j in valuesList.indices) {
            if (valuesList[j] != "-2")
                refineValuesList.add(valuesList[j])
        }

        val mimeTypes = arrayOfNulls<String>(refineValuesList.size)
        for (i in refineValuesList.indices) {
            val type = refineValuesList[i]
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type.toLowerCase())
            if (mimeType != null)
                mimeTypes[i] = mimeType
            else
                mimeTypes[i] = type
        }

        // Intent getContentIntent = FileUtils.createGetContentIntent();

        val getContentIntent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter

        getContentIntent.type = "*/*"
        if (getAllowedValuesCriteria!!.length > 0)
            getContentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // Only return URIs that can be opened with ContentResolver
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE)

        val intent = Intent.createChooser(getContentIntent, getString(R.string.esp_lib_text_selectafile))
        startActivityForResult(intent, REQUEST_CHOOSER)
    }

    override fun onLookupFieldClicked(fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO?, position: Int, isCalculatedMappedField: Boolean) {
        if (!pDialog!!.isShowing()) {
            fieldToBeUpdatedESPLIB = fieldDAOESPLIB
            fieldToBeUpdatedESPLIB?.updatePositionAttachment = position

            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_DynamicFormSectionFieldDAO.BUNDLE_KEY, fieldToBeUpdatedESPLIB)


            val chooseLookupOption = Intent(bContext, ESP_LIB_ChooseLookUpOption::class.java)
            chooseLookupOption.putExtras(bundle)
            startActivityForResult(chooseLookupOption, REQUEST_LOOKUP)
        }

    }


    private fun start_loading_animation() {
        try {
            if (!pDialog!!.isShowing())
                pDialog?.show()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun stop_loading_animation() {

        try {
            if (pDialog!!.isShowing())
                pDialog?.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }

    fun SetUpLookUpValues(fieldESPLIB: ESP_LIB_DynamicFormSectionFieldDAO, lookup: ESP_LIB_LookUpDAO) {

        fieldESPLIB.value = lookup.id.toString()
        fieldESPLIB.lookupValue = lookup.name
        fieldESPLIB.id = lookup.id


        if (criteriaAdapterESPLIB != null)
            fieldToBeUpdatedESPLIB?.updatePositionAttachment?.let { criteriaAdapterESPLIB?.notifyOnly(it) }

        if (fieldESPLIB.isTigger) {
            callService()
        }
    }

    companion object {

        var isGoBAck: Boolean = false


    }

    override fun onResume() {
        super.onResume()

        if (isGoBAck) {
            isGoBAck = false
            onBackPressed()
        } else
            registerReciever()
    }

    override fun onDestroy() {
        super.onDestroy()
        isGoBAck = false
    }

    override fun onPause() {
        super.onPause()
        unRegisterReciever()
    }

    private fun registerReciever() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    private fun unRegisterReciever() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        EventBus.getDefault().unregister(this)
    }

    fun getValuesForCalculatedValues(): ESP_LIB_DynamicResponseDAO? {
        for (i in dynamicStagesDAO?.criteriaList!!.indices) {
            val getList = dynamicStagesDAO?.criteriaList?.get(i);
            val formValues = getFormValues(getList!!)
            dynamicStagesDAO?.criteriaList?.get(i)?.formValues = formValues
        }

        actualResponseJsonsubmitJsonTempESPLIB = actualResponseJsonsubmitJsonESPLIB

        val criteriaList = dynamicStagesDAO?.criteriaList

        for (k in actualResponseJsonsubmitJsonTempESPLIB?.stages!!.indices) {

            val dynamicStagesDAO1 = actualResponseJsonsubmitJsonTempESPLIB?.stages?.get(k)
            if (dynamicStagesDAO1?.id == dynamicStagesDAO?.id) {

                for (p in criteriaList!!.indices) {

                    for (j in criteriaList.get(p).form.sections!!.indices) {
                        criteriaList.get(p).form.sections?.get(j)?.dynamicStagesCriteriaListDAO = null
                    }
                }


                actualResponseJsonsubmitJsonTempESPLIB?.stages?.get(k)?.criteriaList = criteriaList

            }

        }

        return actualResponseJsonsubmitJsonTempESPLIB

    }


    fun getCalculatedValues() {
        if (isKeyboardVisible)
            isCalculatedField = true

        try {
            // start_loading_animation()
            val valuesForCalculatedValues = getValuesForCalculatedValues()
            val apis = CompRoot()?.getService(bContext)
            val calculatedSubmitCall = apis?.getCalculatedValues(valuesForCalculatedValues!!)
            calculatedSubmitCall?.enqueue(object : Callback<List<ESP_LIB_CalculatedMappedFieldsDAO>> {
                override fun onResponse(call: Call<List<ESP_LIB_CalculatedMappedFieldsDAO>>, response: Response<List<ESP_LIB_CalculatedMappedFieldsDAO>>?) {

                    if (response != null && response.body() != null) {

                        val calculatedMappedFieldsList = response.body()

                        for (i in calculatedMappedFieldsList.indices) {
                            val calculatedMappedFieldsDAO = calculatedMappedFieldsList[i]
                            for (y in dynamicStagesDAO?.criteriaList!!.indices) {
                                val getList = dynamicStagesDAO?.criteriaList?.get(y);

                                val form = getList?.form
                                if (form?.sections != null) {
                                    for (sections in form.sections!!) {
                                        var sectionId = sections.id

                                        if (sections.fields != null) {
                                            for (t in sections.fields!!.indices) {
                                                val dynamicFormSectionFieldDAO = sections.fields?.get(t)

                                                if (dynamicFormSectionFieldDAO?.sectionCustomFieldId == calculatedMappedFieldsDAO.sectionCustomFieldId) {
                                                    val targetFieldType = calculatedMappedFieldsDAO.targetFieldType

                                                    if (targetFieldType == 13) {


                                                        if (!calculatedMappedFieldsDAO.value.isEmpty()) {
                                                            val split: Array<String> = calculatedMappedFieldsDAO.value.split(":".toRegex()).toTypedArray()
                                                            val lookupId = split[0]
                                                            if (!lookupId.isEmpty() && ESP_LIB_Shared.getInstance().isNumeric(lookupId)) dynamicFormSectionFieldDAO.id = lookupId.toInt()
                                                            if (split.size >= 1) {
                                                                try {
                                                                    val lookupText = split[1]
                                                                    dynamicFormSectionFieldDAO.lookupValue = lookupText
                                                                } catch (e: java.lang.Exception) {
                                                                    e.printStackTrace()
                                                                }
                                                            }
                                                        }

                                                        val servicelookupItems = calculatedMappedFieldsDAO.lookupItems
                                                        //  if (dynamicFormSectionFieldDAO.lookupValue != null && !dynamicFormSectionFieldDAO.lookupValue!!.isEmpty()) {

                                                        try {
                                                            val servicelookupItemsTemp = ArrayList<String>()
                                                            if (servicelookupItems != null) {
                                                                for (s in servicelookupItems.indices) {
                                                                    servicelookupItemsTemp.add(servicelookupItems.get(s).name!!)
                                                                }
                                                                if (!servicelookupItemsTemp.contains(dynamicFormSectionFieldDAO.lookupValue!!))
                                                                    dynamicFormSectionFieldDAO.lookupValue = ""
                                                            }
                                                        } catch (e: java.lang.Exception) {
                                                        }

                                                        //    }
                                                        calculatedMappedFieldsDAO.lookupItems = servicelookupItems
                                                        ESP_LIB_Shared.getInstance().saveLookUpItems(calculatedMappedFieldsDAO.sectionCustomFieldId, servicelookupItems)
                                                    }

                                                    if (targetFieldType == 7) {

                                                        dynamicFormSectionFieldDAO.isMappedCalculatedField = true
                                                        dynamicFormSectionFieldDAO.type = calculatedMappedFieldsDAO.targetFieldType
                                                        dynamicFormSectionFieldDAO.value = calculatedMappedFieldsDAO.value
                                                        var attachmentFileSize = ""
                                                        if (calculatedMappedFieldsDAO.details != null) {
                                                            val getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(calculatedMappedFieldsDAO.details!!.name)!!.path
                                                            val isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, bContext)
                                                            if (isFileExist) {
                                                                var file: File? = null
                                                                file = File(getOutputMediaFile)
                                                                attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file)
                                                            }

                                                            calculatedMappedFieldsDAO.details!!.fileSize = attachmentFileSize
                                                        }
                                                        dynamicFormSectionFieldDAO.details = calculatedMappedFieldsDAO.details
                                                    } else if (targetFieldType == 4) {
                                                        val calculatedDisplayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, calculatedMappedFieldsDAO.value, false)
                                                        dynamicFormSectionFieldDAO.value = calculatedDisplayDate


                                                    } else if (targetFieldType == 11) {
                                                        val fieldDAO = ESP_LIB_Shared.getInstance().populateCurrency(calculatedMappedFieldsDAO.value)
                                                        val concateValue = fieldDAO.value + " " + fieldDAO.selectedCurrencySymbol
                                                        dynamicFormSectionFieldDAO.value = concateValue
                                                        if (!fieldDAO.value.isNullOrEmpty() && !fieldDAO.selectedCurrencySymbol.isNullOrEmpty())
                                                            calculatedMappedFieldsDAO.value = concateValue

                                                    } else
                                                        dynamicFormSectionFieldDAO.value = calculatedMappedFieldsDAO.value


                                                    //  if (!dynamicFormSectionFieldDAO.value.isNullOrEmpty() || dynamicFormSectionFieldDAO.hasValue) {
                                                    //       dynamicFormSectionFieldDAO.hasValue = !dynamicFormSectionFieldDAO.hasValue
                                                    //         criteriaAdapter?.notifyItemChanged(t)
                                                    //    }

                                                }


                                            }
                                        }

                                    }
                                }


                            }
                            //  }

                            for (j in dynamicStagesDAO?.criteriaList!!.indices) {
                                val dynamicStagesCriteriaListDAO = dynamicStagesDAO?.criteriaList?.get(j)
                                for (s in dynamicStagesCriteriaListDAO?.formValues!!.indices) {
                                    val dynamicFormValuesDAO = dynamicStagesCriteriaListDAO.formValues.get(s)
                                    val sectionCustomFieldId = dynamicFormValuesDAO.sectionCustomFieldId
                                    if (calculatedMappedFieldsDAO.sectionCustomFieldId == sectionCustomFieldId) {
                                        dynamicFormValuesDAO.value = calculatedMappedFieldsDAO.value
                                    }
                                }

                            }


                        }


                        criteriaAdapterESPLIB?.notifyDataSetChanged()


                        val handler = Handler()
                        handler.postDelayed({
                            registerReciever()
                            // stop_loading_animation()
                        }, 1000)

                        if (ESP_LIB_ChooseLookUpOption.isOpen)
                            EventBus.getDefault().post(EventOptions.EventTriggerController())
                    } else {
                        // stop_loading_animation()
                        ESP_LIB_CustomLogs.displayLogs(TAG + " null response")
                        registerReciever()
                    }


                }

                override fun onFailure(call: Call<List<ESP_LIB_CalculatedMappedFieldsDAO>>, t: Throwable) {
                    t.printStackTrace()
                    //  stop_loading_animation()
                    ESP_LIB_CustomLogs.displayLogs(TAG + " failure response")
                    registerReciever()
                }
            })

        } catch (ex: Exception) {
            ex.printStackTrace()
            //   stop_loading_animation()
            registerReciever()
        }

    }//LoggedInUser end


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dataRefreshEvent(eventTriggerController: EventOptions.EventTriggerController) {
        callService()

    }



 /*   @Subscribe(threadMode = ThreadMode.MAIN)
    fun veridyFaceId(eventFaceIdVerification: EventOptions.EventFaceIdVerification) {

        if (pref?.profileFaceId1.isNullOrEmpty()) {
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.app_name), getString(R.string.esp_lib_text_upload_profile_image), context)
        } else if (pref?.getfaceId2().isNullOrEmpty()) {
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.app_name), getString(R.string.esp_lib_text_face_not_verified), context)
        } else {
            val espLibFacedao = ESP_LIB_FaceDAO()
            espLibFacedao.faceId1 = pref?.profileFaceId1
            espLibFacedao.faceId2 = pref?.getfaceId2()
            veridyFaceIds(espLibFacedao)
        }
    }*/

    private fun callService() {
        unRegisterReciever()
        val handler = Handler()
        handler.postDelayed({
            getCalculatedValues()
        }, 1000)
    }

    private fun setStatusColor(status: String?) {
        val actualResponseJson = Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)

        var isSigned: Boolean = false
        for (i in 0 until dynamicStagesDAO?.criteriaList!!.size) {
            val dynamicStagesCriteriaListDAO = dynamicStagesDAO?.criteriaList?.get(i)
            val assessmentStatus = dynamicStagesCriteriaListDAO?.assessmentStatus
            isSigned = dynamicStagesCriteriaListDAO?.isSigned!!
            if (isSigned && (assessmentStatus!!.equals(context?.getString(R.string.esp_lib_text_accepted),ignoreCase = true)||
                            assessmentStatus.equals(context?.getString(R.string.esp_lib_text_rejected),ignoreCase = true))) {
                ivsign.visibility = View.VISIBLE
                break
            }
        }


        rlstatus.setBackgroundResource(R.drawable.esp_lib_drawable_status_background)
        val drawable = rlstatus.background as GradientDrawable
        txtstatus.text = status
        when (status?.toLowerCase(Locale.getDefault())) {
            ESP_LIB_Enums.invited.toString() // Invited
            -> {
                txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_invited))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_invited_background))
            }
            ESP_LIB_Enums.newstatus.toString() // New
            -> {
                txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new_background))
            }
            ESP_LIB_Enums.pending.toString() // Pending
            -> {
                txtstatus.text = context?.getString(R.string.esp_lib_text_opencaps)
                txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending_background))
            }
            ESP_LIB_Enums.locked.toString() // locked
            -> {
                lockedCase(actualResponseJson, drawable)
            }

            ESP_LIB_Enums.completed.toString() // Completed
            -> {
                completeStage(dynamicStagesDAO!!, actualResponseJson, drawable)
            }

            ESP_LIB_Enums.complete.toString() // Complete
            -> {
                completeStage(dynamicStagesDAO!!, actualResponseJson, drawable)
            }

            ESP_LIB_Enums.rejected.toString()  // Rejected
            -> {
                ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
                txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected_background))
            }

            else -> {
                txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new_background))
            }
        }
    }

    private fun lockedCase(actualResponseJsonESPLIB: ESP_LIB_DynamicResponseDAO, drawable: GradientDrawable) {
       // txtstatus.text = context?.getString(R.string.esp_lib_text_completedcaps)
        if (actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true)) {
            ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
            txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
            drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected_background))
        } else if (actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.accepted.toString(), ignoreCase = true)) {
            ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
            txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted))
            drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted_background))
        } else {
            txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_locked))
            drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_transparent_color))
        }


    }

    private fun completeStage(ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO,
                              actualResponseJsonESPLIB: ESP_LIB_DynamicResponseDAO,
                              drawable: GradientDrawable) {
        txtstatus.setText(context?.getString(R.string.esp_lib_text_completedcaps))

        var getAssessmentStatus = "";
        for (i in 0 until ESPLIBDynamicStagesDAO.criteriaList!!.size) {
            getAssessmentStatus = ESPLIBDynamicStagesDAO.criteriaList?.get(i)?.assessmentStatus.toString()
        }


        if (getAssessmentStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true) || actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true)) {

            ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
            txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
            drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected_background))
        } else if (getAssessmentStatus.equals(ESP_LIB_Enums.accepted.toString(), ignoreCase = true) || actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.accepted.toString(), ignoreCase = true)) {
            ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
            txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted))
            drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted_background))
        } else {
            ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
            txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted))
            drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted_background))
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    if (isCalculatedField) {
                        isCalculatedField = false
                        val handler = Handler()
                        handler.postDelayed({
                            if (isCalculatedField)
                                criteriaAdapterESPLIB?.notifyDataSetChanged()
                        }, 500)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun setGravity() {
        if (pref!!.language.equals("ar", ignoreCase = true)) {
            txtStagename.gravity = Gravity.RIGHT
            conditiontext.gravity = (Gravity.RIGHT)
            conditiontextvalue.gravity = (Gravity.RIGHT)
            sequencetext.gravity = (Gravity.RIGHT)
            sequencetextvalue.gravity = (Gravity.RIGHT)
            acceptencetext.gravity = (Gravity.RIGHT)
            acceptencetextvalue.gravity = (Gravity.RIGHT)
            txtcriteria.gravity = (Gravity.RIGHT)
        } else {
            txtStagename.gravity = (Gravity.LEFT)
            conditiontext.gravity = (Gravity.LEFT)
            conditiontextvalue.gravity = (Gravity.LEFT)
            sequencetext.gravity = (Gravity.LEFT)
            sequencetextvalue.gravity = (Gravity.LEFT)
            acceptencetext.gravity = (Gravity.LEFT)
            acceptencetextvalue.gravity = (Gravity.LEFT)
            txtcriteria.gravity = (Gravity.LEFT)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (ESP_LIB_ApplicationDetailScreenActivity.criteriaWasLoaded)
            ESP_LIB_ApplicationDetailScreenActivity.criteriaWasLoaded = false
    }



}
