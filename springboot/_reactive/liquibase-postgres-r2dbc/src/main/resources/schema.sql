create table if not exists public.samples(id UUID NOT NULL DEFAULT gen_random_uuid () primary key, name VARCHAR(255), status VARCHAR(255));

select * from public.samples;

insert into public.samples("name", "status") values('meine name', 'ACTIVE');