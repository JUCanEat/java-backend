CREATE TABLE tags (
    id    UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    value VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE dish_tags (
    dish_id UUID NOT NULL REFERENCES dish(id)  ON DELETE CASCADE,
    tag_id  UUID NOT NULL REFERENCES tags(id)  ON DELETE CASCADE,
    PRIMARY KEY (dish_id, tag_id)
);

CREATE TABLE user_preferences (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id         VARCHAR(255) NOT NULL REFERENCES keycloak_user(id) ON DELETE CASCADE,
    tag_id          UUID         NOT NULL REFERENCES tags(id)          ON DELETE CASCADE,
    preference_type VARCHAR(20)  NOT NULL CHECK (preference_type IN ('INCLUDE', 'EXCLUDE')),
    CONSTRAINT uq_user_tag UNIQUE (user_id, tag_id)
);

