# SaaS Authentication Service

A scalable, stateful Spring Boot (Java 21) service for managing OAuth 2.0 tokens for multiple SaaS platforms. This project starts with a full implementation for Dropbox, allowing the server to securely save and reuse tokens for all future API calls.

This project is built with a 4-layer architecture (Controller, Service, Client, Data) and demonstrates key backend engineering principles like separation of concerns, configuration management, and database persistence.

## Features

* **Stateful Auth:** Securely saves OAuth 2.0 `access_token` and `refresh_token` to a persistent database.
* **Scalable Design:** 4-layer architecture makes it simple to add new services (e.g., Intercom, Slack) without breaking existing code.
* **Reusable APIs:** Provides clean, ready-to-use API endpoints that run on top of the saved tokens.
* **Zero-Setup DB:** Uses an H2 in-memory database for easy setup and testing.

## Tech Stack

* **Java 21 (LTS)**
* **Spring Boot 3.5.7**
* **Spring Web:** For RESTful controllers and `RestTemplate`.
* **Spring Data JPA:** For easy, powerful database interaction.
* **H2 Database:** As an in-memory SQL database.
* **Maven:** For dependency management.

## How to Run

1.  **Prerequisites:** You must have **Java 21** and **Maven** installed.

2.  **Clone the repo:**
    ```bash
    git clone https://github.com/maku123/saas-auth-service.git
    cd saas-auth-service
    ```

3.  **Configure Secrets (CRITICAL):**
    * **Copy the example file:**
        ```bash
        # On Linux/macOS
        cp src/main/resources/application.properties.example src/main/resources/application.properties
        
        # On Windows
        # copy src\main\resources\application.properties.example src\main\resources\application.properties
        ```
    * **Edit the new file:**
        * Open `src/main/resources/application.properties`.
        * Paste your **real Dropbox Client ID and Secret** into this file.

4.  **Run the application:**
    * From the project's root folder, run:
        ```bash
        # On Linux/macOS
        ./mvnw spring-boot:run
        
        # On Windows
        # mvnw.cmd spring-boot:run
        ```
    * The server will start on `http://localhost:8080`.

## How to Use

#### Step 1: Authorize (One-Time Setup)

First, you must authorize with Dropbox. This only needs to be done *once* per server restart (since the database is in-memory).

* Open your browser and go to:
    `http://localhost:8080/auth/start/dropbox`

This will redirect you to Dropbox to "Allow" access. When finished, you will see a "Success!" page.

#### Step 2: Call the API Endpoints

Now that your token is saved, you can call the data APIs.

* **Get Team Info:**
    `http://localhost:8080/api/dropbox/team-info`

* **Get User List:**
    `http://localhost:8080/api/dropbox/users`

* **Get Sign-in Events:**
    `http://localhost:8080/api/dropbox/events`

#### (Optional) Step 3: View the Database

* To see the saved tokens, go to:
    `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:saasdb`
* **User Name:** `sa`
* Click **Connect**.
* Run a query on the `TOKEN_STORAGE` table: `SELECT * FROM TOKEN_STORAGE`
