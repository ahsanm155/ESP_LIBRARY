package utilities.data.applicants.addapplication

import java.io.Serializable

import utilities.data.ESP_LIB_Base
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDAO


class ESP_LIB_PostApplicationsStatusDAO : ESP_LIB_Base(), Serializable {


    var applicationId: Int = 0
    var assessmentId: Int = 0
    var comments: String? = null
    var isAccepted: Boolean = false
    var isVisibletoApplicant: Boolean = false
    var stageId: Int = 0
    var values: List<ESP_LIB_DynamicFormValuesDAO>? = null

    companion object {
        var BUNDLE_KEY = "PostApplicationsStatusDAO"
    }
}
