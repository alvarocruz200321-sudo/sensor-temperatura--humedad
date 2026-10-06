# 🌡️ Sensor Temp-Hum App

Aplicación móvil desarrollada para Android con **Kotlin** y **Jetpack Compose**, diseñada para el monitoreo en tiempo real de datos de temperatura y humedad registrados a través de sensores y sincronizados mediante **Firebase Realtime Database**.

---

## 🚀 Características Principales

* **Autenticación de Usuarios:** Registro e inicio de sesión seguro integrado con **Firebase Authentication** y soporte para credenciales de Google.
* **Monitoreo en Tiempo Real:** Visualización instantánea de las lecturas de humedad y temperatura obtenidas de la base de datos **Firebase Realtime Database**.
* **Interfaz Moderna y Reactiva:** Diseñada desde cero con **Jetpack Compose** y componentes de **Material Design 3**.
* **Compatibilidad AndroidX:** Configuración actualizada para dispositivos Android modernos (Compile SDK 35).

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
* **Backend & BD:** [Firebase Realtime Database](https://firebase.google.com/docs/database) & [Firebase Auth](https://firebase.google.com/docs/auth)
* **Gestor de Dependencias:** Gradle con Version Catalogs (`libs.versions.toml`)
* **Mínimo SDK:** Android 8.0 (API 24)
* **SDK Objetivo:** Android 15 (API 35)

---

## 📋 Requisitos Previos

Antes de ejecutar o compilar el proyecto, asegúrate de contar con:

1. **Android Studio** (versión Ladybug o superior).
2. **JDK 11** o superior configurado en el entorno de desarrollo.
3. Un proyecto en la consola de [Firebase Console](https://console.firebase.google.com/).

---

## ⚙️ Configuración del Proyecto

Sigue estos pasos para ejecutar la aplicación localmente:

### 1. Clonar el repositorio
```bash
git clone [https://github.com/TU_USUARIO/sensor-temperatura--humedad.git](https://github.com/TU_USUARIO/sensor-temperatura--humedad.git)
cd sensor-temperatura--humedad
