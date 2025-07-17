## Project Overview

Project started using Spring Initializr; a blank-canvas, generic Spring Boot project slowly expanding into a functional CRUD API app. Using Copilot to save time but it's a little overconfident sometimes. That said, none of this project has been copied in any way from another repo or user.

### Goals

Create a fully-functional API that I can hit locally with Postman/Insomnia or use a Thymeleaf front end to submit forms with. Determine viability of AI-assisted coding and gain experience with prompts. I'm trying to split the work and manage Copilot more like a tool than a crutch.

### Methodology

Build something clean & functional via TDD alongside Copilot. Constant iteration over features and functionality.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------

## In-progress:
- Conceptualizing how I want to streamline validation. Currently using custom validation, but that's only because if I wire in @Valid and enforce DTOs, I end up with a lot of exceptions that are hard to wrangle. MethodArgumentNotValidExceptions can't be caught by ControllerAdvice and don't provide meaningful feedback. Once a UI is built, I can do front-end validation to block these at the form level and delete my custom validation while adding more reliance on Spring validation as a backup mechanism and use DTOs instead of handling JSON.
- Multi-field PATCH updates.
- Build out Thymeleaf UI pages to wire a front-end for these operations


## Done
✅ Added Swagger/OpenAPI tooling

✅ H2 in-memory db added for real-time regression testing via Postman

✅ GET functionality fetches a user's profile via path variable and findById

✅ GET findAll() is now open

✅ POST functionality creates a new user profile

✅ PATCH functionality takes a single value update

✅ DELETE functionality removes a record by user id

✅ All implemented functionality is tested

✅ Basic back-end input validation
