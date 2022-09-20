# OwnYourFeed

OwnYourFeed is opensource hobby project for creating RSS base feed reader. Application is divined into backend application which provide REST API and to frontend exmaple client (in future).

## Run backend (development mode)

Application use PostgreSQL database, so you need to have one installed or you can use Docker or cloud instance. Set following environment variables for PostgreSQL database:

- POSTGRES_URL: jdbc:postgresql://url:\_port/db_name
- POSTGRES_USERNAME: username
- POSTGRES_PASSWORD: password
