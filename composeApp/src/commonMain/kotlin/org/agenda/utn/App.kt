// Paquete principal de la aplicación MiAgendaUTN
package org.agenda.utn

// Importaciones necesarias para componentes y utilidades de Jetpack Compose, Material y recursos
import androidx.compose.animation.AnimatedVisibility // Componente para mostrar/ocultar contenido de forma animada
import androidx.compose.foundation.Image // Componente para mostrar imágenes en la interfaz
import androidx.compose.foundation.background // Utilidad para establecer color de fondo
import androidx.compose.foundation.layout.Column // Componente para organizar elementos en columna vertical
import androidx.compose.foundation.layout.fillMaxSize // Modificador para ocupar todo el espacio disponible
import androidx.compose.foundation.layout.fillMaxWidth // Modificador para ocupar todo el ancho disponible
import androidx.compose.foundation.layout.safeContentPadding // Agrega relleno para áreas seguras (evita notch u overlays)
import androidx.compose.material3.Button // Botón de Material Design 3
import androidx.compose.material3.MaterialTheme // Tema visual para la UI de Material Design
import androidx.compose.material3.Text // Componente para mostrar texto
import androidx.compose.runtime.* // Herramientas para manejar estados y recomposición
import androidx.compose.ui.Alignment // Utilidad para alineación en la UI
import androidx.compose.ui.Modifier // Permite personalizar el comportamiento/apariencia de los componentes
import org.jetbrains.compose.resources.painterResource // Utilidad para acceder a recursos de imágenes
import miagendautn.composeapp.generated.resources.Res // Objeto de acceso a los recursos generados por Compose
import miagendautn.composeapp.generated.resources.compose_multiplatform // Recurso específico de imagen para multiplataforma
import org.agenda.utn.data.local.DatabaseDriverFactory // Fábrica para acceder al driver de la base de datos

// Función Composable esperada multiplataforma para recordar y obtener la instancia de la fábrica de base de datos
@Composable
expect fun rememberDatabaseDriverFactory(): DatabaseDriverFactory // NO QUITAR, uniforme en todas las plataformas

// Función principal de la app, define la interfaz de usuario utilizando MaterialTheme 
@Composable
fun App() {
    MaterialTheme {
        // Variable de estado local para controlar la visibilidad del contenido animado
        var showContent by remember { mutableStateOf(false) }

        // Layout principal en columna, centra el contenido y aplica color de fondo usando el esquema de Material
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer) // Fondo con el color primario del tema
                .safeContentPadding() // Relleno para evitar detalles del hardware
                .fillMaxSize(), // Ocupa toda la pantalla
            horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente los hijos
        ) {
            // Botón que alterna la visibilidad del contenido secundario
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!") // Etiqueta del botón
            }

            // Animación de visibilidad: muestra contenido extra al hacer click en el botón
            AnimatedVisibility(showContent) {
                // Genera saludo usando la clase Greeting (debe existir en tu proyecto)
                val greeting = remember { Greeting().greet() }

                // Columna para agrupar imagen y saludo, ocupando todo el ancho 
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Muestra imagen de recurso multiplataforma
                    Image(painterResource(Res.drawable.compose_multiplatform), null)

                    // Muestra el saludo concatenado, evidencia clara de funcionalidad dinámica
                    Text("Compose: $greeting")
                }
            }
        }
    }
}
