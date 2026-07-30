### What Doing

Standardized Error Responses
Custom Exceptions
Global Exception Handler
Error Codes


### Why Global Exception Handler

Imagine you wrote this endpoint.
POST/Books

Your code throws
NullPointerException

Spring Returns:

{
  "timestamp":"2026-07-30T09:00:22",
  "status":500,
  "error":"Internal Server Error",
  "trace":"....500 lines..."
}

If you were the frontend developer...
Would this help you fix the UI?
Probably not.

You only know:
    Something exploded.


You don't know
    which field
    why
    whether the user can fix it
    what message to show


and the Client receives
    500 Internal Server Error

Not Helpfull

the service should say
    "This book doesn't exist."

Then someone should convert that into a proper HTTP response.
    That's the job of the Global Exception Handler.


Return
    return new ApiResponse<>();

Better.

But imagine every method becomes

if(member == null){
   return ...
}

if(book == null){
   return ...
}

if(book.isIssued()){
   return ...
}

if(member.isInactive()){
   return ...
}

Soon

your service becomes

Business Logic

+
Response Creation

+
Error Messages

+
HTTP Status

+
Logging

Everything mixed together.

This violates the Single Responsibility Principle.


### What To Do

Instead
throw BookNotFoundException


The exception travels upward.

Repository

↓

Service

↓

Controller

↓

Global Exception Handler


#### The service never creates HTTP responses.

It simply says
    I cannot continue because this book wasn't found.



### Then comes the Global Exception Handler

Imagine every controller had

try{

}
catch(BookNotFoundException e){

}

Again

try{

}
catch(BookNotFoundException e){

}

Again

try{

}
catch(BookNotFoundException e){

}

50 controllers...

300 try-catches...



Spring lets one class catch everything.
@ControllerAdvice


### Standardized Error Response

If text like
"book Not Found"
"Member Misssing"
"Failed"

Frontend now has three different formats.

Every error should look identical.

Example

{
    "success": false,
    "errorCode": "BOOK_NOT_FOUND",
    "message": "Book with id 5 not found",
    "timestamp": "2026-07-30T09:12:31"
}


Now frontend always knows

success

↓

errorCode

↓

message

No guessing.


### Error Code Enum

Now imagine typing

"BOOK_NOT_FOUND"

everywhere.

Soon someone writes

BOOK_NOTFOUND

BOOK_NOT_FOUND

BOOK_NOTFOUND_EXCEPTION

BOOK Missing


Instead

public enum ErrorCode {

    BOOK_NOT_FOUND,

    MEMBER_NOT_FOUND,

    BOOK_ALREADY_ISSUED,

    MEMBER_INACTIVE
}


