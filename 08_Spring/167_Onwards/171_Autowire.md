### Auto Wire

After Making COmputer interface
using autowire attribute

means when you have same variable name and id name 
it will automatically connect both

<property name="com" ref="com"></property>
Instead of adding this

add this
<bean id="alien" class="com.sudhanva.Alien" autowire="byName">

"byType"
for using type matching