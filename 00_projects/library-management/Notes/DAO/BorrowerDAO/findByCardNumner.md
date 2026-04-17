Hibernate works using:
    Session

So we need SessionFactory


mportant Design Choice

We will use:

sessionFactory.getCurrentSession()

👉 NOT openSession()

🧠 Why?
getCurrentSession() → tied to transaction
openSession() → manual lifecycle (error-prone)


// avoids null
return q.uniqueResultOptional();
enforces expectation: only ONE result

