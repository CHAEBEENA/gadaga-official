package kr.co.gadaga.official.ui

import android.content.Context
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.kakao.usermgmt.StringSet.email
import com.nhn.android.naverlogin.OAuthLogin
import org.json.JSONObject

private lateinit var nhnOAuthLoginModule : OAuthLogin



/*class NaverProfileTask : AsyncTask <String, Void, String>() {

    var result : String ?= null

    override fun doInBackground(vararg params: String?): String? {

        var result : String ?= null


        val token = strings[0]// 네이버 로그인 접근 토큰;
        val header = "Bearer $token" // Bearer 다음에 공백 추가
        try {
            val apiURL = "https://openapi.naver.com/v1/nid/me"
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod="GET"
            con.setRequestProperty("Authorization", header)
            val responseCode = con.responseCode
            val br: BufferedReader
            br = if(responseCode ==200) {
                BufferedReader(InputStreamReader(con.inputStream))
            } else {
                BufferedReader(InputStreamReader(con.errorStream))
            }
            var inputLine: String
            val response = StringBuffer()

            do{
                inputLine = br.readLine()
                if(inputLine == null)
                    break
                response.append(inputLine)
            } while (true)

            *//*while ((inputLine = br.readLine()) != null) {
                response.append(inputLine)
            }*//*

            result = response.toString()
            br.close()
            println(response.toString())
        } catch (e: Exception) {
            println(e)
        }

        //result 값은 JSONObject 형태로 넘어옵니다.

        Log.e("User info",result)
        Log.e("USER info","okokokok")

        //result 값은 JSONObject 형태로 넘어옵니다.
        return result

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        try {
            //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
            // result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
            val `object` = JSONObject(result)
            Log.d("TAG", "결과 : $result")
            if (`object`.getString("resultcode") == "00") {
                val jsonObject = JSONObject(`object`.getString("response"))
                val email = jsonObject.getString("id")
                Log.d("jsonObject", jsonObject.toString())
                Log.e("EMAIL",email)

                *//*SharedPreferences.Editor editor = activity.userData.edit();
                editor.putString("email", jsonObject.getString("email"));
                editor.putString("name", jsonObject.getString("name"));
                editor.putString("nickname", jsonObject.getString("nickname"));
                editor.putString("profile", jsonObject.getString("profile_image"));
                editor.apply();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);*//*
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}*/