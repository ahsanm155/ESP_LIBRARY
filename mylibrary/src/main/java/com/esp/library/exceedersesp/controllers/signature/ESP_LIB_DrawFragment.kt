package com.esp.library.exceedersesp.controllers.signature

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.simplify.ink.InkView
import kotlinx.android.synthetic.main.esp_lib_activity_draw_fragment.*
import kotlinx.android.synthetic.main.esp_lib_activity_draw_fragment.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import utilities.common.ESP_LIB_CommonMethodsKotlin
import java.io.*


class ESP_LIB_DrawFragment : Fragment() {

    val TAG = javaClass.simpleName
    var isPreviewEnable:Boolean=false
    internal var pDialog: android.app.AlertDialog? = null

    companion object {
        fun newInstance(): ESP_LIB_DrawFragment {
            val fragment = ESP_LIB_DrawFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.esp_lib_activity_draw_fragment, container, false)
        val ink = view.findViewById(R.id.ink) as InkView
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(requireContext())


        view.ivpreview.setOnClickListener {
            if (!ink.isViewEmpty) {
                val drawing = ink.bitmap
                view.ivsignature.setImageBitmap(drawing)
                view.ivpreview.setImageResource(R.drawable.esp_lib_drawable_ic_preview_selected)
                isPreviewEnable=true
                view.llsignature.visibility = View.VISIBLE
                view.txtpreview.visibility = View.VISIBLE
                view.btsave.isEnabled = true
                view.btsave.alpha = 1f
                view.btsave.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green)
            } else {

            }
        }

        view.ivclear.setOnClickListener {
            if (!ink.isViewEmpty)
                ink.clear()
        }

        ink.addListener(object : InkView.InkListener {
            override fun onInkClear() {
                view.llsignature.visibility = View.GONE
                view.txtpreview.visibility = View.GONE
                isPreviewEnable=true
                view.ivclear.setImageResource(R.drawable.esp_lib_drawable_ic_clear_grey)
                view.ivpreview.setImageResource(R.drawable.esp_lib_drawable_ic_preview_grey)
                view.btsave.isEnabled = false
                view.btsave.alpha = 0.5f
                view.btsave.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button)
            }

            override fun onInkDraw() {
                isPreviewEnable=false
                view.ivclear.setImageResource(R.drawable.esp_lib_drawable_ic_clear_green)
                view.ivpreview.setImageResource(R.drawable.esp_lib_drawable_ic_preview_green)
            }

        })

        view.btsave.setOnClickListener{
            if(!isPreviewEnable)
                showAlertMessage(getString(R.string.esp_lib_text_warning),getString(R.string.esp_lib_text_preview_description),requireActivity())
            else
                uploadSignature()
        }

        return view
    }

    private fun uploadSignature()
    {

        when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            true -> {

                val fontFamily = ""
                val drawing = ink.getBitmap(resources.getColor(R.color.esp_lib_color_palegreythree))
                val buildImageBodyPart = buildImageBodyPart("image3.png", drawing)
                view?.ivsignature?.setImageBitmap(drawing)


              //  val file = ESP_LIB_Shared.getInstance().prepareFilePart(Uri.parse(bitmapToFile.absolutePath), requireContext())
                ESP_LIB_CommonMethodsKotlin.upLoadSignature(requireContext(),buildImageBodyPart, getString(R.string.esp_lib_text_draw),"",fontFamily, pDialog)
            }
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }
    }

    private fun buildImageBodyPart(fileName: String, bitmap: Bitmap):  MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap)
        val reqFile = RequestBody.create(MediaType.parse("image/*"),    leftImageFile)
        return MultipartBody.Part.createFormData(fileName,     leftImageFile.name, reqFile)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(context?.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }



    private fun showAlertMessage(title: String?, message: String?, activity: Context) {
        MaterialAlertDialogBuilder(activity, R.style.Esp_Lib_Style_AlertDialogTheme)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(activity.applicationContext.getString(R.string.esp_lib_text_yes)) {
                    dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                    uploadSignature()

                }
                .setNegativeButton(activity.applicationContext.getString(R.string.esp_lib_text_no)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }

                .show()
    }
}


