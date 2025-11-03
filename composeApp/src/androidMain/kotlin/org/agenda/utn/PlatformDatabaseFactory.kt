package org.agenda.utn

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.agenda.utn.data.local.DatabaseDriverFactory

@Composable
actual fun rememberDatabaseDriverFactory(): DatabaseDriverFactory {
    val context = LocalContext.current

    if (org.agenda.utn.data.local.appContext == null) {
        org.agenda.utn.data.local.appContext = context.applicationContext
    }

    return DatabaseDriverFactory()
}
