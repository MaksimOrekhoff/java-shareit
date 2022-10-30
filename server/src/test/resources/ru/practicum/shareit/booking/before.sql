INSERT INTO users (ID, NAME, EMAIL)
VALUES (1, 'John', 'john.doe@example.com'),
       (2, 'Jane', 'jane.doe@example.com'),
       (3, 'Bob', 'bob.doe@example.com');

INSERT INTO item_request (id, description, requester_id, created)
VALUES (1, 'phone', 1, '2021-09-16 17:32:56');

INSERT INTO items (id, name, description, is_available, owner_id, request_id)
VALUES (1, 'item', 'notebook', true, 2, null),
       (2, 'newItem', 'phone', false, 3, 1);

INSERT INTO bookings (id, start_date_time, end_date_time, item_id, booker_id, approved, canceled)
VALUES (1, '2022-11-16 17:32:56', '2022-12-16 17:32:56', 2, 2, 'WAITING', false),
       (2, '2023-1-16 17:32:56', '2023-10-16 17:32:56', 2, 2, 'APPROVED', false);

