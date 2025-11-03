package org.agenda.utn

import androidx.compose.runtime.Composable
import org.agenda.utn.data.local.DatabaseDriverFactory

@Composable
actual fun rememberDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}
