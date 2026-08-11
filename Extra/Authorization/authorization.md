### Authorization

its the last filter in filter chain

Every Role must be Prefixed with ROLE_
you can change it in Confugiration class

And we have to assign prermissions for each role


### Multiple Implimentation of Authorization MAanger

1. RequestMatcherDelegatingAuthorizationManager
which is user by requestMatcher() 

2. AUthorityAuthorizationManager
which is used to check roles 
and used by above

hasRole()

2. Pre and Post 


### Methods

