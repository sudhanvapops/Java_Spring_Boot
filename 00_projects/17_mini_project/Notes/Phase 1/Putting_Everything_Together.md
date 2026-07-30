### Putting Together

Client

↓

POST /issueBook

↓

Controller

↓

Service

↓

Book doesn't exist

↓

throw BookNotFoundException

↓

GlobalExceptionHandler

↓

Creates Standard Error Response

↓

HTTP 404

↓

Client receives

{
   success:false,
   errorCode:"BOOK_NOT_FOUND",
   message:"Book 15 not found"
}


### Each Layer Has Now One Responsibilty


| Layer                  | Responsibility                                      |
| ---------------------- | --------------------------------------------------  |
| Controller             | Receive request and return response                 |
| Service                | Business logic; throw exceptions when rules fail    |
| Repository             | Database access                                     |
| ExceptionHandler       | Convert exceptions into HTTP responses in each class|
| GlobalExceptionHandler | Instead of same excpeion everywhere added one globally|
| ErrorResponse DTO      | Define a consistent error format                    |
| ErrorCode enum         | Provide stable, machine-readable error identifiers  |
| Custom Exception       | Represent a specific business failure               |

