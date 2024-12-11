# E-commerce Spring Boot Application with Hazelcast Caching and RabbitMQ Integration

## Overview

This is an e-commerce application that allows users to create orders for products and accepts payments for the order. It is built with Spring Boot and MYSQL with Hazelcast integration for caching orders and RabbitMQ for sending messages for payments received. The application provides API endpoints for managing orders, users, products and payments with data validation and exception handling.

### Entities
1. **Product:** Products are ordered by users
2. **User:** User orders products and make payments for orders
3. **CustomerOrder:** A list of order items by a user with products,product quantity and order amount
4. **OrderProduct:** A join table for order, product and quantity.
5. **Payment:** A payment made by a user against a order that may fail or succeed.
6. **Transaction:** A transaction message is generated on successful payment and stored in transaction table.


### Features

1. **Hazelcast Caching:** Improves application performance by reducing database calls.
2. **RabbitMQ Integration:** Enables asynchronous message processing.
3. **Order Management:** Includes creating, retrieving, and managing orders.
4. **Product and User Management:** Provides APIs to manage products and users.
5. **Payments:** Provides API for receiving payments and sends valid payments via rabbitmq messages
6. **SwaggerUI:** Swagger UI integrated for APIs
7. **Docker:** Docker file for generating docker images
8. **Unit Tests:** Unit tests written with jUnit and Mockito for Payment Service
9. **Exception Handling:** Global exception handling using Aspect.
10. **Migration Scripts:** Sql scripts for entities
11. **Data Validation:** Validation using annotations like @Positive,@NotNull

---

## Prerequisites

- **Java:** JDK 17 or above
- **Maven:** 3.8.0 or above
- **RabbitMQ:** Install as per below instructions
- **Hazelcast:** Add hazelcast maven dependencies as below
- **Database:** MySQL 8

---

## Setup Instructions

### Step 1: Clone the Repository

```bash
git clone https://github.com/zavedhussain/neoleap-demo.git
```

### Step 2: Configure Application Properties

Update the `application.yml` file with the following details:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password

```

### Step 3: Install Dependencies

Run the following Maven command to install the dependencies:

```bash
mvn clean install
```

### Step 4: Start RabbitMQ

Ensure RabbitMQ is installed and running.Installation instructions below. You can start RabbitMQ using:

```bash
rabbitmq-server
```


Access the RabbitMQ management console at `http://localhost:15672` (default credentials: guest/guest).

### Step 5: Run the Application

Run the Spring Boot application using:

```bash
mvn spring-boot:run
```

### Step 6: Access the APIs

The application will start on `http://localhost:8000`. Use tools like Postman to test the APIs or via [SwaggerUI](http://localhost:8000/swagger-ui/index.html).

---

### Docker:

Build Docker image

```bash
docker build -t ecommerce-demo .
```

Run docker image
```bash
docker run -p 8000:8000 ecommerce-demo
```

## Usage

### API Endpoints

#### Orders

- **Create Order**
    - **POST** `/orders`
    - Request Body: `{
      "userId": 1,
      "products":[
      {
      "productId":1,
      "quantity":2
      },
      {
      "productId":3,
      "quantity":2
      }
      ]
      }`
- **Get All Orders**
    - **GET** `/orders`
- **Get Order by ID**
    - **GET** `/orders/{id}`
- **Update Order by ID**
  - **PUT** `/orders/{id}`
  - Request Body: `{
    "userId": 1,
    "products":[
    {
    "productId":1,
    "quantity":2
    },
    {
    "productId":3,
    "quantity":2
    }
    ]
    }`
- **Delete Order by ID**
    - **DELETE** `/orders/{id}`

#### Products

- **Create Product**
    - **POST** `/products`
    - Request Body: `{ "name": "Product Name", "price": 100.00 }`

#### Users

- **Create User**
    - **POST** `/users`
    - Request Body: `{
      "userName":"rahim",
      "email":"rahimxyz@gmail.com",
      "phone":9809898908
      }`

#### Payments

- **Create Payment**
    - **POST** `/payments`
    - Request Body: `{
      "orderId":1,
      "userId":2,
      "amount":"175"
      }`
- **Get All Payments**
    - **GET** `/payments`
- **Get Payment by ID**
    - **GET** `/payments/{id}`

---

## Hazelcast Setup

To run Hazelcast you need to install hazelcast and spring-boot-starter-cache.

### Hazelcast maven dependency

```xml
<dependency>
    <groupId>com.hazelcast</groupId>
    <artifactId>hazelcast-spring</artifactId>
    <version>5.5.0</version>
</dependency>
```
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
   ```

Hazelcast is embedded in this application. The configuration is programmatically defined in the `CacheConfig` class.

### Key Hazelcast Settings:
- **Instance Name:** `hazelcast-instance`
- **Cache Map Name:** `orders-cache`
- **Cache Settings:**
    - **Time-to-Live:** 3600 seconds
    - **Max Idle Time:** 600 seconds
    - **Eviction Policy:** LRU (Least Recently Used)
    - **Maximum Size Policy:** Free heap size
---

### Key Annotations:
- **@Enable Caching:** Spring Boot autoconfigures the cache infrastructure 
- **@Cacheable:** to cache response from method
- **@CacheEvict(value = "orders-cache", key = "#id"):** evict cached value with key = id
- **@CachePut(value = "orders-cache", key = "#id"):** update cached value with key = id
---

## RabbitMQ Installation
1. Download and install ERlang http://erlang.org/downloads
2. Download and install RabbitMQ https://www.rabbitmq.com/docs/install-windows#service
3. Go to RabbitMQ Server install Directory C:\Program Files\RabbitMQ Server\rabbitmq_server-3.8.3\sbin
4. Run command rabbitmq-plugins enable rabbitmq_management
5. Open browser and enter http://localhost:15672/ to redirect to RabbitMQ Dashboard
7. Login page default username and password is guest
8. After successfully login you should see RabbitMQ Home page

## RabbitMQ Integration

RabbitMQ is used for sending and receiving messages asynchronously.

### Configuring Listeners and Producers

- **Message Producer:** Sends messages to a queue.
- **Message Listener:** Consumes messages from the queue.

### Example Configuration

1. **Producer:**

```java
rabbitTemplate.convertAndSend("rabbit_mq_exchange", "rabbit_mq_r_key", message);
```

2. **Listener:**

```java
@RabbitListener(queues = "rabbit_mq_queue")
public void listen(String message) {
    log.info("Received message: " + message);
}
```

---

## Troubleshooting

1. **RabbitMQ Connection Issues:**

    - Ensure RabbitMQ is running.
    - Verify credentials and connection properties.

2. **Cache Not Working:**

    - Check Hazelcast configurations.
    - Ensure Hazelcast is included in the classpath.

3. **Database Errors:**

    - Verify database credentials and URL in `application.properties`.
    - Ensure the database is running.

---
