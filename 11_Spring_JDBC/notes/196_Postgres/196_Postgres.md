### Postgres

how to use

change pom.xml
    remove h2
    add postgresJDBC driver

Set url:
    can create bean to acieve that
    can create xml file
    configuration file in application.properties 


Here we are using

application.properties 

spring.datasource.url=jdbc:postgresql://localhost:5432/tk_spring
spring.datasource.username=postgres
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver