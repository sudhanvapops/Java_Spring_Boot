### Designing Author Enitiy

What uniquely identifies an author?
Author ID

What information do you actually need?
Author ID
Author Name 

Do i need Books he is written ?
One author → many books ✅
One book → many authors ✅
Many To Many

email
nationality


### Questions

Are authors reused across books? / Will one author write multiple books in your system?
Yes

@ManyToMany
@JoinTable(...)
private Set<Author> authors;
In this relationship, who owns the mapping?

// Book is OWNER of relationship
    // Set of auhors
    // books filed
    // the field name that owns the relationship in the other entity
    @ManyToMany
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

But isnt it best if i do author hold it is there any benifit ?


### Key Concept (Don’t skip this)
Hibernate needs:
    ONE owner (who manages DB changes)
    ONE inverse side (who just reflects it)

IF this apply then its good if Book is the owner cause 
all Majority CRUD happens There frequnetly than in author

💡 Rule of Thumb
    Owner = the side you most frequently update

Which side will you usually modify?
    Adding authors to a book? → very common
    Adding books to an author? → less common


### 

If in Book you have:
    private Set<Author> authors;
    👉 Then in Author, what should mappedBy refer to?

It should refer to "book_id"
This is wrong
It refers to:
    The field name in the OTHER ENTITY

"authors"
here authors refers to filed that have 
@JoinTable in Book side 

### Step 4: Relationship Consistency (Advanced Thinking)
If you write:
book.getAuthors().add(author);
👉 Does author.getBooks() automatically update?

No ONly owning side does the update to Joint table

### Where should this above logic go doing both side update?
SO manually 

Where?

Option 1: DAO Layer
❌ Bad idea
Why?
DAO should only talk to DB
Not business logic

Option 2: Service Layer
⚠️ Works, but messy over time
Example problem:
You’ll repeat logic everywhere

Option 3: Entity Helper Methods (BEST PRACTICE)
👉 This is what professionals do



### Hibernate relies on for Set:

equals()
hashCode()

Without this:
Duplicate entries can happen
Set may behave incorrectly
Hibernate may get confused

and dont add fields that can do a infinite loop to toString and hashCodee
here in hashcode and equals we only used ID 
