# API v1

Base `/api/v1`. JSON. HTTP Basic sobre HTTPS en despliegues remotos. No almacenar credenciales en aplicaciones públicas. Para mutaciones, obtener primero `/csrf`, conservar la cookie de sesión y enviar el token en la cabecera devuelta. La web mantiene las credenciales únicamente en memoria.

| Método | Ruta | Función |
|---|---|---|
| GET | /csrf | Token, cabecera y usuario autenticado |
| GET | /entries | Registros del usuario |
| POST | /entries | Crear ejercicio, programa o sesión |
| PUT | /entries/{id} | Actualizar con versión esperada |
| POST | /sessions | Crear sesión desde programa |
| GET | /sessions/{id}/loads | Cargas pendientes calculadas |
| GET | /metrics | Recuento de series y repeticiones, RPE y patrones |
| GET | /export | Exportación JSON con versión de esquema |

El cuerpo de entries es `{ "kind": "exercise|program|session", "data": {...}, "version": 0 }`. La versión solo es obligatoria al editar. Cada respuesta incluye `id`, `kind`, `version` y `data`. Los contratos tipados están en `Model.java`. Para programar: `{ "programId": "UUID", "dayIndex": 0, "round": 1, "date": "2026-10-14" }`.

Reglas de carga: FIXED, RM_PERCENT, SET_PERCENT y SET_OFFSET. `reference` es el índice de serie empezando en cero; la interfaz lo muestra empezando en uno. `value` es kg, porcentaje o diferencia según modo; `referenceMax` permite un 1RM específico por serie, con fallback al del programa; `increment` es el paso de redondeo HALF_UP. Las referencias circulares, ausentes y entre ejercicios distintos se rechazan.

Las subidas de vuelta y periodo semanal se suman. La subida afecta a cargas fijas y a la referencia de 1RM; los porcentajes entre series se recalculan después. La prescripción se materializa al crear la sesión. Una actualización posterior del programa no mueve esas cargas.

Códigos: 400 datos inválidos; 401 sin autenticación; 403 CSRF/permisos; 404 registro no accesible; 409 versión en conflicto. La API usa PUT de agregados; la versión impide aplicar dos actualizaciones del mismo estado. POST de creación todavía no tiene claves de idempotencia.

`externalVolume` es una suma sin normalizar según convenciones de carga; no sirve para comparar ejercicios o materiales distintos. La interfaz no lo muestra. Los patrones se consultan desde el catálogo actual, sin snapshot histórico en esta versión.
