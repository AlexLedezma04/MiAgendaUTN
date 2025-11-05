// Paquete responsable de la base de datos para la app MiAgendaUTN
package org.agenda.utn.database.composeApp

// Importaciones de dependencias para manejo e implementación de base de datos con SQLDelight
import app.cash.sqldelight.TransacterImpl // Implementación base para operaciones/consultas transaccionales
import app.cash.sqldelight.db.AfterVersion // Interfaz para manejar migraciones después de cierto versionado
import app.cash.sqldelight.db.QueryResult // Resultado devuelto por consultas SQL ejecutadas
import app.cash.sqldelight.db.SqlDriver // Driver genérico para ejecutar instrucciones SQL en diferentes plataformas
import app.cash.sqldelight.db.SqlSchema // Interfaz para manejar esquemas de bases de datos en SQLDelight
import kotlin.Long // Tipo de dato para claves primarias (id) de tareas o versionado
import kotlin.Unit // Tipo que indica ausencia de valor; similar a void en Java
import kotlin.reflect.KClass // Clase para referenciar tipos en tiempo de ejecución
import org.agenda.utn.database.AgendaDatabase // Interfaz generada para acceso seguro y tipado a la base de datos
import orgagendautndatabase.TaskQueries // Clase de consultas generadas para la tabla de tareas (Tasks)

// Extensión de KClass para obtener el esquema de la base de datos AgendaDatabase
internal val KClass<AgendaDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
    get() = AgendaDatabaseImpl.Schema

// Extensión de KClass para crear una nueva instancia de AgendaDatabase usando un driver SQL proporcionado
internal fun KClass<AgendaDatabase>.newInstance(driver: SqlDriver): AgendaDatabase =
    AgendaDatabaseImpl(driver)

// Implementación privada de la interfaz AgendaDatabase que define el acceso real a la base de datos y sus queries
private class AgendaDatabaseImpl(
    driver: SqlDriver, // Driver a usar (SQLite Android, Native, JVM, etc)
) : TransacterImpl(driver), // Hereda de implementación de transacciones SQLDelight
    AgendaDatabase { // Implementa la interfaz de base generada
    // Instancia de TaskQueries para consultar/modificar la tabla de tareas
    override val taskQueries: TaskQueries = TaskQueries(driver)

    // Objeto Schema para proveer el esquema y control de versionado/migraciones de la base de datos
    public object Schema : SqlSchema<QueryResult.Value<Unit>> {
        override val version: Long
            get() = 1 // Version del esquema de la base de datos

        // Función para crear la base de datos y su/s tabla/s
        override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
            driver.execute(null, """
          |CREATE TABLE Tasks (
          |    id INTEGER PRIMARY KEY AUTOINCREMENT,
          |    title TEXT NOT NULL,
          |    description TEXT NOT NULL,
          |    dueDate INTEGER NOT NULL,
          |    category TEXT NOT NULL,
          |    isCompleted INTEGER NOT NULL DEFAULT 0,
          |    createdAt INTEGER NOT NULL,
          |    updatedAt INTEGER NOT NULL
          |)
          """.trimMargin(), 0) // Sentencia SQL para crear la tabla Tasks con columnas y restricciones
            return QueryResult.Unit // Retorna Unit indicando fin del proceso de creación
        }

        // Función para manejar migraciones si el esquema de base de datos cambia de versión
        override fun migrate(
            driver: SqlDriver,
            oldVersion: Long,
            newVersion: Long,
            vararg callbacks: AfterVersion,
        ): QueryResult.Value<Unit> = QueryResult.Unit // No se realiza lógica de migración por ahora, solo retorna Unit
    }
}
