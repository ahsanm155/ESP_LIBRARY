package com.esp.library.exceedersesp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.esp.library.R
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.exceedersesp.SingleController.ConnectionListener
import com.esp.library.utilities.common.ESP_LIB_Shared
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers


open class ESP_LIB_BaseActivity : AppCompatActivity() {


    var bContext: ESP_LIB_BaseActivity? = null
    private var activityDestroyed = false
    internal var onStartCount = 0
   /* private var compRoot: CompRoot? = null
    private var disposable: CompositeDisposable? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  Shared.getInstance().translucentStatusBar(this)
        bContext = this@ESP_LIB_BaseActivity
        onStartCount = 1
        if (savedInstanceState == null)
        // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
        } else {
            onStartCount = 2
        }
       /* compRoot = CompRoot()
        disposable = CompositeDisposable()
        addInternetConnectionListener()*/
    }

   /* open fun getCompRoot(): CompRoot? {
        return compRoot
    }

    open fun addInternetConnectionListener() {
        disposable!!.add(ConnectionListener.getInstance()
                .listenNetworkChange().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<Boolean?> {
                    @Throws(Exception::class)
                    override fun accept(aBoolean: Boolean?) {
                        onInternetUnavailable()
                    }
                }))
    }


    protected open fun onInternetUnavailable() {
        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_internet_connection_error), bContext)
    }*/




    protected open fun changeStatusBarColor(alsoFullScreen: Boolean) {
        val window = window
        if (alsoFullScreen) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    override fun onDestroy() {
        super.onDestroy()
        activityDestroyed = true
      //  disposable?.dispose()
    }


    override fun onStart() {
        super.onStart()
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)

        } else if (onStartCount == 1) {
            onStartCount++
        }
    }


}
