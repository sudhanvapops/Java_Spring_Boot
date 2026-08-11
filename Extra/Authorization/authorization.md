### Authorization

URL-level

its the last filter in filter chain

Every Role must be Prefixed with ROLE_
you can change it in Confugiration class

And we have to assign prermissions for each role


add 
### Multiple Implimentation of Authorization MAanger

1. RequestMatcherDelegatingAuthorizationManager
which is user by requestMatcher() 

2. AUthorityAuthorizationManager
which is used to check roles 
and used by above

hasRole() for role based access
hasAuthorties() for permsion based access

above is Authorization


Request
   ↓
Authentication filters
   ↓
UserDetailsService / DAO
   ↓
UserDetails
   ↓
Authentication
   ↓
SecurityContext
   ↓
AuthorizationFilter
   ↓
AuthorizationManager
   ↓
"Which authorization rule applies?"
   ↓
"Does this Authentication have the required authority?"
   ↓
ALLOW or DENY


eg:
hasRole("ADMIN)
Spring creates an appropriate AuthorizationManager.

AuthorizationManager:
    "Is this Authentication allowed to access this request?"

The manager examines the user's authorities:
    authentication.getAuthorites()

ROLE_ADMIN

and checks wether the required authorites exists
.hasRole("ADMIN")

if present


✅ Granted
    ↓
request continues
    ↓
Controller

if denied


❌ Denied
    ↓
AccessDeniedException
    ↓
ExceptionTranslationFilter
    ↓
AccessDeniedHandler
    ↓
403 Forbidden


### Methods

Method level Security
@EnableMethodSecurity


2. PreAuthorization and PostAuthorization

