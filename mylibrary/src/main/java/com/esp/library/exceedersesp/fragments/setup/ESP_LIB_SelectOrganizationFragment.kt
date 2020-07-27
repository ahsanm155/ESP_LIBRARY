package com.esp.library.exceedersesp.fragments.setup

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.esp.library.R
import kotlinx.android.synthetic.main.esp_lib_select_organization_fragment.view.*
import utilities.data.setup.ESP_LIB_PersonaDAO
import utilities.data.setup.ESP_LIB_TokenDAO


class ESP_LIB_SelectOrganizationFragment : androidx.fragment.app.Fragment() {

    internal var personas: ESP_LIB_TokenDAO? = null
    internal var context: ESP_LIB_BaseActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity as ESP_LIB_BaseActivity?
        if (arguments != null) {
            personas = requireArguments().getSerializable(ESP_LIB_TokenDAO.BUNDLE_KEY) as ESP_LIB_TokenDAO

            if (personas != null && personas!!.personas != null && personas!!.personas.length > 0) {
                LoadPersonas().execute(personas)
            }


        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_select_organization_fragment, container, false)
        initailize(v)
        return v
    }

    private fun initailize(v:View) {
        val mOrgLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        v.org_list.setHasFixedSize(true)
        v.org_list.layoutManager = mOrgLayoutManager
        v.org_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
    }

    private inner class LoadPersonas : AsyncTask<ESP_LIB_TokenDAO, Void, List<ESP_LIB_PersonaDAO>>() {

        override fun doInBackground(vararg listPersonaDAOESPLIBS: ESP_LIB_TokenDAO): List<ESP_LIB_PersonaDAO>? {

            var list: List<ESP_LIB_PersonaDAO>? = null

            if (listPersonaDAOESPLIBS[0] != null) {
                val personas = listPersonaDAOESPLIBS[0].personas
                val gson = Gson()
                list = gson.fromJson<List<ESP_LIB_PersonaDAO>>(personas, object : TypeToken<List<ESP_LIB_PersonaDAO>>() {

                }.type)
            }

            return list
        }

        override fun onPostExecute(ESPLIBPersonaDAOS: List<ESP_LIB_PersonaDAO>?) {
            super.onPostExecute(ESPLIBPersonaDAOS)
        }
    }

    companion object {

        fun newInstance(persona: ESP_LIB_TokenDAO): ESP_LIB_SelectOrganizationFragment {
            val fragment = ESP_LIB_SelectOrganizationFragment()
            val args = Bundle()
            args.putSerializable(ESP_LIB_TokenDAO.BUNDLE_KEY, persona)
            fragment.arguments = args
            return fragment
        }
    }
}
