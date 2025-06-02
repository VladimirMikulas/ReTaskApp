package com.vlamik.retask.navigation

sealed class NavRoutes(internal open val path: String) {

    data object TaskList : NavRoutes("task_list/")
    data object TaskDetails : NavRoutes("task_details/{$DETAILS_ID_KEY}") {
        fun build(id: String): String =
            path.replace("{$DETAILS_ID_KEY}", id)
    }


    companion object {
        const val DETAILS_ID_KEY: String = "id"
    }
}
