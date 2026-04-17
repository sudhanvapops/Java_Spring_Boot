### Your Entities — Classification

| Entity       | Has Business Key?   | Mutable?    | Strategy       |
| ------------ | ------------------- | ----------- | -------------- |
| Borrower     | ✅ `cardNumber`      | ❌ Immutable | ⭐ Business Key |
| Book         | ❓ (ISBN?)           | depends     | 🤔 You decide  |
| Author       | ❌ (name not unique) | mutable     | ⚠️ ID-based    |
| BorrowRecord | ❌                   | mutable     | ⚠️ ID-based    |


### equals() and hashCode()

Borrower (Best Case)

This is your cleanest entity.

Why?
cardNumber is:
unique ✅
immutable ✅
available before persistence ✅

👉 That’s PERFECT for equality.


Why is using id here a BAD idea?
becasue id is generated in db
and we have to retreat it again


Borrower b = new Borrower(...);
set.add(b);   // hashCode() uses id = null

entityManager.persist(b); // id gets assigned

hashCode() changes ❌
The object is in the wrong bucket in HashSet ❌
set.contains(b) may return false ❌


### 

Author
❌ BorrowRecord

These DO NOT have:

immutable business key ❌
stable identity before persistence ❌


For Author:

Can you safely use:

authorName?
email?

Why or why not?

author name i can beacuse even though its not immutable
once the auhtor is fixed for the book it wont change in real world
not email cause it can be nullable

<!--  ! But That sounds reasonable… but it’s not safe in system design. -->

Can two authors have the same name? - Yes 

# When No other immutable objects are avaible use id 

