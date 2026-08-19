### 2 migration



### Problem 

- wehn someone makes a fresh db out of this 
- if no data availble the quessry updates 0 rows
- if data seeder is done which is after migration
- it can only provide for 5 rows
- so keep that in mind 

Therefore, for a fresh database, the seeder should probably create data that is valid according to the final schema.

### Next

I need deterministic mappings so that the migration will behave correctly when executed on the existing database.

UPDATE
  ↓
Which table?
  ↓
Which column gets changed?
  ↓
What value?
  ↓
WHICH ROWS? ← WHERE


### Adding Data to the existing DB

UPDATE public.book
SET isbn = CASE id
    WHEN 1 THEN '9780062315007'
    WHEN 2 THEN '9780735211292'
    WHEN 3 THEN '9780132350884'
    WHEN 4 THEN '9781612680194'
    WHEN 5 THEN '9781585424337'
    WHEN 6 THEN '9793482167057'
END
WHERE id IN (1, 2, 3, 4, 5, 6)
  AND isbn IS NULL;

For now i have 6 columns