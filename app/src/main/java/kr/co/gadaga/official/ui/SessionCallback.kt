package kr.co.gadaga.official.ui

import android.util.Log
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException



class SessionCallback : ISessionCallback {

    //로그인 실패
    override fun onSessionOpenFailed(exception: KakaoException?) {
        Log.e("SessionCallback::","onSessionOpenFailed:"+exception?.message)
    }

    //로그인 성공
    override fun onSessionOpened() {
        requestMe()
    }

    //사용자 정보 요청
    fun requestMe() {
        //사용자 정보 요청 결과에 대한 Callback
        UserManagement.requestMe(object :MeResponseCallback() {
            //세션 오픈 실패, 세션이 삭제된 경우
            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.e("SessionCallback::","onSessionClosed:" + errorResult?.errorMessage)
            }

            //회원이 아닌 경우
            override fun onNotSignedUp() {
                Log.e("SessionCallback::","onNotSignUp")
            }

            //사용자 정보 요청 성공
            override fun onSuccess(result: UserProfile?) {
                Log.e("SessionCallback::","onSuccess")

                val nickname = result?.nickname

                val email = result?.email

                val id = result?.id



                Log.e("Profile : ", nickname + "")

                Log.e("Profile : ", email + "")

                Log.e("Profile : ", id.toString() + "")


            }

            //사용자 정보 요청 실패
            override fun onFailure(errorResult: ErrorResult?) {
                Log.e("SessionCallback::","onFailure:"+errorResult?.errorMessage)
            }

        })
    }

}