# 🛒 E-Commerce Microservices Application  

## 📌 Description  
Ce projet est une application e-commerce basée sur une architecture **microservices**. Il permet aux utilisateurs de parcourir les produits, passer des commandes en ligne et gérer leurs achats de manière efficace. Chaque microservice est conçu pour gérer un aspect spécifique du système, garantissant **scalabilité, modularité et performance**.

---

## 🏗️ Architecture Globale  

L'application est composée des microservices suivants :  

1️⃣ **API Gateway** - Gestion centralisée des requêtes et sécurisation des accès  
2️⃣ **User Management** - Gestion des utilisateurs et authentification  
3️⃣ **Product Service** - Gestion du catalogue des produits  
4️⃣ **Order Service** - Gestion des commandes  
5️⃣ **Payment Service** - Traitement des paiements  
6️⃣ **Notification Service** - Envoi des notifications via **Twilio**  
7️⃣ **Discovery Service** - Service d'enregistrement des microservices avec **Eureka**  
8️⃣ **Configuration Service** - Gestion centralisée des configurations avec **Spring Cloud Config**  

---

## 🔄 Communication entre Microservices  

✅ **RESTful API** → Communication synchrone (ex: récupération des produits)  
✅ **Event-Driven Architecture** → Communication asynchrone via **RabbitMQ / Kafka** (ex: notification de commande)  

### 🔗 Interaction entre les microservices  

1️⃣ **Création de commande** → L’utilisateur passe une commande → Un événement "Commande créée" est publié → Notification du service de paiement  
2️⃣ **Paiement effectué** → Une fois validé, un événement "Paiement effectué" est publié → Mise à jour du statut de commande → Notification à l’utilisateur  
3️⃣ **Expédition de la commande** → Un événement "Commande expédiée" est généré → L’utilisateur est informé  

---

## 🛠️ Stack Technique  

| **Technologie**          | **Utilisation** |
|-------------------------|----------------|
| **Spring Cloud Gateway** | API Gateway |
| **Keycloak (OAuth2.0)** | Authentication & Authorization |
| **WebClient / OpenFeign** | Communication interne entre services |
| **Spring Cloud Config** | Centralisation des configurations |
| **Vault** | Gestion des secrets |
| **Eureka** | Service Discovery |
| **RabbitMQ / Kafka** | Événements asynchrones |
| **Resilience4j** | Circuit Breaker |
| **Zipkin & Sleuth** | Tracing distribué |
| **ELK (Elasticsearch, Logstash, Kibana)** | Centralisation des logs |
| **JUnit & Mockito** | Tests unitaires |
| **Testcontainers** | Tests d'intégration |
| **Jenkins & GitHub Actions** | CI/CD |
| **Docker & Kubernetes** | Déploiement |

---

## 🚀 Installation & Exécution  

### 📌 Prérequis  
- **Java 17+**
- **Maven 3+**
- **Docker & Kubernetes**
- **Keycloak**
- **RabbitMQ / Kafka**
- **Eureka & Config Server**


