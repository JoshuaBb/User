# Description 
I find creating a generic user service as a great mini-project. The data model is well defined since 
there is a verbose amount of examples of people and companies implementing it. This allows experiementation with control flows and a comparison to compare with existing implementation. In the past I've implemented it in:
1. Java + Spring Boot + Hibernate + Postgres 
2. Scala + Play + Slick + Posgres
3. Python + FastApi + Spanner

This `Project` is built using Scala + Http4s + Cats + RDMS (Not decided yet).
I chose this because I've had a light dive with Cats in previous company and while Cats is an opinionated framework, I've managed to do a shallow dive in what it has to offer. 
Http4s is very opinionated with the use of Cats/. 
 

# Goals 

1. Implement a service using Http4s and Cats
2. Define an OpenAPI specification for the service
3. Create a script that generates a http server to display Open API and contracts
4. Implement some form of authentication (Basic Auth/Authorization Tokens/ect...) and provide a generalized implementation of authenticating
5. Docker-ize service 
6. Have some unit tests and integration tests 
7. Get a better handle of Cats Data Types and IO