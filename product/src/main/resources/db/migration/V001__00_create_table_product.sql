create table product (
    id serial primary key,
    name varchar(255),
    price float8
);

insert into product(name, price) values ('product1', 21.2);
