### Fix

1. Change the List to Set
    The exception goes away
    Treat with suspicion. 
    The SQL still returns the full cartesian product, 
    so you’ve hidden the error but kept the row explosion.


2. @OrderColumn
    It’s no longer a bag, so it’s allowed
    but inserting in a middle should move others to next position 
    so multipel oplertion are done
    and multiple quiers are sent 

    Same row explosion, plus an extra index column to maintain. Rarely worth it.


3. Fetch One collection per Query
    Each query fetches the parents plus one collection
    The standard fix. No cartesian product, 
    and the persistence context stitches the results together.

