### How to do it

### The important pieces are:

V1
│
└── version

__
│
└── separator

create_initial_schema
│
└── description


V2__add_isbn_to_books.sql

means roughly:

Migration version 2, whose description is "add ISBN to books."


isn't merely naming convention.

It represents:

oldest database change
        ↓
        ↓
newest database change


V1
 ↓
V2
 ↓
V3
 ↓
V4 ← correction

