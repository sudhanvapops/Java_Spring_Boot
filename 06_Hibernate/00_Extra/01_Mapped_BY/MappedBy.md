### First: What problem does mappedBy solve?

In relationships like @OneToMany or @ManyToMany, there are two sides:

Owning side (controls DB)
Inverse side (just reflects)

👉 mappedBy tells Hibernate:

“I am NOT the owner. The other side handles the relationship.”


### Think of it like this

Two people:

Book
Author

👉 Who controls the relationship?

If Book has the foreign key / join table, then:

Book = Owner
Author = Inverse (mappedBy)

### What does mappedBy = "authors" mean?

👉 It means:

“Go to the Book class, look at the field named authors, that field owns the relationship.”

### SUPER SIMPLE RULE 🔥
Side	Annotation
Owner	has @JoinTable / @JoinColumn
Inverse	has mappedBy


ONLY the owning side updates the database
book.getAuthors().add(author);   // ✅ works
author.getBooks().add(book);     // ❌ ignored unless synced

Always update both sides manually:
book.getAuthors().add(author);
author.getBooks().add(book);