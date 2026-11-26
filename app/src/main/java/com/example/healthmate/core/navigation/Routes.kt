package com.example.healthmate.core.navigation

import com.example.healthmate.feature.editor.EditorArgs
import java.time.LocalDate


object Routes {
    const val DASHBOARD = "dashboard"
    const val HISTORY = "history"
    const val PROFILE = "profile"

    private const val EDITOR_BASE = "editor"
    const val EDITOR_PATTERN =
        "$EDITOR_BASE?${EditorArgs.RECORD_ID}={${EditorArgs.RECORD_ID}}&${EditorArgs.DATE}={${EditorArgs.DATE}}"

    fun editor(recordId: Long? = null, date: LocalDate? = null): String {
        val params = buildList {
            if (recordId != null) add("${EditorArgs.RECORD_ID}=$recordId")
            if (date != null) add("${EditorArgs.DATE}=$date")
        }
        return if (params.isEmpty()) EDITOR_BASE else "$EDITOR_BASE?${params.joinToString("&")}"
    }
}