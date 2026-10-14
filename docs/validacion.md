# Validación de la versión 0.1.0

Comprobaciones realizadas en el entorno de desarrollo:

- `mvn verify`: compilación y empaquetado correctos con Java 17 y Spring Boot 3.5.16.
- 12 pruebas automatizadas: 7 de reglas de carga y 5 de API, sin fallos ni errores.
- Cálculos: back-off del 90 % sobre top set realizado, redondeo, 1RM por serie, progresión, diferencia negativa de kilos, preservación de series registradas y rechazo de ciclos y duplicados.
- API: autenticación real, CSRF, catálogo inicial, aislamiento por propietario, conflicto de versión y flujo programa → sesión → registro → recálculo → métricas.
- JAR ejecutado por HTTP: inicio, autenticación, CSRF y creación; persistencia H2 comprobada después de detener y reiniciar; recursos estáticos servidos correctamente.
- `node --check src/main/resources/static/app.js`: sintaxis correcta.

Limitaciones de verificación: la prueba automatizada en navegador no se pudo ejecutar por falta del ejecutable de Chromium y fallo de su descarga. No se afirma validación visual ni pruebas completas de interacción. Docker/PostgreSQL y AWS no se ejecutaron en este entorno. GitHub Actions repite las pruebas de Maven al recibir cambios.
