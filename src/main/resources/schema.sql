CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_request
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(4000)                           NOT NULL,
    request_id  bigint                                  not null,
    created     timestamp without time zone             not null,
    constraint pk_item_request primary key (id),
    constraint FK_ITEM_REQUEST_ON_REQUESTER FOREIGN KEY (request_id) REFERENCES users (id)
);


CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  VARCHAR(512)                            NOT NULL,
    is_available BOOLEAN                                 NOT NULL,
    owner_id     INT                                     NOT NULL,
    request_id   INT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    constraint FK_ITEM_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id),
    constraint FK_ITEM_ON_REQUEST FOREIGN KEY (request_id) REFERENCES item_request (id),
    CONSTRAINT UQ_OWNER_ITEM_NAME UNIQUE (owner_id, name)

);


CREATE TABLE IF NOT EXISTS bookings
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date_time timestamp,
    end_date_time   timestamp,
    item_id         bigint,
    booker_id       bigint,
    approved        varchar not null,
    canceled        boolean,
    CONSTRAINT pk_booking primary key (id),
    constraint bookings_items_null_fk
        FOREIGN KEY (item_id) references items (id) ON DELETE CASCADE,
    constraint bookings_users_null_fk
        FOREIGN KEY (booker_id) references users (id) ON DELETE CASCADE
);




CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      varchar(4000)                           not null,
    item_id   bigint,
    author_id BIGINT,
    created   timestamp without time zone             not null,
    CONSTRAINT pk_comment primary key (id),
    CONSTRAINT FK_COMMENT_ON_AUTHOR foreign key (author_id) references users (id)

);

