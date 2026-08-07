### Example of Logon Response

{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "email": "admin@library.com",
    "role": "ADMIN",
    "expiresIn": 86400 // 24 hours
}

| Field         | Why?                                                                             |
| ------------- | -------------------------------------------------------------------------------- |
| `accessToken` | JWT used for authenticated requests.                                             |
| `tokenType`   | Usually `"Bearer"` so the frontend can send `Authorization: Bearer <token>`.     |
| `userId`      | Useful for displaying the logged-in user or future profile features.             |
| `email`       | Lets the frontend show the current user's email without decoding the JWT.        |
| `role`        | Frontend can conditionally show Admin vs Librarian features.                     |
| `expiresIn`   | Helps the frontend know when the token expires (e.g., 86400 seconds = 24 hours). |
