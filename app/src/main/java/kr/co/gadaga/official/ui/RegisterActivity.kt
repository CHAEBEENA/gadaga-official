package kr.co.gadaga.official.ui

import android.app.Notification
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import kr.co.gadaga.official.R
import kr.co.gadaga.official.ui.KeyboardVisibilityUtils

class RegisterActivity :AppCompatActivity() {

    private var matchedNickname = false
    private var matchedEmail= false
    private var matchedPassword = false

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = {keyboardHeight->
                sv_register.run {
                    smoothScrollTo(scrollX,scrollY + keyboardHeight)
                }
            })



        val regexNickname = Regex(pattern = "[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]*")
        val regexEmail = Regex(pattern = "[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[ 0-9a-zA-Z])*.[a-zA-Z]{2,3}")
        val regexPassword = Regex(pattern = "(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#\$%^&+=])[A-Za-z0-9!@#\$%^&+=]{10,}") //문자6개 특수문자or숫자

        //[0-9a-zA-Z!@#$%^&+=]*





        nicknameEditText.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                matchedNickname = regexNickname.matches(input = s.toString())
                if (!matchedNickname) {nicknameCheckTextView.visibility = View.VISIBLE}
                else { nicknameCheckTextView.visibility = View.INVISIBLE}
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        emailEditText.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                matchedEmail = regexEmail.matches(input = s.toString())
                if (!matchedEmail)  {emailCheckTextView.visibility = View.VISIBLE}
                else { emailCheckTextView.visibility = View.INVISIBLE}
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        passwordEditText.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                matchedPassword = regexPassword.matches(input = s.toString())
                if (!matchedPassword) {passwordCheckTextVIew.visibility = View.VISIBLE}
                else {passwordCheckTextVIew.visibility = View.INVISIBLE}

                if(matchedNickname && matchedPassword && matchedEmail){
                    finishregisterButton.setBackgroundResource(R.color.colorAppPrimary)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })




    }

/*
    fun editTextCheck(v:EditText, listener : Notification.Action) {
        v.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                val matched = regexNickname.matches(input = s.toString())
                if (!matched) nicknameCheckTextView.visibility = View.VISIBLE

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }*/



}