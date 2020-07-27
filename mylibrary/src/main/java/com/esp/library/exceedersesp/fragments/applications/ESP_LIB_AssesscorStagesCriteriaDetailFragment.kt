package com.esp.library.exceedersesp.fragments.applications

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity

import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared
import kotlinx.android.synthetic.main.esp_lib_fragment_assessor_stages_criteria_detail.view.*
import utilities.adapters.setup.applications.ESP_LIB_ListStagesCriteriaDetailAdapter
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO


class ESP_LIB_AssesscorStagesCriteriaDetailFragment : androidx.fragment.app.Fragment() {

    internal var context: ESP_LIB_BaseActivity? = null
    private var mStageAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>? = null
    private var mStageLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var mStage: ESP_LIB_DynamicStagesDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity as ESP_LIB_BaseActivity?

        if (arguments != null) {
            mStage = requireArguments().getSerializable(ESP_LIB_DynamicStagesDAO.BUNDLE_KEY) as ESP_LIB_DynamicStagesDAO

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_fragment_assessor_stages_criteria_detail, container, false)
        initailize(v)


        if (ESP_LIB_ApplicationSingleton.instace.application != null) {
            DetailStagesCriteriaView(v).execute()
        }

        return v
    }

    private fun initailize(v: View) {
        mStageLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        v.app_list.setHasFixedSize(true)
        v.app_list.layoutManager = mStageLayoutManager
        v.app_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
    }

    fun UpdateCriteriaList(v: View?) {
        if (ESP_LIB_ApplicationSingleton.instace.application != null) {
            v?.let { DetailStagesCriteriaView(it).execute() }
        }
    }

    inner class DetailStagesCriteriaView(vv: View) : AsyncTask<Void, Void, List<ESP_LIB_DynamicStagesCriteriaListDAO>>() {

        var v:View =vv

        override fun doInBackground(vararg param: Void): List<ESP_LIB_DynamicStagesCriteriaListDAO>? {
            var stages_criterionESPLIBS: List<ESP_LIB_DynamicStagesCriteriaListDAO>? = null

            if (mStage != null) {
                stages_criterionESPLIBS = ESP_LIB_Shared.getInstance().GetCriteriaListByStageId(mStage!!.id)
            }
            return stages_criterionESPLIBS
        }

        override fun onPostExecute(stages_criterionESPLIBS: List<ESP_LIB_DynamicStagesCriteriaListDAO>?) {
            super.onPostExecute(stages_criterionESPLIBS)



            if (stages_criterionESPLIBS != null && stages_criterionESPLIBS.size > 0) {
                mStageAdapter = context?.let { ESP_LIB_ListStagesCriteriaDetailAdapter(stages_criterionESPLIBS,mStage, it, "") }
                v.app_list.adapter = mStageAdapter
                mStageAdapter!!.notifyDataSetChanged()
                v.detail_view.visibility = View.VISIBLE
                v.no_record.visibility = View.GONE
                UpdateView(v)
            } else {
                v.detail_view.visibility = View.GONE
                v.no_record.visibility = View.VISIBLE
            }

        }
    }

    private fun UpdateView(v: View) {
        if (mStage != null) {
            if (mStage!!.isAll) {
                v.condition_value.text = getString(R.string.esp_lib_text_all)
            } else {
                v.condition_value.text = getString(R.string.esp_lib_text_any)
            }

            v.sequence_value.text = mStage!!.order.toString() + ""

            if (mStage!!.criteriaList != null && mStage!!.criteriaList!!.size > 0) {
                v.criteria_count.text = mStage!!.criteriaList!!.size.toString() + ""
            } else {
                v.criteria_count.text = "0"
            }

        }
    }

    companion object {
        fun newInstance(stagesESPLIB: ESP_LIB_DynamicStagesDAO): ESP_LIB_AssesscorStagesCriteriaDetailFragment {
            val fragment = ESP_LIB_AssesscorStagesCriteriaDetailFragment()
            val args = Bundle()
            args.putSerializable(ESP_LIB_DynamicStagesDAO.BUNDLE_KEY, stagesESPLIB)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
