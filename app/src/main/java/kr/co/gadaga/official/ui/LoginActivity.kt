package kr.co.gadaga.official.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri.encode
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.view.inputmethod.InputMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.usermgmt.UserManagement
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility.getPackageInfo
import kotlinx.android.synthetic.main.activity_login.*
import kr.co.gadaga.official.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class LoginActivity :AppCompatActivity (){

    private var callback :SessionCallback = SessionCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        val loginPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val editor = loginPreferences.edit()


        val loginId = loginPreferences.getString("id",null)
        val loginPw = loginPreferences.getString("password",null)
        val autoLogin = loginPreferences.getBoolean("autoLogin",false)


        if(autoLogin) {
            if (loginId.equals("gadaga") && loginPw.equals("123456")) {
                Toast.makeText(this, loginId + "님 자동로그인 입니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        else if(!autoLogin){
            loginButton.setOnClickListener {
                if(loginIdEditText.text.toString().equals("gadaga") && loginPwEditText.text.toString().equals("123456")) {
                    editor.putString("id",loginIdEditText.text.toString())
                    editor.putString("password",loginPwEditText.text.toString())
                    editor.putBoolean("autoLogin",true)
                    editor.commit()
                    Toast.makeText(this,loginIdEditText.text.toString()+loginId+"님 환영합니다.",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"아이디 비밀번호가 틀렸습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }



        logoutButton.setOnClickListener{
            Toast.makeText(this,loginId +"로그아웃",Toast.LENGTH_SHORT).show()
            editor.clear()
            editor.commit()
            Toast.makeText(this,getHashKey(this),Toast.LENGTH_SHORT).show()
            Log.e("HashKey",getHashKey(this))
        }




    }

    fun getHashKey(context: Context): String? {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                val packageInfo = getPackageInfo(context, PackageManager.GET_SIGNING_CERTIFICATES)
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
                            "TAG","Unable to get MessageDigest. signature=$signature"
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






































