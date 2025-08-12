## Project Overview

<p align="center">
    <img src="https://skillicons.dev/icons?i=java,spring,gradle,react,ts,js,vite,html,css,git,idea,vscode"/>
</p>

Project started using Spring Initializr; a blank-canvas, generic Spring Boot project slowly expanding into a full-fledged front and back-end app. None of this project has been copied in any way from another repo or user.

*Back-end*

Java 24 Spring Boot - attempting clean, minimalist design with robust testing

JUnit 5, Mockito, full unit/integration testing

Gradle since it's just objectively better (sorry Maven, you still hold a place in my heart)

*Front-end*

React 19, Vite, TypeScript

---------------------------------------------------------------------------------------------------------------------------------------------------------------------

### Goals

The initial goal was to create a fully-functional Spring Boot API that I can hit locally with Postman. As tends to be the case, my ambitions grew and I added a React UI on top of it. The next major overhauls will be wiring in a "real" database and setting up an EC2 instance to host it.

Anyone who comes across this repo and wants to use it as a reference for a standard API design is welcome to do so. Just realize it's not fully completed yet as of writing this on August 12th, 2025.

### Methodology

Constant iteration over features and functionality. Mostly TDD here, at least on the back.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------

## In-progress:

- Adding real front-end validation and error messaging
- Expanding database from an in-memory H2 to H2 for testing and MySQL (or similar) for a "production" db
- Replacing some custom validation with Spring validation
- Backend DTO enforcemeent
- Multi-field PATCH updates


## Done

✅ Add cookie functionality to simulate auth and session

✅ Full CRUD operation between front and back ends (achieved 08/11)

✅ React front-end Fake Company site with a mock login portal

✅ Added Swagger/OpenAPI tooling

✅ H2 in-memory db added for real-time regression testing via Postman

✅ GET functionality fetches a user's profile via path variable and findById

✅ GET findAll() is now open

✅ POST functionality creates a new user profile

✅ PATCH functionality takes a single value update

✅ DELETE functionality removes a record by user id

✅ All implemented functionality is tested

✅ Basic back-end input validation
