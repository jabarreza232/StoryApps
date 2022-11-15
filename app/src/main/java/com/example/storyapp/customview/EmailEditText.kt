package com.example.storyapp.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns.EMAIL_ADDRESS
import com.google.android.material.textfield.TextInputEditText

class EmailEditText : TextInputEditText {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)


        error = when {
            text.isEmpty() ->{
                "Masukkan email"
            }
            !EMAIL_ADDRESS.matcher(text).matches() -> {
                "Silahkan input email yang valid !"
            }
            else -> {
                null
            }
        }
    }
}