package utilities.interfaces

import utilities.data.CriteriaRejectionfeedback.ESP_LIB_FeedbackDAO

interface ESP_LIB_FeedbackConfirmationListener {

    fun isClickable(ESPLIBFeedbackList: List<ESP_LIB_FeedbackDAO>)
    fun editComment(ESPLIBFeedbackDAO: ESP_LIB_FeedbackDAO)
}
