package utilities.data.applicants


import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO

class ESP_LIB_ApplicationSingleton {

    var application: ESP_LIB_DynamicResponseDAO? = null

    companion object {

        var applicationSingleton: ESP_LIB_ApplicationSingleton? = null
        val instace: ESP_LIB_ApplicationSingleton
            get() {
                if (applicationSingleton == null) {
                    applicationSingleton = ESP_LIB_ApplicationSingleton()
                }
                return applicationSingleton as ESP_LIB_ApplicationSingleton
            }
    }


}
