# Preparación de despliegue AWS

La entrega incluye un JAR ejecutable y Docker Compose con PostgreSQL persistente. No se han creado recursos AWS ni desplegado la aplicación.

Para una primera instalación en EC2: instalar Docker y Compose, clonar el repositorio, definir las variables del archivo `.env` con permisos restrictivos y ejecutar `docker compose up --build -d`. Publicar mediante un proxy HTTPS hacia 127.0.0.1:8080. No exponer PostgreSQL ni publicar el puerto 8080 en Internet. Configurar dominio, certificado y grupo de seguridad para HTTPS; restringir administración SSH.

Para un servicio gestionado, el contenedor puede prepararse para ECS con PostgreSQL RDS, secretos gestionados y TLS en un balanceador. Esa infraestructura se diseñará por separado. No utilizar el H2 local dentro de un contenedor efímero como almacenamiento de producción.

Antes de dar acceso a más personas: sustituir el usuario único por cuentas y permisos completos, incorporar migraciones de esquema y auditoría, comprobar recuperación de backups y acordar protección y conservación de datos. `ddl-auto=update` es solo una facilidad de esta fase inicial.

No hay claves AWS ni credenciales en este repositorio.
