### Spring Boot Object Lifecycle

The Spring object lifecycle is basically:
    Spring creates your object 
    injects dependencies 
    initializes it 
    uses it 
    destroys it when the application/context shuts down.




When Spring Boot starts, component scanning sees @Service.

Spring decides:
    "I need a BookService object."



Initialization callbacks happen
After Spring has created the object and injected its dependencies, initialization callbacks can run.


@PostConstruct
public void init() {
    System.out.println("BookService initialized");
}

