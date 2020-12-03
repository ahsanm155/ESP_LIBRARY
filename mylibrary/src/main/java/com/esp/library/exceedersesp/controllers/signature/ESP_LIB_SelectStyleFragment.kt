package com.esp.library.exceedersesp.controllers.signature

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_GoogleFontsLibrary
import com.esp.library.utilities.data.applicants.ESP_LIB_StylesDAO
import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO
import com.esp.library.utilities.setup.ESP_LIB_CustomStyleSpinnerAdapter
import kotlinx.android.synthetic.main.esp_lib_activity_select_style_fragment.view.*
import kotlinx.android.synthetic.main.esp_lib_criteriasignature.view.*
import utilities.common.ESP_LIB_CommonMethodsKotlin


class ESP_LIB_SelectStyleFragment : Fragment() {

    internal var pDialog: android.app.AlertDialog? = null

    var rawStyles = intArrayOf(R.raw.caveat_regular, R.raw.dancingscript, R.raw.gloriagallelujah_regular, R.raw.greatvibes_regular, R.raw.indieflower_regular,
            R.raw.kaushanscript_regular, R.raw.pacifico_regular, R.raw.pangolin_regular, R.raw.parisienne_regular, R.raw.rocksalt_regular,
            R.raw.sacramento_regular)

    var rawStylesNames = arrayOf("caveat_regular", "dancingscript", "gloriagallelujah_regular", "greatvibes_regular", "indieflower_regular",
            "kaushanscript_regular", "pacifico_regular", "pangolin_regular", "parisienne_regular", "rocksalt_regular",
            "sacramento_regular")


    companion object {
        private var espLibSignatureDAO: ESP_LIB_SignatureDAO? = null

        fun newInstance(espLibSignaturedao: ESP_LIB_SignatureDAO?): ESP_LIB_SelectStyleFragment {
            val fragment = ESP_LIB_SelectStyleFragment()
            espLibSignatureDAO = espLibSignaturedao

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.esp_lib_activity_select_style_fragment, container, false)
        initalize(view)
          //populateData(view)
        view.etxtfontValue.setOnClickListener {
            view.msStyles.performClick()
        }


        stylesDropDown(view)



        view.btsave.setOnClickListener {
            when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                true -> {
                    /*if(view.etxtfontValue.tag==null)
                        view.etxtfontValue.tag="greatvibes_regular"*/
                    val fontFamily = view.etxtfontValue.tag.toString()
                    val text = view.etxtname.text.toString()
                    ESP_LIB_CommonMethodsKotlin.upLoadSignature(requireContext(), null, getString(R.string.esp_lib_text_font), text, fontFamily, pDialog)
                }
                false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
            }

        }


        view.etxtname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                view.etxtsign.setText(s)
                checkValidation(s, view)
            }

        })

        return view
    }

    private fun initalize(view: View) {
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(requireContext())
        view.etxtfontValue.setText(getString(R.string.esp_lib_text_defaultt))
        setTypeFace(getString(R.string.esp_lib_text_defaultt), view)
        view.etxtsignLabel.hint = getString(R.string.esp_lib_text_mysignature)
        val name = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.name
        view.etxtname.setText(name)
        view.etxtsign.setText(name)
        checkValidation(name, view)
    }

    private fun stylesDropDown(v: View) {

        val stylesList = ArrayList<ESP_LIB_StylesDAO>()
        for (i in 0 until rawStyles.size) {
            stylesList.add(ESP_LIB_StylesDAO(getString(R.string.esp_lib_text_signature), rawStyles[i]))
        }
        stylesList.add(0, ESP_LIB_StylesDAO(getString(R.string.esp_lib_text_defaultt), 1122))
        val adapter = ESP_LIB_CustomStyleSpinnerAdapter(requireContext(), R.layout.esp_lib_row_custom_styles_spinner, stylesList)
        v.msStyles.adapter = adapter

        v.msStyles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, spinnerPos: Int, p3: Long) {
                var position=spinnerPos
                val stylesDAO = stylesList[position]
                if (stylesDAO.fontStyle == 1122)
                {
                    v.etxtfontValue.tag="greatvibes_regular"
                    return
                }
                else if (stylesList.get(0).fontStyle == 1122)
                {
                    position=position-1
                    stylesList.removeAt(0)
                }

                v.etxtsign?.typeface = ESP_LIB_GoogleFontsLibrary.setGoogleFont(context, stylesDAO.fontStyle)
                v.etxtfontValue?.setText(stylesDAO.signatureTitle)
                v.etxtfontValue?.tag = rawStylesNames[position]
                v.etxtfontValue?.typeface = ESP_LIB_GoogleFontsLibrary.setGoogleFont(context, stylesDAO.fontStyle)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }


    }


    private fun populateData(view: View) {

        val args = Bundle()
        if (espLibSignatureDAO == null) {
            view.rlincludesignatue?.visibility = View.VISIBLE
            return
        }
        val signatureDAO = espLibSignatureDAO//args.getSerializable("body") as ESP_LIB_SignatureDAO

        if (signatureDAO?.type.equals(getString(R.string.esp_lib_text_font))) {
            view.rlincludesignatue?.visibility = View.VISIBLE
            view.llsignature?.visibility = View.GONE


            val fontList = resources.getStringArray(R.array.fontStyles).toList()
            val fontListName = resources.getStringArray(R.array.fontStylesName).toList()
            val indexOf = fontListName.indexOf(signatureDAO?.fontFamily)
            val getfont = fontList.get(indexOf)
            val typeface = Typeface.createFromAsset(requireContext().assets, "font/" + getfont)
            view.etxtsign?.setTypeface(typeface)
            view.etxtname.setText(signatureDAO?.signatoryName)
            view.etxtsign.setText(signatureDAO?.signatoryName)

        } else {
            view.etxtname.setText("")
            view.etxtsign.setText("")
            view.rlincludesignatue?.visibility = View.GONE
            view.llsignature?.visibility = View.VISIBLE

            view.ivsignature?.let {
                Glide.with(this).load(signatureDAO?.file?.downloadUrl)
                        .error(R.drawable.esp_lib_drawable_default_profile_picture)
                        .into(it)
            }
        }
    }

    private fun checkValidation(s: CharSequence?, view: View) {
        if (s?.replace("\\s".toRegex(), "").isNullOrEmpty()) {
            view.rlincludesignatue?.visibility = View.GONE
            view.txtpreview?.visibility = View.GONE
            view.btsave.isEnabled = false
            view.btsave.alpha = 0.5f
            view.btsave.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button)
            view.tilFieldname.isErrorEnabled = true
            view.tilFieldname.error = getString(R.string.esp_lib_text_required)
        } else {
            view.rlincludesignatue?.visibility = View.VISIBLE
            view.txtpreview?.visibility = View.VISIBLE
            view.btsave.isEnabled = true
            view.btsave.alpha = 1f
            view.btsave.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green)
            view.tilFieldname.isErrorEnabled = false
            view.tilFieldname.error = ""
        }
    }

    /* private fun popUpStyles(view: View) {


         if (materialAlertDialogBuilder == null) {


             val fontList = resources.getStringArray(R.array.fontStyles).toList()
             val fontListName = resources.getStringArray(R.array.fontStylesName).toList()
             val singleChoiceArr = fontListName.toTypedArray()
             var mSelectedIndex = singleChoiceArr.indexOf(view.etxtfontValue.text.toString())

             materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.Esp_Lib_Style_AlertDialogTheme)
                     .setTitle(getString(R.string.esp_lib_text_select_font_style))
                     .setCancelable(false)
                     .setSingleChoiceItems(singleChoiceArr, mSelectedIndex) { dialogInterface: DialogInterface, currencyPosition: Int ->
                         mSelectedIndex = materialAlertDialogBuilder?.listView?.checkedItemPosition!!
                         val selectedValue = fontListName[currencyPosition]
                         val selectedValueFont = fontList[currencyPosition]
                         view.etxtfontValue.setText(selectedValue)
                         setTypeFace(selectedValueFont, view)


                         materialAlertDialogBuilder = null
                         dialogInterface.cancel()
                     }
                     .setNegativeButton(requireContext().getString(R.string.esp_lib_text_cancel), DialogInterface.OnClickListener { dialogInterface, i ->
                         materialAlertDialogBuilder = null
                         dialogInterface.cancel()
                     })
                     .create()
             materialAlertDialogBuilder!!.show()
         }

     }*/

    private fun setTypeFace(selectedValueFont: String, view: View) {
        var setFont = R.raw.greatvibes_regular
        if (selectedValueFont.equals(getString(R.string.esp_lib_text_defaultt), ignoreCase = true)) {
            setFont = R.raw.greatvibes_regular
        }

        view.etxtsign?.typeface = ESP_LIB_GoogleFontsLibrary.setGoogleFont(context, setFont)
    }

}