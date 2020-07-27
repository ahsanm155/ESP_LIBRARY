package com.esp.library.exceedersesp.fragments.applications

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esp.library.R

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity

import kotlinx.android.synthetic.main.esp_lib_fragment_assessor_stages_detail.view.*
import utilities.adapters.setup.applications.ESP_LIB_ListStagesDetailAdapter
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO


class ESP_LIB_AssesscorStagesDetailFragment : androidx.fragment.app.Fragment() {

    internal var context: ESP_LIB_BaseActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity as ESP_LIB_BaseActivity?

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_fragment_assessor_stages_detail, container, false)
        initailize(v)

        if (ESP_LIB_ApplicationSingleton.instace.application != null) {
            DetailStagesView(v).execute()
        }

        return v
    }

    private fun initailize(v: View) {
        val mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        v.app_list.setHasFixedSize(true)
        v.app_list.layoutManager = mApplicationLayoutManager
        v.app_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
    }


    private inner class DetailStagesView(vv: View) : AsyncTask<Void, Void, List<ESP_LIB_DynamicStagesDAO>>() {

        var v:View =vv

        override fun doInBackground(vararg param: Void): List<ESP_LIB_DynamicStagesDAO>? {
            var stages: List<ESP_LIB_DynamicStagesDAO>? = null

            if (ESP_LIB_ApplicationSingleton.instace.application != null) {
                stages = ESP_LIB_ApplicationSingleton.instace.application!!.stages

            }
            return stages
        }

        override fun onPostExecute(stages: List<ESP_LIB_DynamicStagesDAO>?) {
            super.onPostExecute(stages)

            if (stages != null && stages.size > 0) {
                val mApplicationAdapter = context?.let { ESP_LIB_ListStagesDetailAdapter(stages, it, "") }
                v.app_list.adapter = mApplicationAdapter
                mApplicationAdapter?.notifyDataSetChanged()

                v.detail_view.visibility = View.VISIBLE
                v.no_record.visibility = View.GONE
            } else {

                v.detail_view.visibility = View.GONE
                v.no_record.visibility = View.VISIBLE
            }

        }
    }

    companion object {
        fun newInstance(): ESP_LIB_AssesscorStagesDetailFragment {
            val fragment = ESP_LIB_AssesscorStagesDetailFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
