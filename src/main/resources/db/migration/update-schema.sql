ALTER TABLE user_role
DROP
FOREIGN KEY FK859n2jvi8ivhui0rl0esws6o;

ALTER TABLE role_permissions
DROP
FOREIGN KEY FKcppvu8fk24eqqn6q4hws7ajux;

ALTER TABLE role_permissions
DROP
FOREIGN KEY FKf5aljih4mxtdgalvr7xvngfn1;

ALTER TABLE user_role
DROP
FOREIGN KEY FKn6r4465stkbdy93a9p8cw7u24;

ALTER TABLE products
    ADD created_at datetime NULL;

DROP TABLE role_permissions;

DROP TABLE user_role;
ALTER TABLE user_role
    DROP FOREIGN KEY FK859n2jvi8ivhui0rl0esws6o;

ALTER TABLE role_permissions
    DROP FOREIGN KEY FKcppvu8fk24eqqn6q4hws7ajux;

ALTER TABLE role_permissions
    DROP FOREIGN KEY FKf5aljih4mxtdgalvr7xvngfn1;

ALTER TABLE user_role
    DROP FOREIGN KEY FKn6r4465stkbdy93a9p8cw7u24;

ALTER TABLE products
    ADD created_at datetime NULL;

DROP TABLE role_permissions;

DROP TABLE user_role;