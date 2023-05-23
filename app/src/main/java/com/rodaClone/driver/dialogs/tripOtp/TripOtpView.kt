package com.rodaClone.driver.dialogs.tripOtp

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.rodaClone.driver.R


class TripOtpView : LinearLayout {
    private var mOtpOneField: EditText? = null
    private var mOtpTwoField: EditText? = null
    private var mOtpThreeField: EditText? = null
    private var mOtpFourField: EditText? = null
    var currentFoucusedEditText: EditText? = null
        private set

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val styles = context.obtainStyledAttributes(attrs, R.styleable.OtpView)
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mInflater.inflate(R.layout.trip_otpview_layout, this)
        mOtpOneField = findViewById(R.id.otp_one_edit_text)
        mOtpTwoField = findViewById(R.id.otp_two_edit_text)
        mOtpThreeField = findViewById(R.id.otp_three_edit_text)
        mOtpFourField = findViewById(R.id.otp_four_edit_text)
        styleEditTexts(styles)
        styles.recycle()
    }

    /**
     * Get an instance of the present otp
     */
    private fun makeOTP(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(mOtpOneField!!.text.toString())
        stringBuilder.append(mOtpTwoField!!.text.toString())
        stringBuilder.append(mOtpThreeField!!.text.toString())
        stringBuilder.append(mOtpFourField!!.text.toString())
        return stringBuilder.toString()
    }

    /**
     * Checks if all four fields have been filled
     *
     * @return length of OTP
     */
    fun hasValidOTP(): Boolean {
        return makeOTP().length == 4
    }
    /**
     * Returns the present otp entered by the users
     *
     * @return OTP
     */
    /**
     * Used to set the OTP. More of cosmetic value than functional value
     *
     * @param otp Send the four digit otp
     */
    var oTP: String
        get() = makeOTP()
        set(otp) {
            if (otp.length != 4) {
//                Log.e("OTPView", "Invalid otp param")
                return
            }
            if (mOtpOneField!!.inputType == InputType.TYPE_CLASS_NUMBER
                && !otp.matches("[0-9]+".toRegex())
            ) {
//                Log.e("OTPView", "OTP doesn't match INPUT TYPE")
                return
            }
            mOtpOneField!!.setText(otp[0].toString())
            mOtpTwoField!!.setText(otp[1].toString())
            mOtpThreeField!!.setText(otp[2].toString())
            mOtpFourField!!.setText(otp[3].toString())
        }

    fun setOTPCustom(otp: String) {
        if (otp.isEmpty()) {
            mOtpOneField?.setText("")
            mOtpTwoField?.setText("")
            mOtpThreeField?.setText("")
            mOtpFourField?.setText("")
            return
        }
        if (mOtpOneField!!.inputType == InputType.TYPE_CLASS_NUMBER
            && !otp.matches("[0-9]+".toRegex())
        ) {
//            Log.e("OTPView", "OTP doesn't match INPUT TYPE")
            return
        }
        val editTexts = arrayOf(mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField)
        for (i in 0 until otp.length) {
            editTexts[i]!!.setText(otp[i].toString())
        }
    }

    private fun styleEditTexts(styles: TypedArray) {
//        int textColor = styles.getColor(R.styleable.OtpView_android_textColor, Color.BLACK);
//        int backgroundColor =
//                styles.getColor(R.styleable.OtpView_text_background_color, Color.TRANSPARENT);
//        if (styles.getColor(R.styleable.OtpView_text_background_color, Color.TRANSPARENT)
//                != Color.TRANSPARENT) {
//            mOtpOneField.setBackgroundColor(backgroundColor);
//            mOtpTwoField.setBackgroundColor(backgroundColor);
//            mOtpThreeField.setBackgroundColor(backgroundColor);
//            mOtpFourField.setBackgroundColor(backgroundColor);
//        } else {
//            mOtpOneField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
//            mOtpTwoField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
//            mOtpThreeField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
//            mOtpFourField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
//        }
//        mOtpOneField.setTextColor(textColor);
//        mOtpTwoField.setTextColor(textColor);
//        mOtpThreeField.setTextColor(textColor);
//        mOtpFourField.setTextColor(textColor);
        setEditTextInputStyle(styles)
    }

    private fun setEditTextInputStyle(styles: TypedArray) {
        val inputType = styles.getInt(
            R.styleable.OtpView_android_inputType,
            EditorInfo.TYPE_TEXT_VARIATION_NORMAL
        )
        mOtpOneField!!.inputType = inputType
        mOtpTwoField!!.inputType = inputType
        mOtpThreeField!!.inputType = inputType
        mOtpFourField!!.inputType = inputType
        val text = styles.getString(R.styleable.OtpView_otp)
        if (!TextUtils.isEmpty(text) && text!!.length == 6) {
            mOtpOneField!!.setText(text[0].toString())
            mOtpTwoField!!.setText(text[1].toString())
            mOtpThreeField!!.setText(text[2].toString())
            mOtpFourField!!.setText(text[3].toString())
        }
        setFocusListener()
        setOnTextChangeListener()
    }

    private fun setFocusListener() {
        val onFocusChangeListener: OnFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                currentFoucusedEditText = v as EditText
                currentFoucusedEditText!!.setSelection(currentFoucusedEditText!!.text.length)
            }
        }
        mOtpOneField!!.onFocusChangeListener = onFocusChangeListener
        mOtpTwoField!!.onFocusChangeListener = onFocusChangeListener
        mOtpThreeField!!.onFocusChangeListener = onFocusChangeListener
        mOtpFourField!!.onFocusChangeListener = onFocusChangeListener
    }

    fun disableKeypad() {
        val touchListener = OnTouchListener { v, event ->
            v.onTouchEvent(event)
            val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
            true
        }
        mOtpOneField!!.setOnTouchListener(touchListener)
        mOtpTwoField!!.setOnTouchListener(touchListener)
        mOtpThreeField!!.setOnTouchListener(touchListener)
        mOtpFourField!!.setOnTouchListener(touchListener)
    }

    fun enableKeypad() {
        val touchListener = OnTouchListener { v, event -> false }
        mOtpOneField!!.setOnTouchListener(touchListener)
        mOtpTwoField!!.setOnTouchListener(touchListener)
        mOtpThreeField!!.setOnTouchListener(touchListener)
        mOtpFourField!!.setOnTouchListener(touchListener)
    }

    private fun setOnTextChangeListener() {
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (currentFoucusedEditText != null && currentFoucusedEditText!!.text != null) if (currentFoucusedEditText!!.text.length >= 1
                    && currentFoucusedEditText !== mOtpFourField
                ) { //mOtpSixField) {
                    currentFoucusedEditText!!.focusSearch(FOCUS_RIGHT).requestFocus()
                } else if (currentFoucusedEditText!!.text.length >= 1
                    && currentFoucusedEditText === mOtpFourField
                ) { //mOtpSixField) {
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(windowToken, 0)
                } else {
                    val currentValue = currentFoucusedEditText!!.text.toString()
                    if (currentValue.length <= 0 && currentFoucusedEditText!!.selectionStart <= 0) {
                        currentFoucusedEditText!!.focusSearch(FOCUS_LEFT).requestFocus()
                    }
                }
            }
        }
        mOtpOneField!!.addTextChangedListener(textWatcher)
        mOtpTwoField!!.addTextChangedListener(textWatcher)
        mOtpThreeField!!.addTextChangedListener(textWatcher)
        mOtpFourField!!.addTextChangedListener(textWatcher)
    }

    fun simulateDeletePress() {
        currentFoucusedEditText!!.setText("")
    }
}

