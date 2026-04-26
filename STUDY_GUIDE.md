# AIM_UMS Study Guide

This guide helps you learn this project as both a Java developer and a system designer.

## 1) What You Will Learn

By the end, you should be able to:

- explain the full flow from JavaFX UI -> Controller -> Service -> JPA/Hibernate -> Database
- read and extend entity relationships (inheritance + associations)
- write safe CRUD and JPQL queries
- debug common runtime issues (DB config, UI bindings, query results)
- add one full feature across backend + UI with confidence

---

## 2) Prerequisites

- Java 17
- Maven
- MySQL running locally (for runtime app)
- Basic Java + OOP + SQL knowledge

Project stack (from `pom.xml`):

- JPA API (`jakarta.persistence`)
- Hibernate ORM
- MySQL Connector/J
- JavaFX (`javafx-controls`, `javafx-fxml`)
- JUnit 5 + H2 (tests)

---

## 3) Run And Verify First

Use this first to ensure your environment is ready.

```powershell
Set-Location "D:\Self Study\AIM_UMS"
mvn clean compile
mvn -DskipTests javafx:run
```

Run tests:

```powershell
Set-Location "D:\Self Study\AIM_UMS"
mvn test
```

---

## 4) Project Mental Model (Read This Before Code Diving)

Think in layers:

1. **UI Layer (JavaFX + FXML)**
   - Views and user interactions
2. **Controller Layer**
   - Handles button clicks, table selections, form data
3. **Service Layer**
   - Business rules, transactions, query orchestration
4. **Persistence Layer (JPA/Hibernate)**
   - Entities, relationships, SQL generation
5. **Database**
   - MySQL for runtime, H2 for tests

Key startup path:

- `src/main/java/org/example/ums/Main.java`
- `src/main/java/org/example/ums/ui/UmsFxLauncher.java`
- `src/main/java/org/example/ums/ui/UmsFxApplication.java`

---

## 5) High-Value Files By Layer

## Entry + Navigation

- `src/main/java/org/example/ums/Main.java`
- `src/main/java/org/example/ums/ui/UmsFxApplication.java`
- `src/main/java/org/example/ums/ui/SceneNavigator.java`
- `src/main/java/org/example/ums/ui/UserSession.java`

## Entities (Domain Model)

- `src/main/java/org/example/ums/entity/User.java`
- `src/main/java/org/example/ums/entity/Student.java`
- `src/main/java/org/example/ums/entity/Instructor.java`
- `src/main/java/org/example/ums/entity/Admin.java`
- `src/main/java/org/example/ums/entity/Course.java`
- `src/main/java/org/example/ums/entity/Quiz.java`
- `src/main/java/org/example/ums/entity/Question.java`
- `src/main/java/org/example/ums/entity/QuizResult.java`
- `src/main/java/org/example/ums/entity/QuizAnswer.java`

## Services (Business Logic)

- `src/main/java/org/example/ums/service/AuthService.java`
- `src/main/java/org/example/ums/service/AdminManagementService.java`
- `src/main/java/org/example/ums/service/InstructorManagementService.java`
- `src/main/java/org/example/ums/service/AcademicManagementService.java`
- `src/main/java/org/example/ums/service/AcademicQueryService.java`
- `src/main/java/org/example/ums/service/DataBootstrapService.java`

## UI Controllers

- `src/main/java/org/example/ums/ui/controller/LoginController.java`
- `src/main/java/org/example/ums/ui/controller/AdminDashboardController.java`
- `src/main/java/org/example/ums/ui/controller/InstructorDashboardController.java`
- `src/main/java/org/example/ums/ui/controller/StudentDashboardController.java`

## FXML + Theme

- `src/main/resources/org/example/ums/ui/login-view.fxml`
- `src/main/resources/org/example/ums/ui/admin-dashboard.fxml`
- `src/main/resources/org/example/ums/ui/instructor-dashboard.fxml`
- `src/main/resources/org/example/ums/ui/student-dashboard.fxml`
- `src/main/resources/org/example/ums/ui/ums-theme.css`

## Persistence + Tests

- `src/main/resources/META-INF/persistence.xml`
- `src/test/java/org/example/ums/IntegrationFlowTest.java`

---

## 6) 10-Day Study Plan

### Day 1: Boot + Explore

- Run app and tests
- Open all dashboards and click through all tabs
- Note all major user actions

### Day 2: Entity Relationships

- Draw ER-style diagram from entity classes
- Focus on:
  - inheritance `User -> Student/Instructor/Admin`
  - `Course <-> Student`
  - `Course -> Quiz -> Question`
  - `QuizResult -> QuizAnswer`

### Day 3: Authentication + Session

- Trace login flow in `LoginController` + `AuthService`
- Confirm role-based navigation behavior

### Day 4: Admin Flow Deep Dive

- Trace Add User and Add Course in controller -> service
- Verify DB changes and table refresh behavior

### Day 5: Instructor Flow Deep Dive

- Trace create quiz, add question, load students
- Understand ownership checks in instructor service

### Day 6: Student Flow Deep Dive

- Trace enroll, load quizzes, take quiz, submit
- Understand score calculation in `AcademicManagementService`

### Day 7: JPQL Practice

- Read every method in `AcademicQueryService`
- Rewrite one query in SQL and compare expected result set

### Day 8: UI + FXML Binding Practice

- Match `fx:id` values to controller fields
- Edit one label/button style and verify CSS impact

### Day 9: Debugging Drill

- Intentionally break one `fx:id`, then fix it
- Intentionally misconfigure DB credentials, then recover

### Day 10: Build One Feature

- Example: Add "Unenroll" action in student dashboard
- Implement controller + service + UI updates
- Add/update test scenario

---

## 7) Guided Feature Tracing Exercises

Use this template for each feature:

1. UI event source (button/menu/table)
2. Controller handler method
3. Service method(s)
4. Entity/JPQL usage
5. DB effect
6. UI refresh points
7. Error/success feedback path

Recommended flows:

- Admin adds instructor user
- Admin adds course with optional instructor assignment
- Instructor creates quiz + question
- Student takes quiz and submits once

---

## 8) Database And Persistence Focus

From `persistence.xml`:

- runtime PU: `umsPU` (MySQL)
- test PU: `umsTestPU` (H2 in-memory)

Study tasks:

- identify where transaction boundaries are created
- identify where lazy vs eager access might matter
- verify seed behavior in `DataBootstrapService`

Check seeded users when DB is empty:

- `admin@ums.local` / `admin123`
- `instructor@ums.local` / `inst123`
- `student1@ums.local` / `stud123`

---

## 9) Debugging Checklist

When something fails, check in this order:

1. **Compile errors**: wrong imports, missing methods, type mismatch
2. **FXML/controller mismatch**: wrong `fx:id`, wrong `onAction` handler name
3. **Session state**: role/user missing in `UserSession`
4. **DB config**: URL/user/password in `persistence.xml`
5. **Query assumptions**: empty result because enrollment/ownership constraints
6. **Transaction behavior**: object not persisted because code path exited early

---

## 10) Extension Ideas (Practice Projects)

Start easy and increase complexity:

1. Add search/filter on user and course tables
2. Add pagination for quizzes/results
3. Add edit/update user profile flow
4. Add quiz time limit support
5. Add instructor analytics (average score per quiz)
6. Add audit log entity for important actions

---

## 11) Self-Assessment Rubric

You are ready to extend this project when you can:

- explain one full use case end-to-end without looking at docs
- write a new JPQL query and integrate it in UI
- modify one entity relation safely
- troubleshoot an FXML binding issue quickly
- pass tests after adding a feature

---

## 12) Fast Revision Sheet

Remember these core concepts:

- **Architecture**: Controller -> Service -> JPA -> DB
- **Inheritance**: `User` subclasses with role-based UI
- **Data safety**: service-level validation + ownership checks
- **UI consistency**: shared CSS in `ums-theme.css`
- **Testing**: H2 integration tests for flow confidence

---

If you want, the next step is I can also generate a **one-page exam cheat sheet** and a **set of 20 interview-style questions** based on this exact project.
