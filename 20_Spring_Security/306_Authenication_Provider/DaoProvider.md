### DaoAuthenticationProvider

DaoAuthenticationProvider is the default authentication provider used in Spring Security when you authenticate users using a username and password stored in a database.


Suppose the user enters

Username : john
Password : password123


DaoAuthenticationProvider performs several steps.


### Must know

These are the concepts you should be able to explain from memory.

- A login request reaches Spring Security.
- "UsernamePasswordAuthenticationFilter" extracts the username and password.
- It creates an unauthenticated "UsernamePasswordAuthenticationToken".

- The AuthenticationManager choose Which Provider should choose

- DaoAuthenticationProvider performs the authentication.
- It calls UserDetailsService to load the user.

- It uses PasswordEncoder.matches() to verify the password.
- If successful, it returns an authenticated Authentication object.
- Spring stores it in the SecurityContext.


### Step 1 — Receive Authentication Object

It receives

UsernamePasswordAuthenticationToken

containing

username = john
password = password123
authenticated = false

password is still raw



### Step 2 — Call UserDetailsService

It executes
userDetailsService.loadUserByUsername("john");
over own implimentation

This is the only place where Spring asks:

"Go find this user."


### Step 3 — Get UserDetails

Suppose database contains

Username : john

Password :

$2a$10$wEyX....

This password is BCrypt encoded.


Spring receives

UserDetails
which contains
username
encoded password
roles
account status
authorities


### Step 4 — Compare Passwords

The user typed
password123

Database has
$2a$10$Abd72.....

passwordEncoder.matches(
    rawPassword,
    encodedPassword
);


### Step 5 — Check Account Status

It also checks

user.isEnabled()
user.isAccountNonLocked()
user.isAccountNonExpired()
user.isCredentialsNonExpired()

these methods comes from UserDetails


### Step 6 — Success

If everything passes

user exists
password correct
account enabled
account unlocked
credentials valid

it creates

UsernamePasswordAuthenticationToken

but now

authenticated = true

and stores
Principal
Authorities
Credentials removed


