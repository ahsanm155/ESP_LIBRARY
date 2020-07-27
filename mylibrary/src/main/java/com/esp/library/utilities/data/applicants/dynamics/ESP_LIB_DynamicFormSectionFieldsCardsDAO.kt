package utilities.data.applicants.dynamics

import java.util.HashMap

import utilities.data.ESP_LIB_Base

class ESP_LIB_DynamicFormSectionFieldsCardsDAO : ESP_LIB_Base {

    var sectionId: Int = 0

    var fields: List<ESP_LIB_DynamicFormSectionFieldDAO>? = null
    var values: List<ESP_LIB_DynamicSectionValuesDAO.Instance.Value>? = null

    var valuesHashMap = HashMap<Int, List<ESP_LIB_DynamicSectionValuesDAO.Instance.Value>>()

    lateinit var tag: String

    constructor() {

    }

    constructor(fields: List<ESP_LIB_DynamicFormSectionFieldDAO>) {
        this.fields = fields
    }

    companion object {

        var BUNDLE_KEY = "DynamicFormSectionFieldsCardsDAO"
    }
}
