# User Management System

This is a simple CRUD Java / Maven / Spring Boot (version 2.3.3) RESTful API application for managing users in database. The users can be registered, verified, updated and deactivated.

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f08aa9e42f7d4071b444affd5d500ca3)](https://www.codacy.com/gh/tajoku/user-management-system/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tajoku/user-management-system&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/f08aa9e42f7d4071b444affd5d500ca3)](https://www.codacy.com/gh/tajoku/user-management-system/dashboard?utm_source=github.com&utm_medium=referral&utm_content=tajoku/user-management-system&utm_campaign=Badge_Coverage)
## Structure 
This application follows the MVC pattern.

  * Model: User 
  * Repository: UserRepository
  * Service: UserService, EmailService
  * Exceptions: UserNotFoundException, UserAlreadyExistsException
  * RestController: UserController

## Tools 

  * Java
  * Spring Boot
  * Jpa
  * H2
  * Flyway 
  * Maven
  * Lombok
  * SendGrid

## About the Service

The service is just a simple user management REST service. It uses an in-memory database (H2) to store the data with the use of flyway for migrations. 

Here is what this application demonstrates: 

  * Creation of users through the registration endpoint
  * Fetching paginated list of all active users with the option to include deactivated users as well.
  * Generation of a verification link emailed to the users after a successful user registration.
  * Updating user profile to VERIFIED after successfully calling the verification link
  * Ability to update specific fields of the user profile.
  * Ability to deactivate user profiles.
  * Controller Exception handling.
  * Asynchronous email sending after creating, updating, verifying and deactivating a user. 
  * Integration tests
  * Demonstrates MockMVC test framework with associated libraries
  * All APIs are "self-documented" by Swagger2 using annotations 
  * Migration scripts to add dummy users for testing

Here are some endpoints you can call:

### Register a user

```
POST /api/user
Accept: application/json
Content-Type: application/json

{
  "email": "jd@example.com",
  "firstname": "John",
  "lastname": "Doe",
  "mobile": "000000",
  "password": "password",
  "role": "USER|ADMIN",
  "title": "Mr."
}
```

### Retrieve a paginated list of existing users

```
GET /api/users?page=0&size=10&includeDeactivated=true

Response: HTTP 200
Content: paginated list 
```

### Update an existing user

```
PUT /api/user/1
Accept: application/json
Content-Type: application/json

{
  "email": "jd@example.com",
  "firstname": "John",
  "lastname": "Doe",
  "mobile": "000000",
  "password": "password",
  "title": "Mr."
}
```

### Deactivate an existing user

```
DEL /api/user/1
```

### Verify an existing user

```
GET /api/user/verify/123e4567-e89b-12d3-a456-426614174000

```
### To view Swagger 2 API docs

Run the server and browse to localhost:8080/swagger-ui.html

### Note

  * Emails are unique per user.
  * A unique UUID code is generated everytime a user is registered. This is used for building the verification link.