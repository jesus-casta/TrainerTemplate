# Análisis y requisitos

Base: análisis de EntrenoSanson.ods y decisiones acordadas el 14 de octubre de 2026. No se publica la hoja ni datos personales. Una vuelta recorre las sesiones del programa y no tiene por qué coincidir con una semana. El RPE nuevo se registra por serie. El entrenador debe poder utilizar cargas manuales, 1RM, top sets, back-offs y relaciones entre series.

| ID | Requisito | Estado en 0.1 |
|---|---|---|
| RF01 | Programación libre con sesiones, fases, bloques y series | Sesiones y series; fases y bloques explícitos pendientes |
| RF02 | Prescripción y ejecución por serie | Peso, repeticiones fijas y RPE; tiempo y rangos pendientes |
| RF03 | Carga manual y referencias | Implementado; 1RM específico por serie y predeterminado por programa |
| RF04 | Relaciones entre series y validación de dependencias | Porcentaje y diferencia, con detección de ciclos |
| RF05 | Progresión por semana, vuelta, sesión y condiciones | Subida global por vuelta o periodo de siete días; condiciones pendientes |
| RF06 | Recalcular, redondear y ajustar | Implementado en series pendientes |
| RF07 | Registro y adaptación durante la sesión | Registro, corrección, notas y estados; añadir y sustituir pendientes |
| RF08 | Calendario independiente de las vueltas | Fechas y listado; vista mensual y avance automático pendientes |
| RF09 | Planificación e historial diferenciados | Snapshot de sesión y control optimista; auditoría completa pendiente |
| RF10 | Catálogo común y propio | 12 ejercicios iniciales y creación; compartir pendiente |
| RF11 | Clasificación configurable | Patrón libre y músculos; múltiples taxonomías pendientes |
| RF12 | Variantes y material | Material, convención y notas; variantes estructuradas pendientes |
| RF13 | Equilibrio del programa | Distribución realizada por patrón; objetivos y ponderación pendientes |
| RF14 | Métricas de carga y progreso | Series, repeticiones y RPE global; tendencias y filtros pendientes |
| RF15 | Importación y exportación Excel y ODS | Exportación JSON; hojas de cálculo pendientes |
| RF16 | Colaboración entrenador e IA | Pendiente; no se simulan respuestas de IA |
| RF17 | API autenticada y versionada | Implementada para operaciones iniciales |

Requisitos no funcionales: web adaptable, autenticación, CSRF, propiedad de registros, persistencia H2/PostgreSQL, bloqueo optimista, Docker y CI. Rendimiento, funcionamiento sin conexión, copias automatizadas, cuentas múltiples y auditoría requieren desarrollo adicional.

Criterios principales: un top set real de 102,5 produce un back-off al 90 % de 92,5 con paso 2,5; registrar un back-off lo conserva frente a cambios posteriores; un programa de tres sesiones admite cuatro fechas de entrenamiento en la semana; no hay acceso cruzado a registros; las ediciones desactualizadas devuelven conflicto.

Pendientes de definición: autonomía IA, permisos entrenador, semántica de omisiones y cancelaciones, ponderación muscular, formato de importación, catálogo ampliado y métricas de estrés. La hoja contenía errores y referencias inconsistentes; su migración exige revisión humana.
