package com.example.storyapp.customview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText : TextInputEditText {
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
                "Masukkan password"
            }
            text.length < 6 -> {
                "Tidak boleh input password kurang dari 6 karakter !"
            }
            else -> {
                null
            }
        }

    }
}