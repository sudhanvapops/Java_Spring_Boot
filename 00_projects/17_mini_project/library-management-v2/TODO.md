### TODO

### Current 
Register User excpetions in Global handlers


### Done 
- Standardize API error handling
- Add GlobalExceptionHandler
- Introduce ErrorCode enum
-  Get All Records Borrow Record fix it

- Make a Gllobal Validator 
- MAX_BOOK and PLUS_DAYS constant not user given change that

- change the return date logic that now to user selection

- Add spring Security
- Authentication Access Token and Refresh Token


### Next

- Add pagination
- Add optimistic locking

- Add inactive of Book and Member for all routes

- Add AOP 
- Add Role Based and Autheriztion

- Make Frontend For it

- Add Spring Cache for not changing data 
like Settings and all

####


At refreshAccessToken in authService

User.isActive exists on the model, but UserPrincipal.isEnabled() is hardcoded return true, and refreshAccesssToken never checks user.getIsActive() either. Net effect: a deactivated user can still log in, and — worse — an already-issued refresh token for a deactivated user keeps minting valid access tokens for the full 7-day lifetime, since revocation is the only thing checked (storedToken.isRevoked()), not account status. If deactivation is meant to lock a user out immediately, add an active check both at authentication and in refreshAccesssToken (e.g. if (!user.getIsActive()) throw new UserDeactivatedException();).