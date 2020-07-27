package com.esp.library.exceedersesp.controllers.Profile


import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.ESP_LIB_WebViewScreenActivity
import com.esp.library.utilities.common.*
import com.esp.library.utilities.customevents.EventOptions
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.esp_lib_fragment_profile_image.view.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.adapters.setup.applications.ESP_LIB_SectionListAdapter
import utilities.common.ESP_LIB_CommonMethodsKotlin
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldsCardsDAO
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO
import utilities.interfaces.ESP_LIB_Itemclick
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ESP_LIB_FragmentProfileImage : androidx.fragment.app.Fragment(), ESP_LIB_Itemclick {

    internal var TAG = javaClass.simpleName
    internal var bundle: Bundle? = null
    internal var profile_imageESPLIB: ESP_LIB_RoundedImageView? = null
    private var mApplicationLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var adapterESPLIB: ESP_LIB_SectionListAdapter? = null
    internal var context: Context? = null
    internal var dataapplicant: ESP_LIB_ApplicationProfileDAO? = null
    internal var ischeckerror = false
    internal var file: Uri? = null
    internal var pictureFilePath: String? = null
    internal var pDialog: AlertDialog? = null
    internal var idenedi_login_btn: Button? = null
    internal var etxtidenediID: TextView? = null
    internal var rlidenedikey: RelativeLayout? = null
    val PIC_CROP = 3
    internal var pref: ESP_LIB_SharedPreference? = null



    val outputMediaFile: File?
        get() {
            val mediaStorageDir = File(Environment.getExternalStorageDirectory().path + "/ESP/")

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())


            return File(mediaStorageDir.path + File.separator +
                    timeStamp + ".jpg")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.esp_lib_fragment_profile_image, container, false)
        initailize(view)
        if (Build.VERSION.SDK_INT >= 24) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }// for camera above API 24
        if (!ESP_LIB_CommonMethodsKotlin.checkPermission(requireContext())) {
            ESP_LIB_CommonMethodsKotlin.requestPermission(requireContext())
        }

        bundle = arguments
        ischeckerror = bundle!!.getBoolean("ischeckerror")

        if (ischeckerror)
            view.ivback.visibility = View.VISIBLE
        else
            view.ivback.visibility = View.GONE

        updateTop(view)
        populateData(view)
        //UpLoadImageDetect()

        view.profile_image.setOnClickListener {
            if (!ESP_LIB_CommonMethodsKotlin.checkPermission(requireContext())) {
                ESP_LIB_CommonMethodsKotlin.requestPermission(requireContext())
            } else
                showPictureDialog()
        }
        view.ivback.setOnClickListener {
            /*val intent = Intent(context, ESP_LIB_ApplicationsActivityDrawer::class.java)
            val cn = intent.getComponent()
            val mainIntent = Intent.makeRestartActivityTask(cn)
            startActivity(mainIntent)*/
            /*   EventBus.getDefault().post(EventRefreshData())
               ESP_LIB_ESPApplication.getInstance().isOnCLosedTab = true*/
            activity?.finish()
        }



        view.idenedi_login_btn.setOnClickListener {
            val bn = Bundle()
            bn.putString("heading", getString(R.string.esp_lib_text_logineithidenedi))

            //    bn.putString("url", "https://app.idenedi.com/app_permission/?response_type=code&client_id=" + pref?.getidenediClientId() + "&redirect_uri=https://isp.exceedgulf.com/login")
         //   bn.putString("url", "https://app.idenedi.com/app_permission/?response_type=code&client_id=" + pref?.getidenediClientId() + "&redirect_uri=" + ESP_LIB_Constants.base_url.replace("webapi/", "") + "login")
            bn.putString("url", "https://idenedi-prod-stag.azurewebsites.net/app_permission/?response_type=code&client_id=" + pref?.getidenediClientId() + "&redirect_uri=https://qaesp.azurewebsites.net/login")
            bn.putBoolean("isIdenedi", true)
            ESP_LIB_Shared.getInstance().callIntent(ESP_LIB_WebViewScreenActivity::class.java, context as Activity?, bn)
        }

        if (pref?.getidenediClientId().isNullOrEmpty()) {
            view.idenedi_login_btn.visibility = View.GONE
        } else if (dataapplicant?.applicant?.idenediKey.isNullOrEmpty())
            view.idenedi_login_btn.visibility = View.VISIBLE
        else
            setIdenediKey(dataapplicant?.applicant?.idenediKey!!)




        return view

    }

    private fun initailize(view: View) {
        context = activity
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        idenedi_login_btn = view.findViewById(R.id.idenedi_login_btn)
        etxtidenediID = view.findViewById(R.id.etxtidenediID)
        rlidenedikey = view.findViewById(R.id.rlidenedikey)
        pref = ESP_LIB_SharedPreference(context)
        profile_imageESPLIB = view.findViewById(R.id.profile_image)
        mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        view.app_list_sections.setHasFixedSize(true)
        view.app_list_sections.layoutManager = mApplicationLayoutManager
        view.app_list_sections.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

    }

    private fun setIdenediKey(idenediKey: String) {

        val reasonTextConcate = context?.getString(R.string.esp_lib_text_idenediid) + " " + idenediKey
        val wordtoSpan: Spannable = SpannableString(reasonTextConcate)
        wordtoSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.esp_lib_color_black)), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.esp_lib_color_coolgrey)), 12, reasonTextConcate.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        idenedi_login_btn?.visibility = View.GONE
        rlidenedikey?.visibility = View.VISIBLE
        etxtidenediID?.text = wordtoSpan
    }

    private fun updateTop(view: View) {

        try {
            dataapplicant = bundle!!.getSerializable("dataapplicant") as ESP_LIB_ApplicationProfileDAO
            val dataApplicantTemp = dataapplicant
            view.etxtusername.text = dataApplicantTemp?.applicant?.name
            view.etxtemail.text = dataApplicantTemp?.applicant?.emailAddress
            if (dataApplicantTemp?.applicant?.imageUrl?.replace("\\s".toRegex(), "")?.length!! > 0) {
                profile_imageESPLIB?.let {
                    Glide.with(requireActivity()).load(dataApplicantTemp.applicant.imageUrl)
                            .error(R.drawable.esp_lib_drawable_default_profile_picture)
                            .into(it)
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun populateData(view: View) {
        val sectionstemp = ArrayList<ESP_LIB_DynamicFormSectionDAO>()
        val applicantSections = dataapplicant?.applicant?.applicantSections
        val sections = dataapplicant!!.sections
        for (i in sections.indices) {
            val section = sections[i]

            if (section.type == 1 || section.type == 2) {

                val id = section.id
                val cardsList = ArrayList<ESP_LIB_DynamicFormSectionFieldsCardsDAO>()
                for (applicationsection in applicantSections!!) {
                    val sectionId = applicationsection.sectionId
                    if (id == sectionId) {
                        val displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, applicationsection.lastUpdatedOn, true)
                        section.lastUpdatedOn = displayDate
                        val finalFields = ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>()
                        val values = applicationsection.values
                        val fields = section.fields

                        for (o in fields!!.indices) {
                            val parentSectionField = fields[o]
                            val tempField = ESP_LIB_Shared.getInstance().setObjectValues(parentSectionField)
                            if (tempField.isVisible) {
                                finalFields.add(tempField)
                            }
                        }

                        for (j in 0 until finalFields.size) {
                            val getFinalFields = finalFields.get(j)
                            val sectionCustomFieldId = getFinalFields.sectionCustomFieldId
                            for (k in values!!.indices) {
                                val getVal = values[k]
                                var getValue: String? = getVal.value
                                val getSectionFieldId = getVal.sectionFieldId

                                if (getSectionFieldId == sectionCustomFieldId) {
                                    getFinalFields.value = getValue
                                    if (getFinalFields.type == 13)
                                    // lookupvalue
                                    {
                                        getValue = getVal.lookupValue
                                        if (getValue == null) getValue = getVal.value
                                        getFinalFields.lookupValue = getValue
                                        if (getVal.value != null && ESP_LIB_Shared.getInstance().isNumeric(getVal.value)) getFinalFields.id = getVal.value!!.toInt()
                                        getFinalFields.value = getValue
                                    } else if (getFinalFields.type == 7) { // for attachments only
                                        try {
                                            val details = ESP_LIB_DyanmicFormSectionFieldDetailsDAO()
                                            details.downloadUrl = getVal.details?.downloadUrl
                                            details.mimeType = getVal.details?.mimeType
                                            details.createdOn = getVal.details?.createdOn
                                            details.name = getVal.details?.name
                                            getFinalFields.details = details
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    } else if (getFinalFields.type == 19 || getFinalFields.type == 18) {
                                        if (getVal.type == 11) {
                                            val fieldDAO = ESP_LIB_Shared.getInstance().populateCurrency(getValue)
                                            val concateValue = fieldDAO.value + " " + fieldDAO.selectedCurrencySymbol
                                            getFinalFields.value = concateValue
                                        } else if (getVal.type == 7) { // for attachments only
                                            try {
                                                getFinalFields.type = getVal.type
                                                val details = ESP_LIB_DyanmicFormSectionFieldDetailsDAO()
                                                details.downloadUrl = getVal.details?.downloadUrl
                                                details.mimeType = getVal.details?.mimeType
                                                details.createdOn = getVal.details?.createdOn
                                                details.name = getVal.details?.name
                                                getFinalFields.details = details
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                        } else if (getVal.type == 13)
                                        // lookupvalue
                                        {

                                            getValue = getVal.lookupValue
                                            if (getValue == null)
                                                getValue = getVal.value

                                            getFinalFields.lookupValue = getValue

                                            //if (getVal.value != null && getVal.value.isNullOrEmpty())
                                            if (getVal.value != null && ESP_LIB_Shared.getInstance().isNumeric(getVal.value))
                                                getFinalFields.id = Integer.parseInt(getVal.value!!)
                                        }
                                    }


                                }

                            }


                        }

                        if (finalFields.size == 0) {

                            for (f in 0 until section.fields!!.size) {
                                if (section.fields!![f].isVisible)
                                    finalFields.add(section.fields!![f])
                            }
                        }



                        cardsList.add(ESP_LIB_DynamicFormSectionFieldsCardsDAO(finalFields))



                        if (ischeckerror) {
                            if (values != null) {
                                //  for (f in 0 until section.fields!!.size) {
                                // if ((section.fields!![f].isVisible && section.fields!![f].isRequired)
                                section.isShowError = values.size == 0
                                // }
                            }
                        }


                    }

                }
                section.setRefreshFieldsCardsList(cardsList)

                if (section.fields!!.size == 0 && section.isDefault) {
                    section.isShowError = false
                    sectionstemp.add(section)

                }

                for (h in section.getFieldsCardsList().indices) {
                    for (p in section.getFieldsCardsList()[h].fields!!.indices) {
                        if (section.getFieldsCardsList()[h].fields!![p].isVisible && section.getFieldsCardsList()[h].fields!![p].isRequired
                                && (section.getFieldsCardsList()[h].fields!![p].value == null || TextUtils.isEmpty(section.getFieldsCardsList()[h].fields!![p].value))
                                && section.type == 1 && ischeckerror) {
                            section.isShowError = true
                            break
                        } else
                            section.isShowError = false
                    }
                    sectionstemp.add(section)
                }


            }
        }


        val sectionsFinalArray = ArrayList<ESP_LIB_DynamicFormSectionDAO>()
        for (i in 0 until sectionstemp.size) {
            val isArrayHasValue = sectionsFinalArray.any { x -> x.id == sectionstemp[i].id }
            if (!isArrayHasValue)
                sectionsFinalArray.add(sectionstemp[i])
        }




        adapterESPLIB = context?.let { ESP_LIB_SectionListAdapter(sectionsFinalArray, it, this) }
        view.app_list_sections.adapter = adapterESPLIB
    }


    private fun choosePhotoFromGallary() {
        startActivityForResult(ESP_LIB_CommonMethodsKotlin.getIntent(requireActivity(),false,true), GALLERY_REQUEST)
    }

    private fun openCameraIntent() {
        startActivityForResult(ESP_LIB_CommonMethodsKotlin.getIntent(requireActivity(),true,false), CAMERA_REQUEST)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle(getString(R.string.esp_lib_text_choose))
        val pictureDialogItems = arrayOf(getString(R.string.esp_lib_text_camera), getString(R.string.esp_lib_text_gallery))
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> openCameraIntent()
                1 -> choosePhotoFromGallary()
            }
        }
        pictureDialog.show()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST, GALLERY_REQUEST ->
                if (resultCode == Activity.RESULT_OK) {
                    val filePath = data!!.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH)
                    setImageGlide(filePath)
                }
        }

    }

    private fun setImageGlide(pictureFilePath:String)
    {
        Glide.with(requireContext())
                .asBitmap()
                .load(pictureFilePath)
                .apply(RequestOptions.overrideOf(200, 200))
                .into(object : CustomTarget<Bitmap>(){
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                        val imageUri = getImageUri(context, resource)
                        UpdateLoadImageForField(imageUri, context)


                    }
                })
    }

    fun getImageUri(inContext: Context?, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context?.contentResolver, inImage, UUID.randomUUID().toString() + ".png", "drawing")
        return Uri.parse(path)
    }


    fun UpdateLoadImageForField(uri: Uri?, context: Context?) {

        try {
            var body = ESP_LIB_Shared.getInstance().prepareFilePart(uri, requireContext())
            UpLoadFile(body)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    private fun UpLoadFile(body: MultipartBody.Part?) {
        start_loading_animation()
        try {

            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

            val call_upload = apis.picture(body)
            call_upload.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>?) {
                    if (response != null && response.body() != null) {



                        ESP_LIB_CustomLogs.displayLogs(TAG + " response.body(): " + response.body())
                       /* profile_imageESPLIB?.let {
                            Glide.with(context!!).load(response.body()).placeholder(R.drawable.esp_lib_drawable_default_profile_picture)
                                    .error(R.drawable.esp_lib_drawable_default_profile_picture).into(it)
                        }*/

                        Glide.with(context!!)
                                .asBitmap()
                                .load(response.body())
                                .apply(RequestOptions.overrideOf(200, 200))
                                .into(object : CustomTarget<Bitmap>(){
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        stop_loading_animation()
                                    }

                                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {

                                            profile_imageESPLIB?.setImageBitmap(resource)
                                            ESP_LIB_Shared.getInstance().detectAndFrame(resource, ESP_LIB_CommonMethodsKotlin.getFaceClient(), requireActivity(),true)


                                    }
                                })




                    } else {
                        stop_loading_animation()
                        ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_pleasetryagain), context)
                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    // UploadFileInformation(fileDAO);
                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ex.printStackTrace()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dataRefreshEvent(eventRefreshData: EventOptions.EventFaceIdVerification) {
        stop_loading_animation()
        ESP_LIB_CustomLogs.displayLogs("getfaceId: ${pref?.getProfileFaceId1()}")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun UpLoadImageDetect() {
        start_loading_animation()
        try {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()


            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("Ocp-Apim-Subscription-Key", "a210b127e9fc4b42b0bac746c6e1faa6")
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            if (ESP_LIB_Constants.WRITE_LOG) {
                httpClient.addInterceptor(logging)
            }
            httpClient.connectTimeout(5, TimeUnit.MINUTES)
            httpClient.readTimeout(5, TimeUnit.MINUTES)
            httpClient.writeTimeout(5, TimeUnit.MINUTES)
            val gson = GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            /* retrofit builder and call web service*/
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://northeurope.api.cognitive.microsoft.com/face/v1.0/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()

            /* APIs Mapping respective Object*/
            val apis = retrofit.create(ESP_LIB_APIs::class.java)


            var jsonBody = JSONObject()
            jsonBody.put("faceId1", "ab00d070-2e7a-442f-88d5-24384a1766e8")
            jsonBody.put("faceId2", "7bc93833-d545-482f-ba45-2549ab7d1e59")

            val call_upload = apis.uploadDetect(jsonBody)
            call_upload.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>?) {
                    if (response?.body() != null) {

                        stop_loading_animation()


                    } else {
                        stop_loading_animation()
                        ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_pleasetryagain), context)
                    }

                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    // UploadFileInformation(fileDAO);
                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ex.printStackTrace()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
        }

    }

    /*  private fun checkPermission(): Boolean {

          val permissionCamera = ContextCompat.checkSelfPermission(context!!, CAMERA)
          val permissionReadStorage = ContextCompat.checkSelfPermission(context!!, READ_EXTERNAL_STORAGE)
          val permissionWriteStorage = ContextCompat.checkSelfPermission(context!!, WRITE_EXTERNAL_STORAGE)

          val listPermissionsNeeded = ArrayList<String>()

          if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
              listPermissionsNeeded.add(CAMERA)
          }
          if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
              listPermissionsNeeded.add(READ_EXTERNAL_STORAGE)
          }

          if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
              listPermissionsNeeded.add(WRITE_EXTERNAL_STORAGE)
          }

          if (!listPermissionsNeeded.isEmpty()) {
              ActivityCompat.requestPermissions(activity!!, listPermissionsNeeded.toTypedArray(), 1122)
              return false
          }
          return true
      }

      private fun requestPermission() {
          ActivityCompat.requestPermissions(activity!!, arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
      }

      override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
          if (requestCode == PERMISSION_REQUEST_CODE) {
              if (grantResults.size > 0) {
                  val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                  if (locationAccepted) {
                      //"Permission Granted, Now you can access location data."
                      //    openCameraIntent();
                      try {
                          Shared.getInstance().createFolder(Constants.FOLDER_PATH, Constants.FOLDER_NAME, context)
                      } catch (e: Exception) {
                          e.printStackTrace()
                      }

                  }
              }
          }

      }*/

    private fun sendIdendiCode() {

        start_loading_animation()

        try {

            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
            // var call_idenediToken = apis.getIdenediToken()

            var call_idenediToken = apis.linkIdenediProfile(pref?.idenediCode!!)


            call_idenediToken.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>?, response: Response<Any>?) {

                    stop_loading_animation()


                    pref?.saveIdenediCode(null)
                    if (response?.body() != null) {

                        ESP_LIB_CustomLogs.displayLogs(TAG + " response?.body(): " + response.body())
                        setIdenediKey(response.body() as String)
                    } else
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)
                }


                override fun onFailure(call: Call<Any>, t: Throwable) {
                    pref?.saveIdenediCode(null)
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)

                }
            })

        } catch (ex: Exception) {
            pref?.saveIdenediCode(null)
            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)

        }

    }

    override fun itemclick(ESPLIBDynamicFormSectionDAO: ESP_LIB_DynamicFormSectionDAO) {

        val i = Intent(context, ESP_LIB_SectionDetailScreen::class.java)
        i.putExtra("data", ESPLIBDynamicFormSectionDAO)
        i.putExtra("dataapplicant", dataapplicant)
        i.putExtra("ischeckerror", ischeckerror)
        i.putExtra("isprofile", bundle!!.getBoolean("isprofile"))
        startActivity(i)
    }

    companion object {
        private val CAMERA_REQUEST = 1
        private val GALLERY_REQUEST = 2
        private val PERMISSION_REQUEST_CODE = 200
    }

    private fun start_loading_animation() {
        if (!pDialog!!.isShowing())
            pDialog!!.show()
    }

    private fun stop_loading_animation() {
        if (pDialog!!.isShowing())
            pDialog!!.dismiss()
    }

    override fun onResume() {
        super.onResume()

        if (!pref?.idenediCode.isNullOrEmpty())
            sendIdendiCode()

    }

}
