# Support Automation Service

A small Spring Boot service using Flowable to automate ticket handling with optional human review. Contains endpoints to start processes, inspect runtime/historic variables, list active tasks and submit human review decisions.

## Tech stack
1. Java (11+)
2. Spring Boot
3. Flowable Engine
4. Maven
5. openai

## Files of interest
1. `src/main/java/org/example/support/controller/TicketController.java` - REST API controller
2. `src/main/resources/application.yml` or `application.properties` - app configuration
3. BPMN process definition: `src/main/resources/processes/supportAutomation.bpmn20.xml` (or similar)

## Quickstart (Windows)
1. Ensure Java and Maven are installed.
2. Configure datasource in `application.properties` / `application.yml`.
3. Build:
   1. Open PowerShell or Command Prompt.
   2. Run: `mvn clean package`
4. Run:
   1. `mvn spring-boot:run`
   2. Or run the produced JAR: `java -jar target\your-app.jar`
5. App listens on `http://localhost:8080` by default.

## API Endpoints

1. Create ticket (start process)
   - Endpoint: `POST /tickets`
   - Request body (JSON):
       
       { "ticket": "Customer cannot access account", "confidence": 0.85 }
   - Response: `{ "processInstanceId": "<id>" }`

2. Get ticket variables / finished state
   - Endpoint: `GET /tickets/{id}`
   - Response when running:
       
       { "ticket": "...", "confidence": 0.85, "finished": false }
   - Response when finished (historic variables included):
       
       { "ticket": "...", "confidence": 0.85, "humanApproved": true, "reviewNotes": "...", "finished": true }

3. List active tasks (debug)
   - Endpoint: `GET /tickets/{id}/tasks`
   - Response example:
       
       [
           {
               "id": "task-123",
               "name": "Human Review",
               "taskDefinitionKey": "humanReview",
               "assignee": null
           }
       ]
   - If no active tasks and process finished: HTTP `410 Gone`.
   - If no active tasks and process exists: HTTP `404 Not Found`.

4. Submit human review
   - Endpoint: `POST /tickets/{id}/human-review`
   - Request body (JSON):
       
       { "approved": true, "notes": "Looks good" }
   - Successful response (JSON):
       
       {
           "processInstanceId": "<id>",
           "success": true,
           "message": "Human review submitted",
           "taskId": "<taskId>",
           "finished": true|false,
           "variables": { ... }
       }
   - Errors:
     1. Missing body: HTTP `400` with `success: false`.
     2. No active human task: HTTP `404` with `success: false`.
     3. Process already finished: HTTP `410` with `success: false`.
