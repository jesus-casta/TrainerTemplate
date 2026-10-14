# TrainerTemplate

Versión 0.1.0 de una plataforma de programación y seguimiento del entrenamiento. Spring Boot 3.5.16, Java 17+, API REST y web adaptable a móvil servida por el propio backend. Sin compilación frontend.

## Ejecutar

Necesitas Java 17 o superior y Maven 3.9+. Define `TRAINER_PASSWORD` en tu entorno (obligatorio); el usuario predeterminado es `trainer`, configurable con `TRAINER_USERNAME`.

```sh
mvn verify
mvn spring-boot:run
```

Abre http://localhost:8080. Se crean 12 ejercicios iniciales editables; no se incluyen entrenamientos ni datos personales. Los registros persisten por defecto en `./data/trainer` mediante H2. Conserva y respalda ese directorio con la aplicación detenida.

Para PostgreSQL: configura `DB_URL`, `DB_USER` y `DB_PASSWORD`. Para Docker, copia `.env.example` a `.env`, rellena ambas contraseñas y ejecuta `docker compose up --build`. El puerto de Compose está limitado al equipo local.

## Flujo disponible

1. Crear o editar ejercicios con patrón propio, músculos, material y convención de carga.
2. Crear programas con sesiones y series. Cargas fijas, porcentaje de 1RM y porcentaje o diferencia respecto a otra serie del mismo ejercicio.
3. Configurar redondeo por serie y subida global por vuelta o por periodos de siete días desde el inicio.
4. Programar una sesión indicando fecha, día del programa y vuelta por separado.
5. Registrar peso, repeticiones y RPE por serie. Los porcentajes pendientes utilizan el peso realmente registrado en la serie de referencia.
6. Consultar recuentos y distribución del trabajo, y exportar los registros a JSON.

## Alcance real de la versión

Esta entrega es una base funcional de uso individual, no una implementación completa de todos los requisitos. Hay un usuario configurado por instalación; no hay alta de usuarios, vinculación de entrenadores ni proveedor IA conectado. La API aplica propiedad de registros, autenticación Basic y CSRF. El acceso remoto requiere HTTPS.

La referencia de 1RM y las subidas de esta primera interfaz son globales por programa; las reglas por ejercicio, progresiones condicionales, fases, rangos de repeticiones, duraciones, superseries, sustituciones durante la sesión, calendario mensual, filtros temporales, importación ODS/XLSX y auditoría completa están pendientes. No se presenta el índice stress de la hoja como una medida validada de fatiga.

Las sesiones guardan una copia de la prescripción al programarlas. Las ediciones del programa no alteran esas copias. Los cambios de registros usan control de versión optimista; todavía no existe un registro completo de versiones anteriores. El RPE histórico por bloque no se convierte en RPE por serie.

## Documentación

- [Requisitos y alcance](docs/requisitos.md)
- [Casos de uso](docs/casos-de-uso.md)
- [API](docs/api.md)
- [Despliegue AWS](docs/aws.md)
- [Validación](docs/validacion.md)

## Desarrollo

`mvn verify` ejecuta las pruebas de carga, autenticación, permisos y persistencia HTTP. GitHub Actions repite la verificación y genera un JAR. La compilación requiere descargar dependencias de Maven Central.

Referencia técnica: https://docs.spring.io/spring-boot/3.5/system-requirements.html
