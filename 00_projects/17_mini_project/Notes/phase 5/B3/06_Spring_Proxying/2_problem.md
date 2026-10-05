### Problem


lest say i have two methods

@Service
public class BookService {

    public void outerMethod() {
        this.innerMethod();
    }

    @Transactional
    public void innerMethod() {
        // transactional work
    }
}


this.innerMethod()

is a direct Java method call on the current object.
It does not go through the Spring proxy.

so it wont apply transaction

Spring proxy object is a wrapper object that Spring creates around your actual bean to add extra behavior before/after method calls.


So

Controller
    ↓
Spring Proxy
    ↓
outerMethod()
    │
    │ this.innerMethod()
    ↓
actual BookService
    ↓
innerMethod()


This proxy doesnt have the transaction scanned/detected


### Thats Why

@Transactional is applied when the call passes through the Spring proxy.

That's the fundamental reason:
    @Transactional can appear to "not work" during self-invocation.

A call through the Spring proxy allows Spring's transaction interceptor to apply @Transactional behavior

Spring's proxy doesn't need to "detect the annotation" on every call.

The proxy is already configured with the transactional behavior. The problem is that the call never reaches the proxy.

