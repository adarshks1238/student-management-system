# Student Academic Management System

Spring Boot 3 · Java 21 · Spring Security + JWT · Spring Data JPA/Hibernate · PostgreSQL · OpenPDF · HTML/CSS/JS

Two roles: **Teacher** (manages marks & attendance only for assigned subjects/students) and
**Student** (views own marks, attendance, downloads PDF report).

## Run it

1. Install JDK 21, Maven 3.9+ (or use Eclipse's embedded Maven), PostgreSQL.
2. Create the database (tables are created automatically by Hibernate):
   ```sql
   CREATE DATABASE profile;
   ```
   Defaults in `src/main/resources/application.properties`: user `postgres`, password `admin123`, port `5432`.
3. Start:
   ```bash
   mvn spring-boot:run
   ```
   or in Eclipse: *File → Import → Maven → Existing Maven Projects*, then right-click
   `StudentManagementSystemApplication` → *Run As → Java Application*.
4. Open http://localhost:8082

## Demo accounts (created on first start by `DataSeeder`)

| Role | Email | Password |
|------|-------|----------|
| Teacher (DS + DBMS, sem 3) | teacher@sms.com | Teacher@123 |
| Teacher (OS, sem 3) | teacher2@sms.com | Teacher@123 |
| Student (sem 3) | student1@sms.com | Student@123 |
| Student (sem 3) | student2@sms.com | Student@123 |
| Student (sem 5, unassigned) | student3@sms.com | Student@123 |

Turn off seeding with `app.seed.enabled=false`.

## API

| Method | Endpoint | Role |
|---|---|---|
| POST | /api/auth/login | public |
| GET | /api/teachers/profile, /students, /subjects | TEACHER |
| POST | /api/teachers/marks | TEACHER |
| PUT | /api/teachers/marks/{id} | TEACHER |
| GET | /api/teachers/marks?studentId=&subjectId= | TEACHER |
| POST | /api/teachers/attendance | TEACHER |
| PUT | /api/teachers/attendance/{id} | TEACHER |
| GET | /api/teachers/attendance?studentId=&subjectId= | TEACHER |
| GET | /api/students/profile, /my-marks, /my-attendance | STUDENT |
| GET | /api/reports/my-report (PDF) | STUDENT |

Send `Authorization: Bearer <token>` on protected calls. Import `postman/SMS.postman_collection.json`
into Postman (the login requests save the token automatically).

## Security rules
- BCrypt password hashing; stateless JWT (HS256, 24h) validated by `JwtAuthenticationFilter`.
- `/api/teachers/**` → ROLE_TEACHER, `/api/students/**` & `/api/reports/**` → ROLE_STUDENT.
- Student identity always comes from the token, so students cannot read others' data.
- A teacher can write marks/attendance only if an assignment exists for (teacher, subject, student's semester) — try
  teacher@sms.com adding marks for subject CS303 (assigned to teacher2) → `403`.

## Project layout
`controller` → `service` → `repository` → `entity`, plus `security`, `dto`, `exception`, `config`.
See `docs/GUIDE.md` for execution flow, annotation notes and interview questions.
