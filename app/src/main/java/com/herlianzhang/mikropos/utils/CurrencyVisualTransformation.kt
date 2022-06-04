package com.herlianzhang.mikropos.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.herlianzhang.mikropos.utils.extensions.toCurrency
import kotlin.math.max

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedText = text.text.toCurrency()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return max(0, formattedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return max(0, formattedText.length)
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}