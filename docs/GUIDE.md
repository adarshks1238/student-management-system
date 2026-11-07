# Learning Guide

## Request flow (e.g. teacher adds marks)
1. `POST /api/teachers/marks` with `Authorization: Bearer <jwt>`.
2. `JwtAuthenticationFilter` extracts the email, loads the user via `CustomUserDetailsService`, validates the token with `JwtService`, stores an `Authentication` in `SecurityContextHolder`.
3. `SecurityConfig` rules check `hasRole("TEACHER")`.
4. `MarksController` validates the body (`@Valid`) and calls `MarksService.addMarks(email, request)`.
5. Service loads the `Teacher`, `Student`, `Subject`; `TeacherService.verifyAssignment` enforces the assignment rule; saves via `MarksRepository`.
6. Errors are converted to JSON by `GlobalExceptionHandler`.

## Key annotations
- `@Entity/@Table/@Id/@GeneratedValue` – map a class to a table with an auto-increment key.
- `@OneToOne` (User↔Student/Teacher) / `@ManyToOne` (Marks→Student) + `@JoinColumn` – foreign keys. `fetch = LAZY` avoids loading unneeded rows.
- `@UniqueConstraint` – prevents duplicate marks/attendance/assignments.
- `@Enumerated(STRING)` – stores enum names, not ordinals.
- `@Service`, `@RestController`, `@Component`, `@Configuration`, `@Bean` – Spring-managed beans.
- `@Transactional(readOnly = true)` – one DB session per method (needed for lazy loading).
- `@Valid`, `@NotNull`, `@Email`... – request validation.
- `@RestControllerAdvice/@ExceptionHandler` – centralised error handling.
- `@EnableWebSecurity` – activates the custom security filter chain.

## Interview questions
1. Difference between JPA, Hibernate and Spring Data JPA?
2. What does `ddl-auto=update` do and why not use it in production?
3. LAZY vs EAGER fetching; what is `LazyInitializationException`? Why `open-in-view=false`?
4. What is the N+1 problem and how does `join fetch` fix it?
5. `@OneToOne` vs `@ManyToOne` – where does the foreign key live?
6. Why BCrypt instead of SHA/MD5? What is salting?
7. Structure of a JWT; why is it stateless; where should the secret live?
8. Role of `OncePerRequestFilter` and the filter order in Spring Security.
9. `hasRole("TEACHER")` vs `hasAuthority("ROLE_TEACHER")`?
10. Why is CSRF disabled for a JWT API?
11. How do you stop a student reading another student's marks? (identity from token, not request)
12. Why use DTOs/records instead of returning entities?
13. `@ControllerAdvice` – how does it work?
14. `@Component` vs `@Service` vs `@Repository`; constructor injection vs field injection.
15. 401 vs 403 – when does each occur?
16. How would you add refresh tokens, pagination, or admin role?
17. How does Spring Data derive `existsByStudentIdAndSubjectIdAndExamType`?
18. What does `@Transactional` do on `updateMarks`? When does it roll back?
19. How would you unit test `MarksService` (Mockito) and the controllers (MockMvc)?
20. How does OpenPDF build a table and how do you stream it as a download?
