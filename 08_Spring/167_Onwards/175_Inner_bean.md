### Inner Bean

When you do normal bean
it is available for the whole application

so add a property tag
inside the bean you want and which is dependent


<bean id="alien" class="com.sudhanva.Alien" autowire="byName">
    <property name="age" value="21"></property>
    <property name="com">
        <bean id="com" class="com.sudhanva.Laptop">
            <constructor-arg value="Ryzen R5"/>
        </bean>
    </property>
</bean>