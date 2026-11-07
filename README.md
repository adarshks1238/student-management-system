# Student Academic Management System

A role-based web application for managing academic records in a college. It has three user roles (**Admin**, **Teacher** and **Student**), secure JWT login, marks and attendance management, and downloadable PDF academic reports.

Built with **Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA / Hibernate and PostgreSQL**, with a lightweight HTML/CSS/JavaScript frontend.

---

## Features

### Admin
- Dashboard with counts of teachers, students, subjects and assignments
- Create, edit, enable/disable and delete teachers
- Create, edit and delete subjects (blocked while a subject is in use)
- Assign subjects to teachers (the semester follows the subject)
- View students, enable/disable them, or delete them (also removes their marks and attendance)

### Teacher
- View own profile, assigned subjects and students
- Create and edit students (only for semesters the teacher is assigned to)
- Add and update marks (exam type, date, marks obtained, maximum marks)
- Mark and update attendance (present or absent, per subject and date)
- View the marks and attendance entered, filtered by student or subject

### Student
- View own profile
- View subject-wise marks, total marks and percentage
- View attendance history and attendance percentage
- Download an academic report as PDF

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5, Spring MVC |
| Persistence | Spring Data JPA, Hibernate 6 |
| Security | Spring Security 6, JWT (jjwt 0.12), BCrypt |
| Validation | Jakarta Bean Validation |
| Database | PostgreSQL |
| PDF | OpenPDF |
| Build | Maven |
| Frontend | HTML, CSS, JavaScript (Fetch API) |
| Tools | Eclipse, pgAdmin, Postman, Git/GitHub |

---

## Architecture

The project uses a layered architecture:

```
Controller  →  Service  →  Repository  →  Entity (PostgreSQL)
     ↑             ↑
    DTOs      Business rules
Security (JWT filter) · Global exception handling
```

```
src/main/java/com/project/sms
├── StudentManagementSystemApplication.java
├── controller    Admin, Auth, Teacher, Student, Marks, Attendance, Report controllers
├── service       AdminService, AuthService, TeacherService, StudentService,
│                 MarksService, AttendanceService, ReportService
├── repository    Spring Data JPA repositories
├── entity        User, Student, Teacher, Subject, TeacherAssignment, Marks, Attendance
├── dto           Request/response records (entities are never exposed directly)
├── security      SecurityConfig, JwtService, JwtAuthenticationFilter, CustomUserDetailsService
└── exception     Custom exceptions + GlobalExceptionHandler

src/main/resources
├── application.properties          (not committed, see setup)
├── application.properties.example  (template)
└── static                          admin.html, teacher.html, student.html, index.html, css/, js/
```

---

## Database schema

Seven tables, created automatically by Hibernate (`ddl-auto=update`):

| Table | Purpose | Key constraints |
|---|---|---|
| `users` | Login accounts and roles | unique `email` |
| `students` | Student profile (1:1 with `users`) | unique `user_id`, unique `roll_number` |
| `teachers` | Teacher profile (1:1 with `users`) | unique `user_id`, unique `employee_id` |
| `subjects` | Subjects per semester | unique `subject_code` |
| `teacher_assignments` | Teacher ↔ subject per semester | unique (teacher, subject, semester) |
| `marks` | Exam marks | unique (student, subject, exam_type) |
| `attendance` | Daily attendance | unique (student, subject, date) |

Relationships: `users` 1:1 `students`/`teachers`; `marks` and `attendance` are many-to-one to `students`, `subjects` and `teachers`; `teacher_assignments` links `teachers` and `subjects`.

Roles: `ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`. Attendance status: `PRESENT`, `ABSENT`.

---

## Security

- Passwords are hashed with **BCrypt**; plain-text passwords are never stored.
- Login returns a signed **JWT** (HS256, 24 hours). `JwtAuthenticationFilter` validates it on every request.
- URL rules:
  - `/api/admin/**` → Admin only
  - `/api/teachers/**` → Teacher only
  - `/api/students/**` and `/api/reports/**` → Student only
- **Data-level protection:**
  - A student's identity always comes from the token, so students cannot read other students' data.
  - A teacher can add or change marks and attendance only for subjects and semesters assigned to them (checked on create and update).
- Disabled accounts cannot log in.
- Errors are returned as consistent JSON (400, 401, 403, 404, 409).

---

## Getting started

### Prerequisites
- JDK 21
- Maven 3.9+ (or Eclipse with embedded Maven)
- PostgreSQL

### 1. Create the database
```sql
CREATE DATABASE profile;
```

### 2. Configure the application
Copy the template and fill in your values:

```
src/main/resources/application.properties.example  →  application.properties
```

```properties
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/profile
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

# Base64 secret, at least 256 bits
app.jwt.secret=put_a_long_random_base64_string_here
app.jwt.expiration-ms=86400000
```

`application.properties` is listed in `.gitignore` so your secrets are never committed.

### 3. Run
```bash
mvn spring-boot:run
```
or run `StudentManagementSystemApplication` from Eclipse. Hibernate creates the tables on first start. Then open **http://localhost:8085**.

### 4. Create the first admin account
No user exists on a fresh database, so insert an admin once. The password must be a BCrypt hash, which PostgreSQL can generate with the `pgcrypto` extension:

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled)
VALUES ('admin@college.edu', crypt('ChooseAStrongPassword', gen_salt('bf', 10)), 'ROLE_ADMIN', true);
```

> Never insert plain-text passwords. Login only works with BCrypt hashes.

### 5. Typical workflow
1. **Admin** logs in → adds subjects → adds teachers → assigns subjects to teachers.
2. **Teacher** logs in → creates students (in a semester they teach) → adds marks and attendance.
3. **Student** logs in → views marks and attendance → downloads the PDF report.

---

## API overview

All endpoints except login need the header `Authorization: Bearer <token>`.

**Authentication**

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Login, returns a JWT and the role |

**Admin** (`ROLE_ADMIN`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/stats` | Dashboard counts |
| GET, POST | `/api/admin/teachers` | List / create teachers |
| PUT, DELETE | `/api/admin/teachers/{id}` | Update / delete a teacher |
| PUT | `/api/admin/teachers/{id}/enabled?value=true` | Enable or disable a teacher |
| GET | `/api/admin/students` | List students |
| PUT | `/api/admin/students/{id}/enabled?value=true` | Enable or disable a student |
| DELETE | `/api/admin/students/{id}` | Delete a student and their records |
| GET, POST | `/api/admin/subjects` | List / create subjects |
| PUT, DELETE | `/api/admin/subjects/{id}` | Update / delete a subject |
| GET, POST | `/api/admin/assignments` | List / create teacher assignments |
| DELETE | `/api/admin/assignments/{id}` | Remove an assignment |

**Teacher** (`ROLE_TEACHER`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/teachers/profile` | Own profile |
| GET | `/api/teachers/subjects` | Assigned subjects |
| GET, POST | `/api/teachers/students` | List / create students |
| PUT | `/api/teachers/students/{id}` | Update a student |
| GET, POST | `/api/teachers/marks` | View (`?studentId=&subjectId=`) / add marks |
| PUT | `/api/teachers/marks/{id}` | Update marks |
| GET, POST | `/api/teachers/attendance` | View / mark attendance |
| PUT | `/api/teachers/attendance/{id}` | Update attendance |

**Student** (`ROLE_STUDENT`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/students/profile` | Own profile |
| GET | `/api/students/my-marks` | Subject-wise marks, total, percentage |
| GET | `/api/students/my-attendance` | Attendance history and percentage |
| GET | `/api/reports/my-report` | Download the PDF report |

### Example: login
```http
POST /api/auth/login
Content-Type: application/json

{ "email": "teacher@example.com", "password": "yourPassword" }
```
```json
{ "token": "eyJhbGciOi...", "tokenType": "Bearer", "email": "teacher@example.com", "role": "ROLE_TEACHER" }
```

---

## Business rules

- A teacher can create a student only in a semester they are assigned to teach.
- Marks and attendance can only be added or edited for a (subject, semester) assigned to that teacher.
- One marks entry per student, subject and exam type; one attendance entry per student, subject and date.
- `marksObtained` cannot exceed `maxMarks`.
- A teacher with recorded marks or attendance cannot be deleted (disable the account instead).
- A subject that is assigned or has marks/attendance cannot be deleted.

---

## Possible improvements

- Refresh tokens and a change-password feature
- Pagination, search and sorting in the APIs and tables
- Class-wise bulk attendance entry and CSV import/export
- Unit and integration tests (JUnit, Mockito, MockMvc)
- Swagger / OpenAPI documentation
- Dashboard charts and a redesigned UI
- Docker setup for the app and database

---

## Author

**Adarsh K Sundaresan**
GitHub: [@adarshks1238](https://github.com/adarshks1238)
