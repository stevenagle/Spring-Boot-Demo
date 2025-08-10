## Project Overview

<p align="center">
    <img src="https://skillicons.dev/icons?i=java,spring,gradle,react,ts,js,vite,html,css,git,idea,vscode"/>
</p>

Project started using Spring Initializr; a blank-canvas, generic Spring Boot project slowly expanding into a full-fledged front and back-end app. None of this project has been copied in any way from another repo or user.

### Goals

The initial goal was to create a fully-functional Spring Boot API that I can hit locally with Postman. As tends to be the case, my ambitions grew and I'm adding a "real" React UI on top of it. I'm splitting the work with Copilot and experimenting a little along the way.

### Methodology

Constant iteration over features and functionality. Mostly TDD here, at least on the back.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------

## In-progress:

- Wiring the whole thing together
- Some API refactors
- Debating (and probably will end up doing) a database rework, using H2 only for testing and designing a MySQL schema for "production" (finger quotes)
- Currently using custom validation, but that's only because if I wire in @Valid and enforce DTOs, I end up with a lot of exceptions that are hard to wrangle. MethodArgumentNotValidExceptions can't be caught by ControllerAdvice and don't provide meaningful feedback. Once the UI is finished, I can lean on front-end validation to block these at the form level and increase reliance on Spring back-end validations.
- Multi-field PATCH updates.


## Done
✅ React front-end Fake Company site with a mock login portal (merged in on 08/10, still wiring it up though)

✅ Added Swagger/OpenAPI tooling

✅ H2 in-memory db added for real-time regression testing via Postman

✅ GET functionality fetches a user's profile via path variable and findById

✅ GET findAll() is now open

✅ POST functionality creates a new user profile

✅ PATCH functionality takes a single value update

✅ DELETE functionality removes a record by user id

✅ All implemented functionality is tested

✅ Basic back-end input validation
