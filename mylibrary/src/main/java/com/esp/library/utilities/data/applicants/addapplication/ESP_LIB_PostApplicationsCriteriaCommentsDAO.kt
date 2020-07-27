package utilities.data.applicants.addapplication

import java.io.Serializable

import utilities.data.ESP_LIB_Base


class ESP_LIB_PostApplicationsCriteriaCommentsDAO : ESP_LIB_Base(), Serializable {

    var id: Int = 0
    var commentId: Int = 0
    var assessmentId: Int = 0
    var comments: String? = null

    companion object {
        var BUNDLE_KEY = "PostApplicationsCriteriaCommentsDAO"
    }


}
