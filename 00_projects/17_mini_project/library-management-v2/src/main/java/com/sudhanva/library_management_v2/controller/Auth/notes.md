POST   /api/auth/register          -> Public signup (MEMBER only)
POST   /api/auth/login             -> Login

GET    /api/users/me               -> Current user's profile

GET    /api/admin/users            -> List users
POST   /api/admin/users            -> Admin creates a user
PATCH  /api/admin/users/{id}/role  -> Change role
PATCH  /api/admin/users/{id}/status-> Enable/disable account
DELETE /api/admin/users/{id}       -> Delete user