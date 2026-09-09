-- dilbretly tying to break in this and fix in next

ALTER TABLE public.book
    ADD COLUMN publisher VARCHAR(255) NOT NULL;

-- Since The Column is not present and its not null
-- and every row when this column exits it has to put a value
-- since default is not provided it cannot apply null
-- and will give error