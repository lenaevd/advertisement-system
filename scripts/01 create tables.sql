--Create tables and constraints

CREATE TABLE users (
	user_id SERIAL PRIMARY KEY,
	username varchar(25) UNIQUE NOT NULL ,
	email varchar(100) UNIQUE NOT NULL,
	password varchar(150) NOT NULL,
	role varchar(15) NOT NULL
);

CREATE TABLE advertisements (
    advertisement_id SERIAL PRIMARY KEY,
    title varchar(50) NOT NULL,
    content varchar(200) NOT NULL,
    price int CHECK (price > 0),
    seller_id int,
    type varchar(100) NOT NULL,
    status varchar(20) NOT NULL,
    published_at timestamp NOT NULL,
    updated_at timestamp,
    premium_expiry_date timestamp,
    CONSTRAINT fk_user_advertisement FOREIGN KEY (seller_id) REFERENCES users(user_id)
);

CREATE TABLE sales (
    sale_id SERIAL PRIMARY KEY,
    advertisement_id int,
    customer_id int,
    sold_at date,
    CONSTRAINT fk_advertisement_sale FOREIGN KEY(advertisement_id) REFERENCES advertisements(advertisement_id),
    CONSTRAINT fk_customer_sale FOREIGN KEY(customer_id) REFERENCES users(user_id)
);

CREATE TABLE grades (
    grade_id SERIAL PRIMARY KEY,
    number int CHECK (number >=1 AND number <= 5),
    seller_id int,
    customer_id int,
    created_at timestamp NOT NULL,
    CONSTRAINT fk_seller_grade FOREIGN KEY (seller_id) REFERENCES users(user_id),
    CONSTRAINT fk_customer_grade FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

CREATE TABLE comments (
    comment_id SERIAL PRIMARY KEY,
    advertisement_id int,
    author_id int,
    content varchar(500) NOT NULL,
    created_at timestamp NOT NULL,
    CONSTRAINT fk_user_comment FOREIGN KEY(author_id) REFERENCES users(user_id),
    CONSTRAINT fk_advertisement_comment FOREIGN KEY(advertisement_id) REFERENCES advertisements(advertisement_id)
);

CREATE TABLE chats (
    chat_id SERIAL PRIMARY KEY,
    advertisement_id int,
    customer_id int,
    UNIQUE (advertisement_id, customer_id),
    CONSTRAINT fk_advertisement_chat FOREIGN KEY(advertisement_id) REFERENCES advertisements(advertisement_id),
    CONSTRAINT fk_customer_chat FOREIGN KEY(customer_id) REFERENCES users(user_id)
);

CREATE TABLE messages (
    message_id SERIAL PRIMARY KEY,
    chat_id int,
    sender_id int,
    content varchar(500) NOT NULL,
    sent_at timestamp NOT NULL,
    is_read boolean NOT NULL,
    CONSTRAINT fk_chat_message FOREIGN KEY(chat_id) REFERENCES chats(chat_id),
    CONSTRAINT fk_sender_message FOREIGN KEY(sender_id) REFERENCES users(user_id)
);

