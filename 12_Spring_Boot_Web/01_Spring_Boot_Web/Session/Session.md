### Session

A session is a way for a web application to remember a user across multiple requests.

Because HTTP is stateless.

That means:

Request 1 != Request 2

The server normally forgets everything after sending a response.

So sessions were created to maintain user-specific data.



What Happens Internally
Step 1: User Logs In

Server creates a session:

Session ID = ABC123

Server stores data:

ABC123 -> {
   username: "Sudhanva",
   role: "USER"
}
Step 2: Browser Stores Session ID

Usually in a cookie:

JSESSIONID=ABC123
Step 3: Every Request Sends Session ID

Browser automatically sends:

Cookie: JSESSIONID=ABC123

Server checks:

"Oh this is Sudhanva's session"




### Session in Servlet/Spring

You can create/access session like:

HttpSession session = request.getSession();

Store values:

session.setAttribute("username", "Sudhanva");

Read values:

String name = (String) session.getAttribute("username");



### Real Uses of Session

Sessions are commonly used for:
    login state
    shopping carts
    user preferences
    temporary user data
    multi-step forms

