package utilities.data.applicants.dynamics

import utilities.data.ESP_LIB_Base
import java.io.Serializable
import java.util.*

class ESP_LIB_DynamicFormSectionDAO : ESP_LIB_Base(), Serializable {
    var id: Int = 0
    var defaultName: String? = null
    //var approveText: String? = null
    // var rejectText: String? = null
    var isMultipule: Boolean = false
    var isActive: Boolean = false
    var isDelete: Boolean = false
    var isDefault: Boolean = false
    var order: Int = 0
    var type: Int = 0
    //  var assessmentStatus: String? = null
    var lastUpdatedOn: String? = null
    var fields: List<ESP_LIB_DynamicFormSectionFieldDAO>? = null
    var isShowError = false
    var dynamicStagesCriteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO? = null
    var stages: List<ESP_LIB_DynamicStagesDAO>? = null

    //For PostResponse
    var nestedSections: List<ESP_LIB_DynamicFormSectionDAO>? = null

    //For LocalUse
    internal var fieldsCardsList: List<ESP_LIB_DynamicFormSectionFieldsCardsDAO>? = null

    fun getFieldsCardsList(): List<ESP_LIB_DynamicFormSectionFieldsCardsDAO> {

        if (fieldsCardsList == null)
            fieldsCardsList = ArrayList()

        return fieldsCardsList as List<ESP_LIB_DynamicFormSectionFieldsCardsDAO>
    }

    fun setFieldsCardsList(fieldsCardsList: MutableList<ESP_LIB_DynamicFormSectionFieldsCardsDAO>?) {
        var fieldsCardsList = fieldsCardsList

        if (fieldsCardsList == null)
            fieldsCardsList = ArrayList()

        fieldsCardsList.add(ESP_LIB_DynamicFormSectionFieldsCardsDAO(fields!!))

        this.fieldsCardsList = fieldsCardsList
    }

    fun setRefreshFieldsCardsList(fieldsCardsList: List<ESP_LIB_DynamicFormSectionFieldsCardsDAO>) {
        this.fieldsCardsList = fieldsCardsList
    }

    companion object {

        var BUNDLE_KEY = "DynamicFormSectionDAO"
    }

}
