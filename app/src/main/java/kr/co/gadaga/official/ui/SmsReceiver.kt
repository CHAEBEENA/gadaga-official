package kr.co.gadaga.official.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SmsReceiver :BroadcastReceiver() {

    private var otpReceiver : OTPReceiveListener?= null

    fun initOTPListener(receiver:OTPReceiveListener){
        this.otpReceiver = receiver
    }


    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    // But here we are just passing it to MainActivity
                    if (otpReceiver != null) {
                       // otp = otp.replace("<#> Your ExampleApp code is: ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        otp = extractNumber(otp)
                        Log.e("chaebeen","Otp:$otp")
                        otpReceiver!!.onOTPReceived(otp)
                    }
                }

                CommonStatusCodes.TIMEOUT ->
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                   //otpReceiver!!.onOTPTimeOut()
                    Toast.makeText(context, "입력가능한 시간이 초과되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }




    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut(){
                   }
    }


    //문자열에서 숫자만 추출
    private fun extractNumber(str:String):String{
        var number=""
        val pattern = Pattern.compile("\\d+")
        val matcher = pattern.matcher(str)

        while(matcher.find()){
            number += matcher.group()
        }
        return number.substring(0,6)
    }
}