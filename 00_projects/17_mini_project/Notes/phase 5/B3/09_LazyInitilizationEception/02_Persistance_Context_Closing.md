### Persistance Context Closing


### what is the Persistence Context

When Spring starts a transaction involving JPA

@Transactional
      ↓
Transaction starts
      ↓
Persistence Context is available
      ↓
Hibernate manages entities


The persistence context is essentially Hibernate's working area for managed entities.



When the tranactional method finishes successfully
Persistance Context Closes
and 
Book is no longer managed
and Entity Get's Detached


The Java Object Dosent Go
It will be ther ein memory
but the relationship with Hibernate changes
The Hibernate is Nolonge managing the Object


### Why Does this Matter

cause wen teh transaction is active
the object is being managed
when the object is being managed
it can lazy load

once the transaction ends
theobject is no more managed
The persistence context that Hibernate needed to perform that lazy loading is already gone.
and then teh lazy load cannot happen
the hibernate cannot make the call

And this is the setup for LazyInitializationException

