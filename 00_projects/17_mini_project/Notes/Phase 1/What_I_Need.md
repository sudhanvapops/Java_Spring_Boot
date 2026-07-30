### Your library system needs exceptions like

BookNotFoundException
MemberNotFoundException
BookAlreadyIssuedException
FineNotPaidException

### Why not reuse RuntimeException?

You could do

throw new RuntimeException("Book not found");

Technically works.

But imagine a project with
200 endpoints
30 developers
500 exceptions

Now every exception looks like

RuntimeException
RuntimeException
RuntimeException
RuntimeException

