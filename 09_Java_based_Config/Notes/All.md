# Java-Based Spring Configuration — Quick Notes

## 1. Setup (Replacing XML with Java Config)

### Old XML way

```xml
<bean id="myCoach" class="com.demo.CricketCoach"/>
```

### Java Config way

- here @Configuration is used for the config class we created

```java
@Configuration
public class SportConfig {

    @Bean
    public Coach cricketCoach() {
        return new CricketCoach();
    }
}
```

### Loading Spring Container

```java
AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(SportConfig.class);

Coach coach = context.getBean("cricketCoach", Coach.class);

context.close();
```

---

#### Every thng can be Done in Two ways
@Component
@Configuration - @Bean

---

# 2. Bean Name

## Default Bean Name

- Spring creates bean name from class name:
- default name of the Bean is method name

```java
@Component
public class TennisCoach {
}
```



Bean name becomes:

```text
tennisCoach
```

(first letter lowercase)

---

OR


```java
@Configuration
public class SportConfig {

    @Bean(name = {"name1","name2"})
    public Coach cricketCoach() {
        return new CricketCoach();
    }
}
```

## Custom Bean Name

```java
@Component("myCoach")
public class TennisCoach {
}
```

Usage:

```java
Coach coach = context.getBean("myCoach", Coach.class);
```

---

# 3. Scope Annotation

Defines bean lifecycle.

## Singleton (Default)

Only ONE object created.

```java
@Component
@Scope("singleton")
public class TennisCoach {
}
```

Same object returned every time.

---

## Prototype

New object every time.

```java
@Component
@Scope("prototype")
public class TennisCoach {
}
```

OR

```java
@Configuration
public class SportConfig {

    @Bean
    @Scope("prototype")
    public Coach cricketCoach() {
        return new CricketCoach();
    }
}
```

---

## Checking Scope

```java
Coach c1 = context.getBean("tennisCoach", Coach.class);
Coach c2 = context.getBean("tennisCoach", Coach.class);

System.out.println(c1 == c2);
```

* `true` → singleton
* `false` → prototype

---

# 4. Autowiring

Spring automatically finds and injects required objects (dependencies) for you.

##### three types
- Constructor injection
- Setter injection
- Field injection

Example:

```java
public interface FortuneService {
    String getFortune();
}
```

```java
@Component
public class HappyFortuneService implements FortuneService {

    @Override
    public String getFortune() {
        return "Good Day!";
    }

    // or

    @Autowired
    Class anotherObj;
}
```

OR

```java
@Configuration
public class SportConfig {

    @Bean
    public Coach cricketCoach(Team team) {
        CricketCoach obj = new CricketCoach()
        obj.setAge(21);
        // dependent Class
        obj.setTeam(team)
        return obj;
    }
}
```
If Team Obj is Available Inside The Container it passes that object
without you manually creating it

---

# 5. Constructor Injection (Recommended)

```java
@Component
public class CricketCoach {

    private FortuneService fortuneService;

    @Autowired
    public CricketCoach(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }
}
```



### Important

If only ONE constructor exists:

```java
public CricketCoach(FortuneService fortuneService)
```

`@Autowired` is optional in modern Spring.

---

# 6. Setter Injection

```java
@Component
public class CricketCoach {

    private FortuneService fortuneService;

    @Autowired
    public void setFortuneService(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }

    // or 

    @Autowired
    Class anotherObj;
}
```

---

# 7. Field Injection

```java
@Component
public class CricketCoach {

    @Autowired
    private FortuneService fortuneService;
}
```

## Warning

Easy but NOT recommended in real projects.

Constructor injection is preferred.

---

# 8. Primary Annotation

Problem:

```java
@Component
public class HappyFortuneService implements FortuneService {
}
```

```java
@Component
public class SadFortuneService implements FortuneService {
}
```

Now Spring gets confused:

```java
@Autowired
private FortuneService fortuneService;
```

Error:

```text
NoUniqueBeanDefinitionException
```

---

## Solution → `@Primary`

```java
@Component
@Primary
public class HappyFortuneService implements FortuneService {
}
```

OR

```java
@Configuration
public class SportConfig {

    @Bean
    public Coach cricketCoach(Team team) {
        CricketCoach obj = new CricketCoach()
        obj.setAge(21);
        // dependent Class
        obj.setTeam(team)
        return obj;
    }
}

```

OR

```java
@Configuration
public class SportConfig {

    @Bean
    public Coach cricketCoach(@Primary("BeanName") team) {
        CricketCoach obj = new CricketCoach()
        obj.setAge(21);
        // dependent Class
        obj.setTeam(team)
        return obj;
    }
}

```

Spring uses this bean by default.

---

# 9. Qualifier Annotation

Choose exact bean manually.

```java
@Component("happyService")
public class HappyFortuneService implements FortuneService {
}
```

```java
@Component("sadService")
public class SadFortuneService implements FortuneService {
}
```

Injection:

```java
@Autowired
@Qualifier("sadService")
private FortuneService fortuneService;
```

OR

```java
@Configuration
public class SportConfig {

    @Bean
    public Coach cricketCoach(@Qualifier("BeanName") team) {
        CricketCoach obj = new CricketCoach()
        obj.setAge(21);
        // dependent Class
        obj.setTeam(team)
        return obj;
    }
}

```

---

# 10. Component Stereotype Annotations

These tell Spring to create beans automatically
and manage it

---

```java
@Configuration
@ComponentScan("com.sudhanva") // Base Package
public class SportConfig {

    @Bean
    public Coach cricketCoach() {
        return new CricketCoach();
    }
}
```
@ComponentScan("com.sudhanva") it scans for the Beans and manages it

---

## `@Component`

General-purpose bean.

```java
@Component
public class TennisCoach {
}
```

---

## `@Controller`

Used in Spring MVC controllers.

```java
@Controller
public class HomeController {
}
```

---

## `@Service`

Business logic layer.

```java
@Service
public class UserService {
}
```

---

## `@Repository`

Database layer / DAO.

```java
@Repository
public class UserDAO {
}
```

Extra DB exception handling support.

---

# 11. Component Scanning

Spring scans package for annotations.

```java
@Configuration
@ComponentScan("com.demo")
public class SportConfig {
}
```

Without this:

* `@Component`
* `@Service`
* `@Repository`

won’t work.

---

# 12. Value Annotation

Inject literal values.

```java
@Component
public class CricketCoach {

    @Value("RCB")
    private String team;
}
```

---

## Reading from Properties File

### sports.properties

```properties
team.name=RCB
coach.name=Virat
```

---

### Config Class

```java
@Configuration
@PropertySource("classpath:sports.properties")
public class SportConfig {
}
```

---

### Using Values

```java
@Component
public class CricketCoach {

    @Value("${team.name}")
    private String team;

    @Value("${coach.name}")
    private String coach;
}
```

---

# 13. Full Flow Example

```java
@Configuration
@ComponentScan("com.demo")
@PropertySource("classpath:sport.properties")
public class SportConfig {
}
```

```java
@Component
public class HappyFortuneService implements FortuneService {

    @Override
    public String getFortune() {
        return "Lucky Day!";
    }
}
```

```java
@Component
@Scope("singleton")
public class CricketCoach {

    private FortuneService fortuneService;

    @Value("${team.name}")
    private String team;

    @Autowired
    public CricketCoach(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }

    public String getDailyWorkout() {
        return "Practice Fast Bowling";
    }
}
```

---

# Quick Revision Table

| Topic             | Purpose              |
| ----------------- | -------------------- |
| `@Configuration`  | Java config class    |
| `@Bean`           | Manually create bean |
| `@Component`      | Auto-detect bean     |
| `@ComponentScan`  | Scan package         |
| `@Autowired`      | Dependency injection |
| `@Qualifier`      | Choose specific bean |
| `@Primary`        | Default bean         |
| `@Scope`          | Bean lifecycle       |
| `@Value`          | Inject values        |
| `@PropertySource` | Load properties file |

---

# Best Practices

## Prefer

✅ Constructor Injection
✅ `@Qualifier` when multiple beans exist
✅ Singleton scope unless needed

---

## Avoid

❌ Field injection in large projects
❌ Too many prototype beans
❌ Mixing XML + Java config unnecessarily
