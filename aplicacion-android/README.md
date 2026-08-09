# Mesa TI Android

Módulo móvil inicial del sistema Mesa TI desarrollado con Kotlin y Jetpack Compose.

## Funcionalidades

- Mostrar resumen de tickets.
- Buscar por título o categoría.
- Mostrar prioridad y estado.
- Consultar tickets desde `http://10.0.2.2:8081` en el emulador.
- Crear, editar y eliminar tickets mediante la API REST.
- Mostrar estados de carga, error y reintento.

## Requisitos

- Android Studio.
- Android SDK 35.
- Java 17 o superior.

## Compilación

```bash
./gradlew clean test assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.
