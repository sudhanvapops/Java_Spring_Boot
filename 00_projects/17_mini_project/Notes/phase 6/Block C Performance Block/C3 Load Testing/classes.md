### PerfConatinerConfig Class


This class is for configuration of PerfApplication class
the proxyBeanMethods = false


for example:


@Configuration
public class AppConfig {

    @Bean
    public Database database() {
        return new Database();
    }

    @Bean
    public UserService userService() {
        return new UserService(database());
    }
}


means When one @Bean Methods calls another Bean Methd inside it 
The intersepor wont intersept the @Bean in anotehr rsukting 
each time creatign new Objct 

the above is wothout interceptor

with interceptor 
spring gives te anaged bean from the IOC container 


here databse()



### What does proxyBeanMethods = true mean?

By default @ConfigurationUses a mechanism called CGLIB proxy
this is a special version of Configuration class 
that intercepts call to its @Bean methods

userService() is called
Proxy intercepts database()
Returns the existing Spring-managed Database bean

Spring is not creating a new database object every time that method is called through the configuration class.

with proxyBeanMethods = false?

Now Spring does not create that special configuration-class proxy to intercept calls between @Bean methods.
Therefore, database() inside userService() executes like an ordinary Java method.
Result: it creates another Database object.

However, Spring still manages the bean returned by the database() method when Spring calls that method to register the bean. 
Setting this option to false does not disable Spring's bean management.


### Then Why Write this


Your configuration has only one bean method, 
so there are no inter-bean method calls to intercept. 
Disabling proxying avoids unnecessary proxying and is a good fit for this simple configuration.



### WHy

Why is each SQL statement set-based (generate_series) rather than a Java loop?
beacuse java creates object
Makes insert operations
and sedns it to Hibernate or JDBC without batching

That causes time
unecessry Too many insert operation, write operation

While Postgress is atomic



Why ANALYZE after a bulk load?

postgres planner needs to estimate how many rows wach step will produce
to plan appropriate Join stratergy


ANALYZE collects statistics 
about table contents 
and stores them in PostgreSQL's system catalogs 
for the query planner.



Why does the records insert join on row_number() instead of guessing ids?