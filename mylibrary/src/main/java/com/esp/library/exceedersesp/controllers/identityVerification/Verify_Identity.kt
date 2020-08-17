package com.esp.library.exceedersesp.controllers.identityVerification

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.utilities.common.ESP_LIB_Constants
import com.esp.library.utilities.common.ESP_LIB_Shared
import kotlinx.android.synthetic.main.esp_lib_activity_verify_identity.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Verify_Identity : ESP_LIB_BaseActivity() {

    private val PERMISSION_REQUEST_CODE = 200
    private val TAG = javaClass.simpleName

    internal var pDialog: AlertDialog? = null
    private var mCamera: Camera? = null
    private var mPicture: Camera.PictureCallback? = null
    private var cameraManager: CameraManager? = null
    private var mPreview: CameraPreview? = null
    private var myContext: Context? = null
    private var getBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(true)
        setContentView(R.layout.esp_lib_activity_verify_identity)
        initialize()



        val sourceString =  getString(R.string.esp_lib_text_verfication_mismatch_text) + "<b> " + getString(R.string.esp_lib_text_try_face_again) +"</b>"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txterrordiscription.text= Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY);
        } else {
            txterrordiscription.text= Html.fromHtml(sourceString);
        }


        btcancel.setOnClickListener { onBackPressed() }

        btretakenotverified.setOnClickListener {
            if(btretakenotverified.text.equals(getString(R.string.esp_lib_text_continuee)))
            {
                val intent = Intent()
                intent.putExtra("success", true)
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
            else {
                llnotverified.visibility = View.GONE
                rlfour.visibility = View.GONE
                llnotverifiedbuttons.visibility = View.GONE
                rlverified.visibility = View.GONE
                btaction.visibility = View.VISIBLE
                cameracontainer.visibility = View.VISIBLE
                ivcapture.visibility = View.GONE
            }
        }

        btstartverification.setOnClickListener {

            if (getBitmap != null) {
                val imageUri = getImageUri(getBitmap!!)
                UpdateLoadImageForField(imageUri)
            }
        }

        btretakePhoto.setOnClickListener {
            btaction.visibility = View.VISIBLE
            cameracontainer.visibility = View.VISIBLE
            ivcapture.visibility = View.GONE
            llretakebuttons.visibility = View.GONE
        }


        btaction.setOnClickListener {
            if (btaction.text.equals(getString(R.string.esp_lib_text_allow))) {
                if (!checkPermission()) {
                    requestPermission()
                } else {
                    btaction.text = getString(R.string.esp_lib_text_continuee)
                    rltwo.visibility = View.VISIBLE
                }
            } else if (btaction.text.equals(getString(R.string.esp_lib_text_continuee))) {
                btaction.text = getString(R.string.esp_lib_text_take_photo)
                rlthree.visibility = View.VISIBLE
                cameracontainer.visibility = View.VISIBLE
                launchCamera()

            } else if (btaction.text.equals(getString(R.string.esp_lib_text_take_photo))) {
                mCamera?.takePicture(null, null, mPicture);
            }
        }

    }


    private fun initialize() {
        myContext = this;
        setSupportActionBar(gradienttoolbar)
        ibToolbarBack.setOnClickListener { onBackPressed() }
        toolbarheading?.text = getString(R.string.esp_lib_text_verify_your_identity_title)
        cameraManager = CameraManager(this);
        mPreview = CameraPreview(applicationContext, mCamera)
        mPreview?.keepScreenOn = true
        cameracontainer?.addView(mPreview)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(this)
    }

    private fun launchCamera() {
        val camerasNumber = Camera.getNumberOfCameras()
        if (camerasNumber > 1) {
            //release the old camera instance
            //switch camera, from the front and the back and vice versa
            releaseCamera()
            chooseCamera()
        } else {
            val toast = Toast.makeText(applicationContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG)
            toast.show()
        }

        if (!cameraManager!!.hasCamera()) {
            val toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG)
            toast.show()
            finish()
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (CameraManager.findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show()
            }
            try {
                mCamera = Camera.open(CameraManager.findFrontFacingCamera())
                mPicture = getPictureCallback()
                mPreview!!.refreshCamera(mCamera)
            } catch (e: java.lang.Exception) {
                Log.e("camera", e.message)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (btaction.text.equals(getString(R.string.esp_lib_text_take_photo)))
            launchCamera()
    }


    override fun onPause() {
        super.onPause()
        //when on Pause, release camera in order to be used from other applications
        releaseCamera() // release the camera immediately on pause event
    }

    override fun onBackPressed() {
        super.onBackPressed()
        releaseCamera()

    }

    private fun releaseCamera() {
        if (mCamera != null) {
            mCamera!!.release() // release the camera for other applications
            mCamera = null
        }
    }

    fun chooseCamera() {
        val cameraId = CameraManager.findFrontFacingCamera()
        if (cameraId >= 0) {
            //open the backFacingCamera
            //set a picture callback
            //refresh the preview
            try {
                mCamera = Camera.open(cameraId)
                mPicture = getPictureCallback()
                mPreview!!.refreshCamera(mCamera)
            } catch (e: java.lang.Exception) {
                Log.e("Camera", e.message)
            }
        }
    }


    private fun getPictureCallback(): PictureCallback {
        return PictureCallback { data, camera ->
            val pictureFile: File = generateMediaFile()
            try {
                val imageProcessing = ImageProcessing(myContext)
                imageProcessing.createImage(pictureFile, data)

                val handler = Handler()
                handler.postDelayed({
                    Glide.with(this)
                            .asBitmap()
                            .load(pictureFile.absolutePath)
                            .apply(RequestOptions.overrideOf(200, 200))
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                }

                                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                                    ivcapture.setImageBitmap(resource)
                                    getBitmap = resource


                                }
                            })

                }, 500)



                ivcapture.visibility = View.VISIBLE
                llretakebuttons.visibility = View.VISIBLE
                cameracontainer.visibility = View.GONE
                btaction.visibility = View.GONE

            } catch (e: IOException) {
                e.printStackTrace()
            }

            //refresh camera to continue preview
            mPreview!!.refreshCamera(mCamera)
        }
    }

    fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, UUID.randomUUID().toString() + ".png", "drawing")
        return Uri.parse(path)
    }


    fun UpdateLoadImageForField(uri: Uri?) {

        try {
            val body = ESP_LIB_Shared.getInstance().prepareFilePart(uri, myContext)
            veridyFaceIds(body)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun veridyFaceIds(body: MultipartBody.Part?) {
        rlfour.visibility = View.VISIBLE
        llretakebuttons.visibility = View.GONE
        txtverification.text = getString(R.string.esp_lib_text_verfication_process)
        try {

            val apis = ESP_LIB_Shared.getInstance().retroFitObject(myContext)

            val call_upload = apis.verifyface(body)
            call_upload.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>?) {

                    if (response?.code() == 200)
                        verified()
                    else
                        notVerified()

                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), myContext)
                    // UploadFileInformation(fileDAO);
                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ex.printStackTrace()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), myContext)
        }

    }

    private fun verified() {
        txtverification.text = getString(R.string.esp_lib_text_profile_verification)
        rlverified.visibility = View.VISIBLE
        llnotverifiedbuttons.visibility = View.VISIBLE
        btretakenotverified.text=getString(R.string.esp_lib_text_continuee)
        ivverify.setImageResource(R.drawable.esp_lib_drawable_verified)
        ivverify.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        txtverified.text = getString(R.string.esp_lib_text_verfication_verified)
        txtverified.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
    }

    private fun notVerified() {
        btretakenotverified.text=getString(R.string.esp_lib_text_retake_photo)
        txtverification.text = getString(R.string.esp_lib_text_profile_verification)
        rlverified.visibility = View.VISIBLE
        llnotverified.visibility = View.VISIBLE
        llnotverifiedbuttons.visibility = View.VISIBLE
        ivverify.setImageResource(R.drawable.esp_lib_drawable_not_verified)
        ivverify.setColorFilter(ContextCompat.getColor(this, R.color.esp_lib_color_lipstick));
        txtverified.text = getString(R.string.esp_lib_text_not_verified)
        txtverified.setTextColor(ContextCompat.getColor(this, R.color.esp_lib_color_lipstick))
    }

    private fun start_loading_animation() {
        if (!pDialog!!.isShowing())
            pDialog!!.show()
    }

    private fun stop_loading_animation() {
        if (pDialog!!.isShowing())
            pDialog!!.dismiss()
    }

    fun generateMediaFile(): File {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Selfie Flashlight")
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File(mediaStorageDir.getPath() + File.separator.toString() +
                "IMG_" + timeStamp + ".jpg")
    }

    private fun checkPermission(): Boolean {

        val permissionCamera = ContextCompat.checkSelfPermission(this, CAMERA)
        val permissionExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionInternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)


        val listPermissionsNeeded = ArrayList<String>()

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(CAMERA)
        }
        if (permissionInternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0) {
                var permissionGranted=true

                for(element in grantResults)
                {
                    if(element ==PackageManager.PERMISSION_DENIED)
                    {
                        permissionGranted=false
                        break
                    }
                }


                //val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (permissionGranted) {
                    btaction.text = getString(R.string.esp_lib_text_continuee)
                    rltwo.visibility = View.VISIBLE
                    try {
                        ESP_LIB_Shared.getInstance().createFolder(ESP_LIB_Constants.FOLDER_PATH, ESP_LIB_Constants.FOLDER_NAME, this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }

    }


}