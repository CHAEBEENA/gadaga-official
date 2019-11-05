package kr.co.gadaga.official

import android.app.Application
import com.kakao.auth.KakaoSDK
import kr.co.gadaga.official.ui.KakaoSDKAdapter

class GadagaApplication: Application() {


    /* kodein */
    override fun onCreate() {
        super.onCreate()

        instance = this

        KakaoSDK.init(KakaoSDKAdapter())
    }


    fun getGadagaApplicationContext() : GadagaApplication {
       /*
       if(instance == null) {
            throw IllegalStateException("This Application does not inherit gadaga.official.GadagaApplication")
        }
        */
        checkNotNull(instance) {"This Application does not inherit gadaga.official.GadagaApplication"}
        return instance!!
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    companion object {
        var instance:GadagaApplication ?= null
    }
}