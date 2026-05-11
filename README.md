# RPG Full Stack Application

This project is a full stack RPG application.

The backend is currently completed and implemented in **Java Spring Boot**.

The frontend is still in development because it is being built in **Unity** and will be added as soon as it is finished.

## Current Project Status

- Backend: completed and working
- Database: PostgreSQL running in Docker
- Frontend: in development with Unity
- Final goal: backend, frontend, and database will all be fully Dockerized together

## Project Structure

```text
backend/
  rpg-backend/
    src/main/java/com/bozidar/rpg/RpgBackendApplication.java
frontend/
```

The Spring Boot application main class is located at:

```text
backend/rpg-backend/src/main/java/com/bozidar/rpg/RpgBackendApplication.java
```

## Requirements

To run the application, Docker must be installed and running.

The PostgreSQL database is started through Docker Compose.

## Running the Application

From the project root folder, go to the backend folder:

```bash
cd backend/rpg-backend
```

Start PostgreSQL with Docker Compose:

```bash
docker-compose up --build
```

Then run the Spring Boot backend application from the main application class in the IDE:

```text
backend/rpg-backend/src/main/java/com/bozidar/rpg/RpgBackendApplication.java
```

Alternatively, from the `backend/rpg-backend` folder, the backend can be started with Maven:

```bash
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

## Backend API Endpoints

### Health Check

```http
GET /api/health
```

Example response:

```json
{
  "status": "UP",
  "message": "RPG backend is running"
}
```

### Start New Run

```http
POST /api/runs
```

No request body is required.

### Get Run By ID

```http
GET /api/runs/{runId}
```

### Start Battle

```http
POST /api/runs/{runId}/battles
Content-Type: application/json
```

Example request:

```json
{
  "monsterId": "goblin_warrior"
}
```

### Play Turn

```http
POST /api/battles/{battleId}/turns
Content-Type: application/json
```

Example request:

```json
{
  "moveId": "slash"
}
```

Available hero moves:

```text
slash
shield_up
battle_cry
second_wind
```

### Equip Hero Moves

```http
PUT /api/runs/{runId}/hero/moves
Content-Type: application/json
```

Example request:

```json
{
  "moveIds": ["slash", "shield_up", "battle_cry", "second_wind"]
}
```

## Example Test Flow

### 1. Check Backend Health

```http
GET /api/health
```

### 2. Create a New Run

```http
POST /api/runs
```

Save the returned `runId`.

### 3. Start a Battle

```http
POST /api/runs/{runId}/battles
Content-Type: application/json
```

```json
{
  "monsterId": "goblin_warrior"
}
```

Save the returned `battleId`.

### 4. Play Turns Until the Battle Is Finished

```http
POST /api/battles/{battleId}/turns
Content-Type: application/json
```

```json
{
  "moveId": "slash"
}
```

### 5. Check Updated Run State

```http
GET /api/runs/{runId}
```

### 6. Equip Learned Moves

```http
PUT /api/runs/{runId}/hero/moves
Content-Type: application/json
```

```json
{
  "moveIds": ["slash", "shield_up", "battle_cry", "frenzy"]
}
```

## Frontend

The frontend is currently being developed in Unity.

It will be added to the repository and connected with the backend as soon as it is completed.

## Notes

At the moment, the backend and database are the main working parts of the application.

The final version of the project will include a Unity frontend connected to the Spring Boot backend, with the full application prepared for Docker-based execution.
