create table product (
    id serial primary key,
    name varchar(255),
    price float
);

insert into product(name, price) values ('product1', 21.2);
