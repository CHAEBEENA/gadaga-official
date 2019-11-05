package kr.co.gadaga.official.ui

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.plugins.RxAndroidPlugins
import kotlinx.android.synthetic.main.activity_tos.*
import kr.co.gadaga.official.R


class TosActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle? ){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tos)




        registerCheck6.setOnClickListener {
            if(registerCheck6.isChecked) {
                registerCheck1.isChecked = true
                registerCheck2.isChecked = true
                registerCheck3.isChecked = true
                registerCheck4.isChecked = true
                registerCheck5.isChecked = true
            }
            else{
                registerCheck1.isChecked = false
                registerCheck2.isChecked = false
                registerCheck3.isChecked = false
                registerCheck4.isChecked = false
                registerCheck5.isChecked = false
            }
        }

        nextStepRegisterButton.setOnClickListener{
            if(registerCheck1.isChecked && registerCheck2.isChecked && registerCheck3.isChecked && registerCheck4.isChecked) {
                //다음페이지로 넘어가기
                Toast.makeText(this, "회원가입", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PhoneVerificationActivity::class.java)
                startActivity(intent)

            }

            else
                Toast.makeText(this,"약관에 동의해주세요",Toast.LENGTH_SHORT).show()
        }
    }



}