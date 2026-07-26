### Stateless

- It means the server does not keep client state between requests.


### First, how form login normally works (stateful)

When you log in:

Username + Password
        │
        ▼
Spring Security authenticates you
        │
        ▼
Creates a Session
        │
        ▼
Stores SecurityContext in the Session
        │
        ▼
Sends Session ID (JSESSIONID) cookie

Next request:

Browser
   │
JSESSIONID Cookie
   │
   ▼
Server finds Session
   │
   ▼
Already knows who you are


The server stores your login state.
This is stateful authentication.



### Now, stateless (JWT)

You log in once:

Username + Password
        │
        ▼
Server authenticates you
        │
        ▼
Creates JWT
        │
        ▼
Returns JWT


The server doesn't store anything.

Every future request:

Client
   │
Authorization: Bearer <JWT>
   │
   ▼
Server verifies JWT
   │
   ▼
Request allowed


Notice:

No Session
No JSESSIONID
No session lookup

Each request contains everything needed.

The filter authenticates the request every time.