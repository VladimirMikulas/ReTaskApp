package com.vlamik.retask.commons

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

interface StringResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
    fun getPluralString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String
}