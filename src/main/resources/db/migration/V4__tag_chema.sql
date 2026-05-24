CREATE TABLE tags (
    id    UUID         NOT NULL PRIMARY KEY,
    tag_value VARCHAR(50)  NOT NULL UNIQUE
);

CREATE TABLE dish_tags (
    dish_id UUID NOT NULL,
    tag_id  UUID NOT NULL,
    PRIMARY KEY (dish_id, tag_id)
);

CREATE TABLE user_preferences (
    id              UUID         NOT NULL PRIMARY KEY,
    user_id         VARCHAR(255) NOT NULL,
    tag_id          UUID         NOT NULL,
    preference_type VARCHAR(20)  NOT NULL CHECK (preference_type IN ('INCLUDE', 'EXCLUDE')),
    CONSTRAINT uq_user_tag UNIQUE (user_id, tag_id)
);

alter table if exists dish_tags add constraint fk_dish_tags_dish foreign key (dish_id) references dish;
alter table if exists dish_tags add constraint fk_dish_tags_tag foreign key (tag_id) references tags;
alter table if exists user_preferences add constraint fk_user_preferences_user foreign key (user_id) references keycloak_user;
alter table if exists user_preferences add constraint fk_user_preferences_tag foreign key (tag_id) references tags;