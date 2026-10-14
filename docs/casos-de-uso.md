# Casos de uso

Actores: deportista, entrenador autorizado, servicio IA, integración externa y administrador. La versión 0.1 opera como espacio individual; los actores colaborativos forman parte del diseño posterior.

| ID | Caso | Flujo y resultado | Alternativas |
|---|---|---|---|
| CU01 | Gestionar ejercicios | Buscar, crear o editar nombre, patrón, músculos, material y convención; guardar identidad estable | Compartir, duplicar e historial de clasificación pendientes |
| CU02 | Configurar equilibrio | Definir categorías y objetivos; revisar distribución | 0.1 muestra distribución realizada por patrón; objetivos pendientes |
| CU03 | Crear programa | Añadir sesiones, ejercicios, series y prescripciones; guardar | Edición no modifica snapshots de sesiones existentes |
| CU04 | Cargas y progresiones | Elegir regla y referencia, paso y progresión; validar | Rechazar ciclos y referencias inválidas; condiciones futuras pendientes |
| CU05 | Calendario | Elegir programa, sesión, vuelta y fecha | La semana es independiente de la vuelta; automatismos pendientes |
| CU06 | Registrar sesión | Abrir sesión, registrar kg, reps y RPE por serie, cerrar con estado y notas | RPE opcional; 0 repeticiones admitido para fallo |
| CU07 | Adaptar sesión | Cambiar carga real y recalcular propuestas pendientes | Añadir, quitar, reordenar o sustituir desde web pendiente |
| CU08 | Consultar progreso | Revisar series, repeticiones, RPE y distribución | Tendencias, filtros e indicadores comparables pendientes |
| CU09 | Actualizar programación | Editar plan para próximas sesiones | No altera sesiones ya materializadas; versionado completo pendiente |
| CU10 | Acceso entrenador | Vincular, conceder y revocar permisos | Pendiente |
| CU11 | Solicitar IA | Autorizar datos, analizar, revisar y aplicar propuesta | Pendiente; sin proveedor conectado |
| CU12 | Importar y exportar | Exportar registros propios en JSON | ODS/XLSX e importación con revisión pendientes |
| CU13 | Integración API | Autenticar, obtener CSRF y operar con permisos | 401/403/404/409 explícitos; idempotencia POST pendiente |
| CU14 | Corregir historial | Abrir sesión, corregir serie, recalcular indicadores | Control optimista; registro de versiones anteriores pendiente |

Precondición común: autenticación para datos y operaciones. Los ejemplos del catálogo no incluyen entrenamientos reales. La fase siguiente desarrollará escenarios y pruebas de aceptación para las funciones pendientes sin limitar los métodos del entrenador.
