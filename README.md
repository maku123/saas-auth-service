# SaaS Authentication Service

A scalable, stateful **Spring Boot (Java 21)** service for managing and persisting **OAuth 2.0 tokens** for multiple SaaS platforms.  
This project demonstrates 4-layer architecture to solve a common backend challenge: creating a single, reliable service that can securely handle API credentials for many external applications.

The initial implementation provides a full, end-to-end authentication flow for **Dropbox Business** and exposes reusable API endpoints for fetching data.

---
## Table of Contents

- [Key Features](#key-features)
- [Architecture Deep Dive](#architecture-deep-dive)
  - [Layer Responsibilities](#layer-responsibilities)
- [Data Flow](#data-flow)
  - [Flow 1: Authorization (Saving the Token)](#flow-1-authorization-saving-the-token)
  - [Flow 2: Getting Data (Reusing the Token)](#flow-2-getting-data-reusing-the-token)
- [How to Run](#how-to-run)
  - [1. Prerequisites](#1-prerequisites)
  - [2. Clone the Repo](#2-clone-the-repo)
  - [3. Configure Secrets (CRITICAL)](#3-configure-secrets-critical)
  - [4. Run the Application](#4-run-the-application)
- [API Endpoints & Usage](#api-endpoints--usage)
  - [Step 1: Authorize (One-Time Setup)](#step-1-authorize-one-time-setup)
  - [Step 2: Call the API Endpoints](#step-2-call-the-api-endpoints)
  - [Step 3: View the Database (Optional)](#optional-step-3-view-the-database)

 ---

## Key Features

- **Stateful & Persistent:** Securely saves OAuth 2.0 `access_token` and `refresh_token` to a persistent SQL database (H2 for development).  
- **Scalable 4-Layer Architecture:** The design (Controller, Service, Client, Data) ensures a clean separation of concerns.  
- **Extensible by Design:** Built to be a "hub." Adding a new service is as simple as adding a new client and controller, without touching any existing code.  
- **Reusable APIs:** Provides a clean internal API (`/api/dropbox/...`) that runs on the saved tokens, abstracting away the auth logic.  
- **Zero-Setup Database:** Uses an H2 in-memory database for simple setup, testing, and review.

---

## Architecture Deep Dive

This project is built with a 4-layer **Separation of Concerns** architecture. This makes the service testable, scalable, and easy to maintain.

```
   [USER'S BROWSER]
          |
 (HTTP Request: /auth/start/dropbox, /api/dropbox/users)
          |
          v
 +-------------------------------------------------------------+
 |                 saas-auth-service (Spring Boot App)         |
 |-------------------------------------------------------------|
 | LAYER 1: [CONTROLLER]                                       |
 |  • AuthController.java (Handles auth requests)              |
 |  • ApiController.java  (Handles data requests)              |
 |-------------------------------------------------------------|
 |                    v (Calls methods)                        |
 |-------------------------------------------------------------|
 | LAYER 2: [SERVICE]                                          |
 |  • AuthService.java (Orchestrates getting tokens)           |
 |  • DataService.java (Orchestrates getting data)             |
 |-------------------------------------------------------------|
 |                    v (Calls methods)                        |
 |-------------------------------------------------------------|
 | LAYER 3: [CLIENT & REPOSITORY]                              |
 |  • DropboxClient.java (talks to Dropbox)                    |
 |  • TokenRepository.java (talks to our H2 DB)                |
 +-------------------------------------------------------------+
 |             |                               |               |
 | (Saves/Reads Tokens)              (Calls External API)      |
 |             v                               v               |
 |     [LAYER 4: H2 DATABASE]        [EXTERNAL: DROPBOX API]   |
 |         (The "Database")               (The "External API") |
 +-------------------------------------------------------------+
```

### Layer Responsibilities

- **1. Controller Layer:**  
  The only layer that speaks HTTP. Its *only* job is to handle incoming web requests, validate them, and call the `Service` layer.  
  It doesn't know *how* to talk to a database or an external API.

- **2. Service Layer:**  
  The "brain" of the application. It contains all business logic and orchestrates the flow.  
  For example, the `DataService` knows it must *first* ask the `TokenRepository` for a token, and *then* give that token to the `DropboxClient` to get data.

- **3. Client & Repository Layer:**  
  These are the "doers" or "specialists."
  - **Client (`DropboxClient`):** A class whose *only* job is to build and execute HTTP requests to the external Dropbox API.
  - **Repository (`TokenRepository`):** An interface whose *only* job is to talk to our internal database (save, find tokens).

- **4. Data Layer (`TokenStorage`):**  
  An `@Entity` class that defines the "blueprint" for our database table.

---

## Data Flow

This architecture provides two distinct, clean data flows.

---

### Flow 1: Authorization (Saving the Token)

This is the one-time setup to get the token.

1. User visits `/auth/start/dropbox`.  
2. `AuthController` → Redirects user to Dropbox.  
3. User allows access; Dropbox redirects back to `/auth/callback/dropbox` with a `[CODE]`.  
4. `AuthController` → Catches the `[CODE]` and passes it to the `AuthService`.  
5. `AuthService` → Calls `DropboxClient` with the `[CODE]`.  
6. `DropboxClient` → Calls Dropbox API, exchanges the `[CODE]` for `[TOKENS]`.  
7. `DropboxClient` → Returns `[TOKENS]` to `AuthService`.  
8. `AuthService` → Calls `TokenRepository` to `save()` the `[TOKENS]` to the H2 Database.  
9. `AuthController` → Returns a "Success!" message to the user's browser.  

---

### Flow 2: Getting Data (Reusing the Token)

This is the reusable flow for all future API calls.

1. User visits `/api/dropbox/users`.  
2. `ApiController` → Catches the request and calls the `DataService`.  
3. `DataService` → Calls `TokenRepository` to `findById("dropbox")`.  
4. `TokenRepository` → Gets the `[ACCESS_TOKEN]` from the H2 Database.  
5. `TokenRepository` → Returns the `[ACCESS_TOKEN]` to `DataService`.  
6. `DataService` → Calls `DropboxClient` with the `[ACCESS_TOKEN]`.  
7. `DropboxClient` → Calls Dropbox API (`/team/members/list_v2`) and gets the `[USER_LIST_JSON]`.  
8. `DropboxClient` → Returns the `[USER_LIST_JSON]` to `DataService`.  
9. `DataService` → Returns the `[USER_LIST_JSON]` to `ApiController`.  
10. `ApiController` → Sends the raw JSON data to the user's browser.  

---

## How to Run

### 1. Prerequisites

You must have **Java 21** and **Maven** installed.

---

### 2. Clone the Repo

```bash
git clone https://github.com/maku123/saas-auth-service.git
cd saas-auth-service
```

---

### 3. Configure Secrets (CRITICAL)

An example configuration file is included.

**Copy the example file:**

```bash
# On Linux/macOS
cp src/main/resources/application.properties.example src/main/resources/application.properties

# On Windows
copy src\main\resources\application.properties.example src\main\resources\application.properties
```

**Edit the new file:**

Open `src/main/resources/application.properties`,  
and paste your **real Dropbox Client ID and Secret** into this file.

---

### 4. Run the Application

From the project's root folder, run:

```bash
# On Linux/macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

The server will start on **http://localhost:8080**.

---

## API Endpoints & Usage

### Step 1: Authorize (One-Time Setup)

First, you must authorize with Dropbox.  
This only needs to be done *once* per server restart (since the database is in-memory).

- **Endpoint:** `GET /auth/start/dropbox`  
- **Action:** Open this in your browser to start the flow.  
  ```
  http://localhost:8080/auth/start/dropbox
  ```

This will redirect you to Dropbox to "Allow" access.  
When finished, you will be redirected to the callback and your tokens will be saved.

---

### Step 2: Call the API Endpoints

Now that your token is saved, you can call the data APIs.

- **Get Team Info:**  
  ```
  GET http://localhost:8080/api/dropbox/team-info
  ```

- **Get User List:**  
  ```
  GET http://localhost:8080/api/dropbox/users
  ```

- **Get Sign-in Events:**  
  ```
  GET http://localhost:8080/api/dropbox/events
  ```

---

### (Optional) Step 3: View the Database

- **Endpoint:** `GET /h2-console`  
- **Action:** Go to  
  ```
  http://localhost:8080/h2-console
  ```
- **JDBC URL:** `jdbc:h2:mem:saasdb`  
- **User Name:** `sa`  
- **Password:** *(leave blank)*  

Click **Connect**.  
Then run a query on the `TOKEN_STORAGE` table:  

```sql
SELECT * FROM TOKEN_STORAGE;
```
