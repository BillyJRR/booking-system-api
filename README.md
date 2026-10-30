# 🚀 Backend - Sistema de Gestión de Citas (Appointment Booking System)

API RESTful desarrollada con **Spring Boot** para gestionar usuarios, roles, citas médicas o de servicios, autenticación JWT, notificaciones asíncronas basadas en eventos y persistencia en base de datos relacional.

---

## 🛠️ Tecnologías Utilizadas

* **Framework Principal:** Spring Boot
* **Seguridad:** Spring Security con JSON Web Token (JWT)
* **Persistencia:** Spring Data JPA
* **Base de Datos:** PostgreSQL (alojado en Supabase)
* **Mensajería Asíncrona:** RabbitMQ (gestionado mediante CloudAMQP) para colas de correos
* **Notificaciones:** Resend (integrado para el envío de correos electrónicos)
* **Contenedorización:** Docker & Docker Compose

---

## 📋 Prerrequisitos para Ejecutar Localmente

Asegúrate de tener instalado en tu máquina:
* [Java 17 o superior](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* [Docker y Docker Compose](https://www.docker.com/) (opcional, si prefieres correrlo en contenedores)

---

## ⚙️ Configuración de Variables de Entorno

Crea un archivo `.env` o configura en tu entorno de trabajo las siguientes variables necesarias para que la aplicación funcione correctamente:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://<tu-host-supabase>:5432/<tu-base-de-datos>
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_contraseña
JWT_SECRET=tu_clave_secreta_jwt
CLOUDAMQP_URL=amqps://tu_usuario:tu_password@tu_[host.rmq.cloudamqp.com/tu_vhost](https://host.rmq.cloudamqp.com/tu_vhost)
RESEND_API_KEY=re_tu_api_key_de_resend