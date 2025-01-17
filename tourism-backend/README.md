# Tourism Backend API

This project provides a RESTful backend API for managing attractions, trips, users, reviews, and analytics in a tourism management system. It supports various operations for both regular users and administrators.

---

## Features

- **Attractions**: 
  - Retrieve, add, update, delete, and track traffic for attractions.
- **Trips**:
  - Retrieve pre-defined trip plans, detailed trip info, and manage trip plans (Admin only).
- **Users**:
  - Register, log in, log out, and manage user profiles.
- **Reviews**:
  - Add and retrieve reviews for attractions (Logged-in users only).
- **Admin Analytics**:
  - Retrieve analytics data such as total clicks and most visited attractions (Admin only).

---

## API Endpoints

### **Attractions**

| Method | Endpoint                      | Description                                               | Authorization      |
|--------|-------------------------------|-----------------------------------------------------------|--------------------|
| GET    | `/api/attractions`            | Retrieve a list of all attractions with basic details.     | None               |
| GET    | `/api/attractions/:id`        | Retrieve detailed information for a specific attraction.   | None               |
| POST   | `/api/attractions`            | Add a new attraction.                                      | Admin only         |
| PUT    | `/api/attractions/:id`        | Update an existing attraction.                             | Admin only         |
| DELETE | `/api/attractions/:id`        | Delete an attraction.                                      | Admin only         |
| POST   | `/api/attractions/:id/traffic`| Increment the traffic count for an attraction when clicked.| None               |

---

### **Trips**

| Method | Endpoint                  | Description                                         | Authorization      |
|--------|---------------------------|-----------------------------------------------------|--------------------|
| GET    | `/api/trips`              | Retrieve pre-defined trip plans (3-day and 5-day).  | None               |
| GET    | `/api/trips/:id`          | Retrieve detailed information for a specific trip.  | None               |
| POST   | `/api/trips`              | Add a new trip plan.                                | Admin only         |
| PUT    | `/api/trips/:id`          | Update an existing trip plan.                       | Admin only         |
| DELETE | `/api/trips/:id`          | Delete a trip plan.                                 | Admin only         |

---

### **Users**

| Method | Endpoint               | Description                                      | Authorization      |
|--------|------------------------|--------------------------------------------------|--------------------|
| POST   | `/api/users/register`  | Register a new user.                             | None               |
| POST   | `/api/users/login`     | Log in a user and return a token.                | None               |
| POST   | `/api/users/logout`    | Log out a user (invalidate token).               | Logged-in users    |
| GET    | `/api/users/profile`   | Retrieve the profile of the logged-in user.      | Logged-in users    |
| PUT    | `/api/users/profile`   | Update the profile of the logged-in user.        | Logged-in users    |

---

### **Reviews**

| Method | Endpoint                            | Description                                              | Authorization      |
|--------|-------------------------------------|----------------------------------------------------------|--------------------|
| GET    | `/api/reviews/attraction/:id`       | Retrieve all reviews for a specific attraction.           | None               |
| POST   | `/api/reviews/attraction/:id`       | Add a new review for an attraction.                      | Logged-in users    |

---

### **Admin Analytics**

| Method | Endpoint               | Description                                             | Authorization      |
|--------|------------------------|---------------------------------------------------------|--------------------|
| GET    | `/api/admin/analytics` | Retrieve analytics data (total clicks, popular attractions). | Admin only         |

---

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/tourism-backend.git
   cd tourism-backend
   ```

2. **Install dependencies**:
   ```bash
   mvn install
   ```

3. **Environment Configuration Files**:

  This project uses `.env` files to manage environment-specific configurations. Below are the contents of the `.env` and `.env.test` files:

  `.env`

  This file contains the configuration for the development environment.

  ```properties
  # Database Configuration
  DB_URL=jdbc:mysql://localhost:3306/tourismdb # Any mysql DB_URL
  DB_USERNAME=your_username 'root'
  DB_PASSWORD=your_password

  # JWT Configuration
  JWT_SECRET=your_secret_key
  JWT_EXPIRATION_MS=3600000  # 1 hour in milliseconds

  # Server Configuration
  SERVER_PORT=8080
  ```

  `.env.test`

  This file contains the configuration for the test environment.

  ```properties
  # Test Database Configuration
  DB_URL=jdbc:mysql://localhost:3306/tourism_test # Any mysql DB_URL
  DB_USERNAME=testuser
  DB_PASSWORD=your_password

  # JWT Configuration
  JWT_SECRET=your_secret_key
  JWT_EXPIRATION_MS=3600000  # 1 hour in milliseconds

  # Test Server Configuration
  SERVER_PORT=8081
  ```

---


4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

5. **Access the API**:
   - The API will be available at `http://localhost:8080`.

---

## Authentication

- The API uses **JWT (JSON Web Token)** for authentication.
- After a successful login, users will receive a token that must be included in the `Authorization` header of subsequent requests:
  ```http
  Authorization: Bearer <token>
  ```

---

## Error Handling

The API uses consistent error responses for client and server errors. Example:

```json
{
  "error": "Resource not found",
  "message": "Attraction not found with ID: 9999"
}
```

---

## Sample Requests

### 1. **Register a new user**
   ```http
   POST /api/users/register
   Content-Type: application/json

   {
     "name": "John Doe",
     "email": "john.doe@example.com",
     "password": "password123"
   }
   ```

### 2. **Log in a user**
   ```http
   POST /api/users/login
   Content-Type: application/json

   {
     "email": "john.doe@example.com",
     "password": "password123"
   }
   ```

### 3. **Retrieve all attractions**
   ```http
   GET /api/attractions
   ```

---

## Technologies Used

- **Spring Boot**: Backend framework
- **Spring Security**: Authentication and authorization
- **JWT**: Token-based authentication
- **Hibernate**: ORM for database operations
- **H2 / MySQL / PostgreSQL**: Database (configurable)
- **Maven**: Dependency management

---

## Contributors

- **Thapelo Magqazana** – [GitHub](https://github.com/your-username)

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---