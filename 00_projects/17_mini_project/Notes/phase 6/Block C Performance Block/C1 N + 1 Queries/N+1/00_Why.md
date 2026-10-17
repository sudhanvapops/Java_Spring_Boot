### what causes N + 1

supose i want book and thier authors
Wher Author i markd lazy
so 

1 query: N Books
N Queries: N Author

1 + N Query total to get all the book and thier author


### Why it happens 

- Initailly we mark it as lazy
- cuase if books had 100k data
- each with 2 to 3 author 

- tht will be a large unesesarry data to handle with other information as well
- to avoid it we mark it as lazy

- but somethimes we delebreatly want it to be egaer


### Testing with

| Table | Rows | Shape |
|---|---|---|
| book | 100,000 | `author 0`..`author 4999` (~20 books per author) |
| member | 10,000 | ids 1..10000 |
| borrow_transaction | 50,000 | 5 per member |
| borrow_record | 100,000 | 2 per transaction (10 per member) |


### Egaer doesnt mean Join

Egaer doesn tmean join 
if the data is not in hiberanet persistance context
it will fire multiple quires to get it 

### How to spot it 

1. With Logs
any one 
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG