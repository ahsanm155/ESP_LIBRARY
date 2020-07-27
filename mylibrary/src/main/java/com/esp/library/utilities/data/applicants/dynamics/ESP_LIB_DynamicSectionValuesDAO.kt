package utilities.data.applicants.dynamics

import utilities.data.ESP_LIB_Base
import java.io.Serializable

class ESP_LIB_DynamicSectionValuesDAO : ESP_LIB_Base(), Serializable {

    var id: Int = 0
    var instances: List<Instance>? = null

    class Instance : Serializable{

        var values: List<Value>? = null

        class Value : Serializable{

            var sectionCustomFieldId: Int = 0
            var type: Int = 0
            var sectionId: Int = 0
            var sectionIndex: Int = 0
            var value: String? = null
            var id: Int = 0
            var selectedLookupText: String? = null
            var details: ESP_LIB_DynamicFormValuesDetailsDAO? = null
        }
    }

    companion object {

        var BUNDLE_KEY = "DynamicSectionValuesDAO"
    }

}
