package kr.co.gadaga.official.ui


import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_phoneverification.*
import kr.co.gadaga.official.R


class PhoneVerificationActivity:AppCompatActivity(),  SmsReceiver.OTPReceiveListener{

    private val smsBroadcast = SmsReceiver()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phoneverification)



        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = {keyboardHeight->
                sv_phoneverification.run {
                    smoothScrollTo(scrollX,scrollY + keyboardHeight)
                }
            })


        val appSignature = AppSignatureHelper(this)
        appSignature.appSignatures


        phonenumberTextview.setOnClickListener {
            verificationTextview.visibility = View.VISIBLE
            verificationEdittext.visibility = View.VISIBLE
            verificationTimeTextview.visibility = View.VISIBLE
            verificationNextButton.visibility  = View.VISIBLE
            phonenumberTextview.setTextColor(resources.getColor(R.color.colorAppPrimary))
            startSMSListener()
        }

        smsBroadcast.initOTPListener(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

        applicationContext.registerReceiver(smsBroadcast, intentFilter)

        verificationNextButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }



    private fun startSMSListener(){

        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Toast.makeText(this, "SMS Retriever starts", Toast.LENGTH_SHORT).show()
        }

        task.addOnFailureListener{
            Toast.makeText(this,"메세지 전송 실패",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOTPReceived(otp: String) {

        Log.i("chaebeen","onOTPReceived 인증번호:$otp")

        if (smsBroadcast != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsBroadcast)
        }
        Toast.makeText(this, otp, Toast.LENGTH_SHORT).show()


        //otpTxtView.text = "Your OTP is: $otp"

        verificationEdittext.setText(otp)

        verificationEdittext.addTextChangedListener (object :TextWatcher{

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (verificationEdittext.text.toString() == otp) {
                    Toast.makeText(this@PhoneVerificationActivity, "인증성공", Toast.LENGTH_SHORT).show()
                    verificationNextButton.setBackgroundResource(R.color.colorAppPrimary)
                }
                else
                    Toast.makeText(this@PhoneVerificationActivity, "인증실패", Toast.LENGTH_SHORT).show()
            }

        })
                //
        Log.e("OTP Received", otp)


    }


    override fun onOTPTimeOut() {
        Toast.makeText(this, "입력가능한 시간이 초과되었습니다.", Toast.LENGTH_SHORT).show()
    }


}