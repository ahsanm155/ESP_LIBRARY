package utilities.common

import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Constants
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.customevents.EventOptions
import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO
import com.microsoft.projectoxford.face.FaceServiceRestClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Response
import java.io.*
import java.util.*

class ESP_LIB_CommonMethodsKotlin {


    companion object {

        private val PERMISSION_REQUEST_CODE = 200
        private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

        @JvmStatic
        fun applyCustomEllipsizeSpanning(maxLines: Int, textToSpan: TextView, context: Context) {
            textToSpan.post {

                textToSpan.maxLines = maxLines

                if (textToSpan.lineCount > maxLines) {
                    val lineStartIndex = textToSpan.layout.getLineStart(0)
                    var lineEndIndex = textToSpan.layout.getLineEnd(maxLines - 1)


                    val actualText = textToSpan.getText().toString()
                    var subStringText: String

                    subStringText = actualText.substring(lineStartIndex, lineEndIndex)

                    lineEndIndex = subStringText.length - 8
                    subStringText = subStringText.substring(lineStartIndex, lineEndIndex)
                    var lastIndex = lineEndIndex

                    for (i in subStringText.length - 1 downTo 0) {

                        val lastIndexChar = subStringText[i]

                        if (!lastIndexChar.toString().matches("[a-zA-Z.?]*".toRegex())) {
                            lastIndex = i
                            break
                        }
                    }

                    subStringText = subStringText.substring(0, lastIndex)
                    subStringText += " [ ... ]"

                    val spannableStringBuilder = SpannableStringBuilder(subStringText);
                    spannableStringBuilder.setSpan(
                            ForegroundColorSpan(
                                    ContextCompat.getColor(context, R.color.colorPrimary)
                            ), spannableStringBuilder.length - 7, spannableStringBuilder.length, 0
                    )
                    spannableStringBuilder.setSpan(
                            StyleSpan(Typeface.BOLD),
                            spannableStringBuilder.length - 7,
                            spannableStringBuilder.length,
                            0
                    )

                    textToSpan.text = spannableStringBuilder


                }

            }
        }


        fun checkPermission(context: Context): Boolean {

            val permissionInternal = context.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) }
            val permissionExternal = context.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) }
            val permissionCamera = context.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }


            val listPermissionsNeeded = ArrayList<String>()

            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }

            if (permissionInternal != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (permissionExternal != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (!listPermissionsNeeded.isEmpty()) {
                context.let { ActivityCompat.requestPermissions(it as Activity, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS) }
                return false
            }
            return true
        }

        fun requestPermission(context: Context) {
            context.let { ActivityCompat.requestPermissions(it as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE) }
        }

        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, context: Context) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.size > 0) {
                    val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (locationAccepted) {
                        //"Permission Granted, Now you can access location data."

                        try {
                            ESP_LIB_Shared.getInstance().createFolder(ESP_LIB_Constants.FOLDER_PATH, ESP_LIB_Constants.FOLDER_NAME, context)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        fun decodeFile(f: File): String? {
            var b: Bitmap? = null
            try {
                // Decode image size
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true

                var fis = FileInputStream(f)
                try {
                    BitmapFactory.decodeStream(fis, null, o)
                } finally {
                    fis.close()
                }

                var scale = 1
                val size = Math.max(o.outHeight, o.outWidth)
                while (size shr scale - 1 > 800) {
                    ++scale
                }

                // Decode with inSampleSize
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                fis = FileInputStream(f)
                try {
                    b = BitmapFactory.decodeStream(fis, null, o2)
                } finally {
                    fis.close()
                }
            } catch (e: IOException) {
            }

            return persistImage(b, f.path)
        }

        fun persistImage(bitmap: Bitmap?, fpath: String): String? {


            val fnewpath = File(fpath)
            val os: OutputStream
            try {
                os = FileOutputStream(fnewpath)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, os)
                os.flush()
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            return fnewpath.path
        }

        fun createBitmap(path: String?): Bitmap {
            return BitmapFactory.decodeFile(path)
        }

        fun getImageUri(inContext: Context?, inImage: Bitmap): Uri? {
            try {
                val bytes = ByteArrayOutputStream()
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(inContext!!.contentResolver, inImage, "Title", null)
                return Uri.parse(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        fun getFaceClient(): FaceServiceRestClient {
            // Add your Face endpoint to your environment variables.
            val apiEndpoint = "https://northeurope.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&recognitionModel=recognition_01&returnRecognitionModel=false"

            // Add your Face subscription key to your environment variables.
            val subscriptionKey = "a210b127e9fc4b42b0bac746c6e1faa6"
            return FaceServiceRestClient(apiEndpoint, subscriptionKey)
        }

        fun getIntent(contxt: Context,isCamera: Boolean,isgallery:Boolean): Intent {
            val intent = Intent(contxt, ImageSelectActivity::class.java)
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true) //default is true
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, isCamera) //default is true
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, isgallery) //default is true
            return intent
        }


        fun upLoadSignature(context: Context, file: MultipartBody.Part?, type: String, signature:String, fontFamily: String, pDialog: AlertDialog?) {


            val fontFamilyText = RequestBody.create(MediaType.parse("text/plain"), fontFamily)
            val getType = RequestBody.create(MediaType.parse("text/plain"), type)
            val signaturetext = RequestBody.create(MediaType.parse("text/plain"), signature)
            val fileGuidText = RequestBody.create(MediaType.parse("text/plain"), "")

            start_loading_animation(pDialog)
            try {

                val call_upload = ESP_LIB_Shared.getInstance().retroFitObject(context).sendSignature(file, getType, fontFamilyText,signaturetext, fileGuidText)
                call_upload.enqueue(object : retrofit2.Callback<Any> {


                    override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                        if (response?.code() == 200)
                            ESP_LIB_Shared.getInstance().messageBox(context.getString(R.string.esp_lib_text_signature_success), context as Activity?)
                        else
                            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_error), context.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

                        stop_loading_animation(pDialog)

                    }


                    override fun onFailure(call: Call<Any>?, t: Throwable?) {
                        stop_loading_animation(pDialog)
                        ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_error), context.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

                    }


                })

            } catch (ex: Exception) {
                stop_loading_animation(pDialog)
                ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_error), context.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

            }

        }




        private fun start_loading_animation(pDialog: AlertDialog?) {

            if (pDialog != null && !pDialog.isShowing)
                pDialog.show()


        }

        private fun stop_loading_animation(pDialog: AlertDialog?) {

            if (pDialog != null && pDialog.isShowing)
                pDialog.dismiss()


        }


        fun getLocalBitmapUri(bmp: Bitmap,context:Context): Uri? {
            var bmpUri: Uri? = null
            try {
                val file = File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "share_image_" + System.currentTimeMillis() + ".png"
                )
                val out = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
                out.close()
                bmpUri = Uri.fromFile(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bmpUri
        }

    }


}