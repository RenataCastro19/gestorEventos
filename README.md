# 📋 Gestor de Eventos — Caruma Barras

<p align="center">
  <strong>Aplicación Android nativa para la gestión integral de eventos, desarrollada con Kotlin y Jetpack Compose.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Firebase-Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/Min_SDK-24-brightgreen?style=for-the-badge&logo=android&logoColor=white" alt="Min SDK" />
</p>

---

## 📖 Descripción

**Gestor de Eventos** es una aplicación Android desarrollada para **Caruma Barras para Eventos**, una empresa de servicios de barras para eventos. La app permite gestionar de manera integral todos los aspectos operativos de la organización de eventos: desde el registro de empleados y clientes, hasta la asignación de mobiliario, servicios y la generación de documentos PDF profesionales (contratos de servicio y órdenes de trabajo).

La aplicación implementa un **sistema de roles** con tres niveles de acceso (Super Administrador, Administrador y Empleado), garantizando que cada usuario tenga acceso únicamente a las funcionalidades correspondientes a su perfil.

---

## ✨ Funcionalidades Principales

### 🔐 Autenticación y Roles
- **Login seguro** con ID de usuario y contraseña
- **Tres niveles de acceso:**
  - **Super Administrador** — Acceso total a todos los módulos de gestión
  - **Administrador** — Gestión de eventos, empleados, mobiliario y servicios
  - **Empleado** — Visualización de eventos asignados

### 📅 Gestión de Eventos
- Crear, visualizar y administrar eventos
- Asignación de fecha, hora de inicio/fin, dirección y número de personas
- Vinculación con clientes, empleados, servicios y mobiliario
- Detalle de servicios personalizados por evento

### 👥 Gestión de Empleados
- Registro de nuevos usuarios con generación automática de ID
- Visualización del listado completo de empleados
- Asignación/desasignación de empleados a eventos
- Funcionalidad de habilitar/inhabilitar empleados

### 🪑 Gestión de Mobiliario
- Catálogo de mobiliario con categorías
- Registro de mobiliario por categoría y color
- Creación de categorías personalizadas

### 🍹 Gestión de Servicios
- Catálogo de servicios (ej: Esquites, Micheladas, Aguas, Snacks)
- Registro con descripción, categorías de detalle y precio por persona

### 📄 Generación de PDFs
- **Contrato de Servicio (Cliente)** — Documento profesional con detalles del evento, servicio contratado, personal asignado, mobiliario y términos y condiciones
- **Orden de Trabajo (Empleados)** — Documento con información del evento, cliente, servicio, equipo de trabajo, mobiliario y checklist de preparación
- Branding corporativo de Caruma Barras (logo, colores dorado/negro)
- Compartir PDFs directamente desde la app

---

## 🏗️ Arquitectura

El proyecto sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)**, con una clara separación de responsabilidades:

```
┌─────────────────────────────────────────────┐
│                    VIEW                     │
│  (Composables - Jetpack Compose / Material3)│
├─────────────────────────────────────────────┤
│                 VIEWMODEL                   │
│   (StateFlow, lógica de negocio, estado)    │
├─────────────────────────────────────────────┤
│                REPOSITORY                   │
│      (Acceso a datos / Firebase CRUD)       │
├─────────────────────────────────────────────┤
│                   MODEL                     │
│          (Data classes Kotlin)              │
├─────────────────────────────────────────────┤
│              FIREBASE FIRESTORE             │
│          (Base de datos en la nube)          │
└─────────────────────────────────────────────┘
```

---

## 📂 Estructura del Proyecto

```
gestorEventos/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/gestoreventos/
│   │   │   ├── MainActivity.kt                    # Actividad principal con navegación
│   │   │   │
│   │   │   ├── model/                              # Modelos de datos
│   │   │   │   ├── CategoriaMobiliario.kt          # Categoría de mobiliario
│   │   │   │   ├── Cliente.kt                      # Cliente del evento
│   │   │   │   ├── Evento.kt                       # Evento con todos sus detalles
│   │   │   │   ├── Mobiliario.kt                   # Pieza de mobiliario
│   │   │   │   ├── Servicio.kt                     # Servicio ofrecido
│   │   │   │   └── Usuario.kt                      # Usuario con rol
│   │   │   │
│   │   │   ├── repository/                         # Capa de acceso a datos
│   │   │   │   ├── CategoriaMobiliarioRepository.kt
│   │   │   │   ├── ClienteRepository.kt
│   │   │   │   ├── EventoRepository.kt
│   │   │   │   ├── MobiliarioRepository.kt
│   │   │   │   ├── ServicioRepository.kt
│   │   │   │   └── UsuarioRepository.kt
│   │   │   │
│   │   │   ├── viewmodel/                          # ViewModels
│   │   │   │   ├── CategoriaMobiliarioViewModel.kt
│   │   │   │   ├── ClienteViewModel.kt
│   │   │   │   ├── EventoViewModel.kt
│   │   │   │   ├── MobiliarioViewModel.kt
│   │   │   │   ├── ServicioViewModel.kt
│   │   │   │   ├── SuperAdminViewModel.kt
│   │   │   │   └── UsuarioViewModel.kt
│   │   │   │
│   │   │   ├── view/                               # Pantallas (Composables)
│   │   │   │   ├── LoginScreen.kt                  # Inicio de sesión
│   │   │   │   ├── HomeScreenSuperAdmin.kt         # Dashboard Super Admin
│   │   │   │   ├── HomeScreenAdmin.kt              # Dashboard Admin
│   │   │   │   ├── HomeScreenEmpleado.kt           # Dashboard Empleado
│   │   │   │   ├── EventosListScreen.kt            # Listado de eventos
│   │   │   │   ├── EventosEmpleadoListScreen.kt    # Eventos del empleado
│   │   │   │   ├── AgregarEventoForm.kt            # Formulario de nuevo evento
│   │   │   │   ├── EmpleadosListScreen.kt          # Listado de empleados
│   │   │   │   ├── EmpleadosListScreenAdmin.kt     # Empleados (vista admin)
│   │   │   │   ├── RegistroUsuarioScreen.kt        # Registro de empleados
│   │   │   │   ├── MobiliarioListScreen.kt         # Listado de mobiliario
│   │   │   │   ├── AgregarMobiliarioForm.kt        # Formulario de mobiliario
│   │   │   │   ├── AgregarCategoriaMobiliarioForm.kt # Formulario de categoría
│   │   │   │   ├── ServiciosListScreen.kt          # Listado de servicios
│   │   │   │   ├── AgregarServicioForm.kt          # Formulario de servicio
│   │   │   │   └── CommonComponents.kt             # Componentes reutilizables
│   │   │   │
│   │   │   ├── ui/theme/                           # Tema visual
│   │   │   │   ├── Color.kt                        # Paleta de colores
│   │   │   │   ├── Theme.kt                        # Configuración del tema
│   │   │   │   └── Type.kt                         # Tipografía
│   │   │   │
│   │   │   └── utils/                              # Utilidades
│   │   │       └── PdfGenerator.kt                 # Generación de PDFs con iText
│   │   │
│   │   ├── res/                                    # Recursos Android
│   │   └── AndroidManifest.xml                     # Manifiesto de la app
│   │
│   ├── build.gradle.kts                            # Dependencias del módulo
│   └── google-services.json                        # Configuración Firebase
│
├── build.gradle.kts                                # Configuración raíz Gradle
├── settings.gradle.kts                             # Settings del proyecto
├── gradle.properties                               # Propiedades de Gradle
└── gradlew / gradlew.bat                           # Gradle Wrapper
```

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| **Lenguaje** | Kotlin |
| **UI Framework** | Jetpack Compose con Material 3 |
| **Arquitectura** | MVVM (Model-View-ViewModel) |
| **Navegación** | Navigation Compose |
| **Estado** | StateFlow + ViewModel Compose |
| **Base de Datos** | Firebase Cloud Firestore |
| **Analíticas** | Firebase Analytics |
| **Asincronía** | Kotlin Coroutines |
| **PDFs** | iText PDF Library |
| **Build System** | Gradle (Kotlin DSL) |
| **SDK mínimo** | API 24 (Android 7.0 Nougat) |
| **SDK objetivo** | API 35 |

---

## 🚀 Configuración del Proyecto

### Prerrequisitos

- **Android Studio** Hedgehog o superior
- **JDK 11** o superior
- **Cuenta de Firebase** con un proyecto configurado

### Pasos de Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/RenataCastro19/APLICACION_GestionEventos.git
   cd gestorEventos
   ```

2. **Configurar Firebase:**
   - Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
   - Habilitar **Cloud Firestore** como base de datos
   - Descargar el archivo `google-services.json` y colocarlo en `app/`

3. **Abrir en Android Studio:**
   - Seleccionar *File > Open* y elegir el directorio del proyecto
   - Esperar la sincronización de Gradle

4. **Compilar y ejecutar:**
   ```bash
   ./gradlew assembleDebug
   ```
   O bien, usar el botón ▶️ **Run** en Android Studio sobre un emulador o dispositivo físico.

---

## 🗃️ Modelos de Datos

### Evento
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | Identificador único |
| `fecha` | String | Fecha del evento |
| `horaInicio` | String | Hora de inicio |
| `horaFin` | String | Hora de finalización |
| `numeroPersonas` | Int | Capacidad del evento |
| `idCliente` | String | Referencia al cliente |
| `direccionEvento` | String | Dirección del evento |
| `listaIdsEmpleados` | List\<String\> | Empleados asignados |
| `idMobiliario` | String | Mobiliario asignado |
| `idServicio` | String | Servicio contratado |
| `detalleServicio` | String | Detalles adicionales |

### Usuario
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | ID único (generado automáticamente) |
| `nombre` | String | Nombre |
| `apellidoPaterno` | String | Apellido paterno |
| `apellidoMaterno` | String | Apellido materno |
| `telefono` | String | Teléfono de contacto |
| `contrasena` | String | Contraseña de acceso |
| `rol` | String | `super_admin`, `admin` o `empleado` |

### Servicio
| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | Identificador único |
| `nombre` | String | Nombre del servicio |
| `descripcion` | String | Descripción detallada |
| `categoriasDetalle` | List\<String\> | Categorías de detalle |
| `precioPorPersona` | Double | Precio por persona |


---

## 🎨 Branding

La aplicación utiliza la identidad visual de **Caruma Barras para Eventos**:

- **Dorado** `#D4AF37` — Color principal de marca
- **Negro** `#000000` — Color de contraste
- **Blanco** `#FFFFFF` — Fondo y textos claros

Estos colores se aplican de forma consistente en la UI y en los documentos PDF generados.

---

## 📄 Licencia

Este proyecto es de uso privado para **Caruma Barras para Eventos**.

---
