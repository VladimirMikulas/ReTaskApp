package com.vlamik.core.commons


/**
 * Sealed class for representing text that can be a dynamic String or a String resource.
 * Allows deferring the resolution of String resources to the UI layer, making the ViewModel independent of Context.
 */
sealed class AppText {

    /**
     * Represents a dynamic String.
     * @param value The actual String value.
     */
    data class DynamicString(val value: String) : AppText()

    /**
     * Represents a String resource.
     * @param resId The String resource ID.
     * @param args Arguments for formatting the String resource.
     */
    data class StringResource(
        val resId: Int,
        val args: List<Any> = emptyList()
    ) : AppText()

    companion object {
        /**
         * Creates a DynamicString instance.
         * @param value The actual String value.
         * @return A AppText.DynamicString instance.
         */
        fun dynamic(value: String): AppText =
            DynamicString(value)

        /**
         * Creates a StringResource instance.
         * @param resId The String resource ID.
         * @param args Arguments for formatting the String resource.
         * @return A AppText.StringResource instance.
         */
        fun from(resId: Int, vararg args: Any): AppText =
            StringResource(resId, args.toList())
    }
}

