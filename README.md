## Project Overview

Starting with a pretty generic Spring Boot REST API build-out and slowly expanding into functional CRUD app. Using Copilot to save time but it's a little overconfident sometimes.

### Goals

Create a fully-functional API that I can hit locally with Postman/Insomnia or use a Thymeleaf front end to submit forms with. Determine viability of AI-assisted coding and gain experience with prompts. I'm trying to split the work and manage Copilot more like a tool than a crutch.

### Methodology

Build something clean & functional via TDD alongside Copilot. Constant iteration over features and functionality.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------

## In-progress:
- Conceptualizing how I want to streamline validation (currently using a somewhat hacky validator class during bootstrapping)
- Multi-field PATCH updates
- Ongoing debate on if I want to save this as an easy skeleton reference for a basic Spring Boot CRUD app or extend it into something resembling an actual product
- Build out Thymeleaf UI pages to wire a front-end for these operations
- Add security features


## Done
✅ H2 in-memory db added for real-time regression testing via Postman

✅ GET functionality fetches a user's profile via path variable and findById

✅ POST functionality creates a new user profile

✅ PATCH functionality takes a single value update

✅ DELETE functionality removes a record by user id

✅ All implemented functionality is tested

✅ Basic back-end input validation
