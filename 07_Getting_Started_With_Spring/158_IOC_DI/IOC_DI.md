### IOC: Inversion of Control.

Instead of your code creating and managing objects directly, the Spring framework does it for you.


#### Normal Java (Without IoC)

Normally, one class creates another class itself.

class Engine {
    void start() {
        System.out.println("Engine started");
    }
}

class Car {
    Engine engine = new Engine();

    void drive() {
        engine.start();
        System.out.println("Car driving");
    }
}

Here:

Car controls creation of Engine
Car is tightly coupled to Engine

So if you later change engine type:

PetrolEngine
ElectricEngine
MockEngine for testing

you must modify Car.



### With IoC in Spring

@Component
class Engine {
    void start() {
        System.out.println("Engine started");
    }
}

@Component
class Car {

    private Engine engine;

    @Autowired
    Car(Engine engine) {
        this.engine = engine;
    }

    void drive() {
        engine.start();
        System.out.println("Car driving");
    }
}


Now:

Car does NOT create Engine
Spring creates both objects
Spring injects Engine into Car

This is IoC.


### Why is it called "Inversion" of Control?

Normally:
    Your class controls dependencies

After IoC:
    Framework controls dependencies


### The Spring Container

The thing managing this is called the:
    IoC Container
    Usually:
        ApplicationContext
        BeanFactory

It:
    creates objects
    stores them
    injects dependencies
    manages lifecycle

Objects managed by Spring are called: Beans

All the created Objects go to Container
and DI links them adn Inject


### BEANS

Spring-managed object = Bean


### Dependency Injection (DI)

DI is a Design Pattern
DI is the way Spring implements IoC.

IoC = concept
DI = technique

Example: 
    @Autowired
    Engine engine;

Spring injects dependency automatically.


### Benefits

1. Loose Coupling
    Classes depend less on concrete implementations.

2. Easier Testing
    You can replace dependencies easily.

3. Better Maintainability
    Change one component without changing everything.

4. Cleaner Code
    Less object creation code.


