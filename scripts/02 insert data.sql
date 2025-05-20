--Insert test data
INSERT INTO users (username, email, password, role)
VALUES
('Ekaterina', 'katya@example.com', '$2a$10$PrAjYsB.tk087W3kZPlor.R8hXnrKqGEBXpZCax6gs/D5s5XdhPhC', 'ROLE_USER'),
('Mariya', 'mariya@example.com', '$2a$10$WUOE7lh60BSKTss/ncir3u3Uam.L/fIZsSl3yphy6/58nrxPPdGJq', 'ROLE_USER'),
('Ivan', 'ivan@example.com', '$2a$10$pLdAsllpIUhoue5DwQJ6gOL9CAv3GbhjXU4uKYJdl9B8hZh6LqagO', 'ROLE_USER'),
('Lena', 'lena@example.com', '$2a$10$4Ge5iF1Kwr0k2bdWFgy1juzD4IHz/gFKJP94wYneMQS/wqPkIlUv2', 'ROLE_USER'),
('Dima', 'dima@example.com', '$2a$10$Q4nY3I.xxg8K/cRHw3YGz.KfTm7vSV0PYkMREwTcESgP9GjBswui.', 'ROLE_USER');

INSERT INTO advertisements (title, content, price, seller_id, type, status, published_at, updated_at, premium_expiry_date)
VALUES
('postcards', 'postcards with SPb, I do not need them anymore', 320, 4, 'HOBBIES_AND_REST', 'ACTIVE',
'2025-05-10 10:00:00', NULL, NULL),--1

('Six of Crows fantasy book', 'book is like new one', 550, 2, 'HOBBIES_AND_REST', 'ACTIVE',
'2025-05-20 10:00:00', NULL, '2025-06-01 10:00:00'),--2

('blue chair', 'really old', 800, 2, 'HOME_AND_GARDEN', 'ACTIVE',
'2025-05-01 10:00:00', NULL, NULL),--3

('white dress', 'wedding dress for beautiful woman', 8000, 1, 'PERSONAL_ITEMS', 'ACTIVE',
'2025-05-07 10:00:00', NULL, NULL),--4

('LG TV', 'tv for a huge wall and big family', 20000, 1, 'ELECTRONICS', 'COMPLETED',
'2025-04-25 10:00:00', NULL, NULL),--5

('new jeans', 'with tag', 2400, 4, 'PERSONAL_ITEMS', 'COMPLETED',
'2025-04-12 10:00:00', NULL, NULL),--6

('colorful shirt', 'just take it away', 50, 4, 'PERSONAL_ITEMS', 'COMPLETED',
'2025-04-18 10:00:00', NULL, NULL), --7

('red car', 'my old red car is looking for new owner', 100000, 5, 'TRANSPORT', 'ARCHIVED',
'2025-02-20 10:00:00', NULL , NULL); --8

INSERT INTO sales (advertisement_id, customer_id, sold_at)
VALUES
(6, 2, '2025-04-14'),
(7, 3, '2025-04-22'),
(5, 3, '2025-05-01');

INSERT INTO grades (number, seller_id, customer_id, created_at)
VALUES
(5, 4, 2, '2025-04-15 12:00:00'),
(4, 4, 3, '2025-04-24 12:00:00'),
(5, 1, 3, '2025-05-02 12:00:00');

INSERT INTO comments (advertisement_id, author_id, content, created_at)
VALUES
(2, 5, 'oh I love this book', '2025-05-20 20:00:00'),
(4, 4, 'cool dress, but too expensive', '2025-05-11 15:00:00');

INSERT INTO chats (advertisement_id, customer_id)
VALUES (2, 4);

INSERT INTO messages (chat_id, sender_id, content, sent_at, is_read)
VALUES
(1, 4, 'how many pages are in book?', '2025-05-20 15:00:00', TRUE),
(1, 2, 'it has 670 pages', '2025-05-20 15:30:00', TRUE),
(1, 4, 'too much', '2025-05-20 17:00:00', TRUE);


