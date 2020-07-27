package com.esp.library.exceedersesp.fragments.applications

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import kotlinx.android.synthetic.main.esp_lib_fragment_assessor_stages_criteria_comments.view.*

import utilities.adapters.setup.applications.ESP_LIB_ListApplicationStageCriteriaCommentsAdapter
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaCommentsListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO


class ESP_LIB_AssesscorApplicationStagesCriteriaCommentsFragment : androidx.fragment.app.Fragment() {

    internal var context: ESP_LIB_BaseActivity? = null
    internal var imm: InputMethodManager? = null

    internal var mCriteriaESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity as ESP_LIB_BaseActivity?

        if (arguments != null) {
            mCriteriaESPLIB = requireArguments().getSerializable(ESP_LIB_DynamicStagesCriteriaListDAO.BUNDLE_KEY) as ESP_LIB_DynamicStagesCriteriaListDAO

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_fragment_assessor_stages_criteria_comments, container, false)
        initailize(v)
        if (ESP_LIB_ApplicationSingleton.instace.application != null) {
            DetailStagesCriteriaCommentsView(v).execute()
        }

        return v
    }

    private fun initailize(v: View) {
        val mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        v.app_list.setHasFixedSize(true)
        v.app_list.layoutManager = mApplicationLayoutManager
        v.app_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }


    fun UpdateCriteriaCommentsList(v: View?) {
        if (ESP_LIB_ApplicationSingleton.instace.application != null) {
            v?.let { DetailStagesCriteriaCommentsView(it).execute() }
        }
    }

    inner class DetailStagesCriteriaCommentsView(vv: View) : AsyncTask<Void, Void, List<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>>() {

        var v: View =vv

        override fun doInBackground(vararg param: Void): List<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>? {
            var stages_criterionESPLIBS: List<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>? = null

            if (mCriteriaESPLIB != null) {
                stages_criterionESPLIBS = ESP_LIB_Shared.getInstance().GetCriteriaCommentsListByCriteriaId(mCriteriaESPLIB!!.id)
            }
            return stages_criterionESPLIBS
        }

        override fun onPostExecute(stages_criterionESPLIBS: List<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>?) {
            super.onPostExecute(stages_criterionESPLIBS)

            // UpdateView();

            if (stages_criterionESPLIBS != null && stages_criterionESPLIBS.size > 0) {
                val mApplicationAdapter = context?.let { ESP_LIB_ListApplicationStageCriteriaCommentsAdapter(stages_criterionESPLIBS, it, "") }
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

        fun newInstance(criteriaESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO): ESP_LIB_AssesscorApplicationStagesCriteriaCommentsFragment {
            val fragment = ESP_LIB_AssesscorApplicationStagesCriteriaCommentsFragment()
            val args = Bundle()
            args.putSerializable(ESP_LIB_DynamicStagesCriteriaListDAO.BUNDLE_KEY, criteriaESPLIB)
            fragment.arguments = args
            return fragment
        }
    }


}// Required empty public constructor
