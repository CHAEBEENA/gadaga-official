package kr.co.gadaga.official.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.renderscript.ScriptGroup
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.util.helper.Utility.getPackageInfo
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.data.OAuthLoginState
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kr.co.gadaga.official.R
import org.json.JSONObject
import retrofit2.http.GET
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UncheckedIOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.NoSuchElementException

class LoginActivity :AppCompatActivity () {

    private var callback: SessionCallback = SessionCallback()
    private lateinit var nhnOAuthLoginModule: OAuthLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val loginPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val editor = loginPreferences.edit()


        val loginId = loginPreferences.getString("id", null)
        val loginPw = loginPreferences.getString("password", null)
        val autoLogin = loginPreferences.getBoolean("autoLogin", false)




        if (autoLogin) {
            if (loginId.equals("gadaga") && loginPw.equals("123456")) {
                Toast.makeText(this, loginId + "님 자동로그인 입니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else if (!autoLogin) {
            loginButton.setOnClickListener {
                if (loginIdEditText.text.toString().equals("gadaga") && loginPwEditText.text.toString().equals(
                        "123456"
                    )
                ) {
                    editor.putString("id", loginIdEditText.text.toString())
                    editor.putString("password", loginPwEditText.text.toString())
                    editor.putBoolean("autoLogin", true)
                    editor.commit()
                    Toast.makeText(
                        this,
                        loginIdEditText.text.toString() + loginId + "님 환영합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "아이디 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        logoutButton.setOnClickListener {
            Toast.makeText(this, loginId + "로그아웃", Toast.LENGTH_SHORT).show()
            editor.clear()
            editor.commit()
            Toast.makeText(this, getHashKey(this), Toast.LENGTH_SHORT).show()
            Log.e("HashKey", getHashKey(this))
        }

        //카카오 제공 버튼
        Session.getCurrentSession().addCallback(callback)

        //커스텀버튼
        kakao_login_btn.setOnClickListener {
            val session = Session.getCurrentSession()
            session.addCallback(callback)
            session.open(AuthType.KAKAO_LOGIN_ALL, this)
        }

        //네이버 로그인
        nhnOAuthLoginModule = OAuthLogin.getInstance()
        nhnOAuthLoginModule.init(
            this, getString(R.string.nhn_oauth_client_id),
            getString(R.string.nhn_oauth_client_secret),
            getString(R.string.nhn_oauth_client_name)
        )

        initOnClickListener()
    }

    //로그인 성공시 호출
    fun successLogin() {
        Toast.makeText(this, "네이버 로그인 성공", Toast.LENGTH_SHORT).show()
    }

    private fun initOnClickListener() {
        button_naverlogin.setOnClickListener {
            nhnSignIn()
        }
    }

    //네이버 로그인 콜백
    private fun nhnSignIn() {
        if (nhnOAuthLoginModule.getState(this@LoginActivity) == OAuthLoginState.OK) {
            Log.d("TAG", "Nhn status don't need login")
            RequestNHNLoginApiTask().execute()

            val accessToken = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)
            NaverProfileTask().execute(accessToken)


        } else {
            Log.d("TAG", "Nhn status need login")
            nhnOAuthLoginModule.startOauthLoginActivity(this, @SuppressLint("HandlerLeak")
            object : OAuthLoginHandler() {
                override fun run(success: Boolean) {
                    if (success) {

                        val accessToken = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)
                        val refreshToken = nhnOAuthLoginModule.getRefreshToken(this@LoginActivity)
                        val expiresAt = nhnOAuthLoginModule.getExpiresAt(this@LoginActivity)
                        val tokenType = nhnOAuthLoginModule.getTokenType(this@LoginActivity)
                        Log.i("TAG", "nhn Login Access Token : $accessToken")
                        Log.i("TAG", "nhn Login refresh Token : $refreshToken")
                        Log.i("TAG", "nhn Login expiresAt : $expiresAt")
                        Log.i("TAG", "nhn Login Token Type : $tokenType")
                        Log.i(
                            "TAG",
                            "nhn Login Module State : " + nhnOAuthLoginModule.getState(this@LoginActivity).toString()
                        )

                        //유저정보 가져오기
                        val task = NaverProfileTask()
                        task.execute(accessToken)

                        successLogin()
                    } else {
                        val errorCode =
                            nhnOAuthLoginModule.getLastErrorCode(this@LoginActivity).getCode()
                        val errorDesc = nhnOAuthLoginModule.getLastErrorDesc(this@LoginActivity)
                        Toast.makeText(
                            this@LoginActivity,
                            "errorCode:$errorCode, errorDesc:$errorDesc",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            )
        }

    }

    inner class RequestNHNLoginApiTask : AsyncTask<Void, Void, String>() {


        override fun onPostExecute(result: String?) {
            Log.d("TAG", "onPreExecute : $result")
            val startToken = "<message>"
            val endToken = "</message>"
            val startIndex = result?.indexOf(startToken) ?: -1
            val endIndex = result?.indexOf(endToken) ?: -1
            if (startIndex == -1 || endIndex <= 0) {
                return
            }
            val message = result?.substring(startIndex + startToken.length, endIndex)?.trim()
            if (message.equals("success")) {
                Log.d("TAG", "Success RequestNHNLoginApiTask")
                successLogin()
            } else {
                Log.e("TAG", "Login Fail")
            }
        }

        override fun doInBackground(vararg params: Void?): String {
            val url = "https://apis.naver.com/nidlogin/nid/getHashId_v2.xml"
            val at = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)
            Log.e("accesstoken",at)
            Log.e("TASIiK", nhnOAuthLoginModule.requestApi(this@LoginActivity, at, url))


            return nhnOAuthLoginModule.requestApi(this@LoginActivity, at, url)
        }

        override fun onPreExecute() {

        }
    }

    //유저 정보 가져오기
    inner class NaverProfileTask : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String? {

            var result: String? = null

            Log.e("UserInfo","testeststest")


            val token = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)// 네이버 로그인 접근 토큰;
            val header = "Bearer $token" // Bearer 다음에 공백 추가
            try {
                val apiURL = "https://openapi.naver.com/v1/nid/me"
                val url = URL(apiURL)
                val con = url.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", header)
                val responseCode = con.responseCode
                val br: BufferedReader
                br = if (responseCode == 200) {
                    BufferedReader(InputStreamReader(con.inputStream))
                } else {
                    BufferedReader(InputStreamReader(con.errorStream))
                }

                var inputLine: String?
                val response = StringBuffer()
                inputLine = br.readLine()
                while(true) {
                    if(inputLine != null) {
                        response.append(inputLine)
                        inputLine = br.readLine()
                    } else
                        break
                }

                result = response.toString()
                br.close()
                Log.e("tagggggg",response.toString())
            } catch (e: Exception) {
                Log.e("tagaaaaa",e.toString())
            }

            //result 값은 JSONObject 형태
            return result

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                Log.d("TAG", "결과 : $result")
                if (jsonObject.getString("resultcode") == "00") {
                    val jsonObject = JSONObject(jsonObject.getString("response"))
                    val email = jsonObject.getString("id")
                    Log.d("jsonObject", jsonObject.toString())
                    Log.e("EMAIL", email)

                    /*SharedPreferences.Editor editor = activity.userData.edit();
                    editor.putString("email", jsonObject.getString("email"));
                    editor.putString("name", jsonObject.getString("name"));
                    editor.putString("nickname", jsonObject.getString("nickname"));
                    editor.putString("profile", jsonObject.getString("profile_image"));
                    editor.apply();
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);*/
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


        //해시키 구하기
        fun getHashKey(context: Context): String? {
            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    val packageInfo =
                        getPackageInfo(context, PackageManager.GET_SIGNING_CERTIFICATES)
                    val signatures = packageInfo.signingInfo.apkContentsSigners
                    val md = MessageDigest.getInstance("SHA")
                    for (signature in signatures) {
                        md.update(signature.toByteArray())
                        return String(Base64.encode(md.digest(), NO_WRAP))
                    }
                } else {
                    val packageInfo =
                        getPackageInfo(context, PackageManager.GET_SIGNATURES) ?: return null

                    for (signature in packageInfo!!.signatures) {
                        try {
                            val md = MessageDigest.getInstance("SHA")
                            md.update(signature.toByteArray())
                            return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                        } catch (e: NoSuchAlgorithmException) {
                            Log.e(
                                "TAG", "Unable to get MessageDigest. signature=$signature"
                            )
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return null
        }


    }







































