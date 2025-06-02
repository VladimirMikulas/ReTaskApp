package com.vlamik.retask.common.utils

import android.content.Context
import com.vlamik.core.commons.AppText
import com.vlamik.core.commons.AppText.DynamicString
import com.vlamik.core.commons.AppText.StringResource

/**
 * Resolves the AppText to an actual String using the provided Context.
 * @param context The Context needed to access String resources.
 * @return The resolved String.
 */
fun AppText.asString(context: Context): String = when (this) {
    is DynamicString -> value

    is StringResource ->
        if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args.toTypedArray())
        }
}