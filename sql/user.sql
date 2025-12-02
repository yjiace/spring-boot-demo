-- DROP TABLE IF EXISTS t_user;
CREATE TABLE "public"."t_user"(
    "id" varchar(64) NOT NULL,
    "username" varchar(20) NOT NULL,
    "password" varchar(255) NOT NULL,
    "mobile" varchar(20),
    "openid" varchar(64),
    "avatar_url" varchar(2048),
    "status" varchar(1) NOT NULL,
    "created_by" varchar(64) NOT NULL,
    "created_time" timestamptz NOT NULL,
    "updated_by" varchar(64) NOT NULL,
    "updated_time" timestamptz NOT NULL,
    "deleted" varchar(1) NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "public"."t_user" IS '用户表';
COMMENT ON COLUMN "public"."t_user"."id" IS '主键';
COMMENT ON COLUMN "public"."t_user"."username" IS '用户名';
COMMENT ON COLUMN "public"."t_user"."password" IS '密码';
COMMENT ON COLUMN "public"."t_user"."mobile" IS '手机号';
COMMENT ON COLUMN "public"."t_user"."openid" IS '微信openid';
COMMENT ON COLUMN "public"."t_user"."avatar_url" IS '头像';
COMMENT ON COLUMN "public"."t_user"."status" IS '状态，Y正常，N冻结';
COMMENT ON COLUMN "public"."t_user"."created_by" IS '创建人(ID)';
COMMENT ON COLUMN "public"."t_user"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_user"."updated_by" IS '更新人(ID)';
COMMENT ON COLUMN "public"."t_user"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_user"."deleted" IS '删除，Y：删除；N：未删除';


-- DROP TABLE IF EXISTS t_role;
CREATE TABLE "public"."t_role"(
    "id"           varchar(64) NOT NULL,
    "name"         varchar(20) NOT NULL,
    "created_by"   varchar(64) NOT NULL,
    "created_time" timestamptz NOT NULL,
    "updated_by"   varchar(64) NOT NULL,
    "updated_time" timestamptz NOT NULL,
    "deleted"      varchar(1)  NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "public"."t_role" IS '角色表';
COMMENT ON COLUMN "public"."t_role"."id" IS '主键';
COMMENT ON COLUMN "public"."t_role"."name" IS '角色名';
COMMENT ON COLUMN "public"."t_role"."created_by" IS '创建人(ID)';
COMMENT ON COLUMN "public"."t_role"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_role"."updated_by" IS '更新人(ID)';
COMMENT ON COLUMN "public"."t_role"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_role"."deleted" IS '删除，Y：删除；N：未删除';

-- DROP TABLE IF EXISTS t_permission;
CREATE TABLE "public"."t_permission"(
    "id"             varchar(64)  NOT NULL,
    "parent_id"      varchar(64)  NOT NULL,
    "name"           varchar(255) NOT NULL,
    "val"            varchar(255),
    "identification" varchar(255),
    "icon"           varchar(255),
    "jump_path"      varchar(1024),
    "type"           varchar(10)  NOT NULL,
    "order_num"      int4         NOT NULL,
    "remark"         varchar(255),
    "created_by"     varchar(64)  NOT NULL,
    "created_time"   timestamptz  NOT NULL,
    "updated_by"     varchar(64)  NOT NULL,
    "updated_time"   timestamptz  NOT NULL,
    "deleted"        varchar(1)   NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "public"."t_permission" IS '权限表';
COMMENT ON COLUMN "public"."t_permission"."parent_id" IS '父级id';
COMMENT ON COLUMN "public"."t_permission"."name" IS '权限名称';
COMMENT ON COLUMN "public"."t_permission"."val" IS '权限值';
COMMENT ON COLUMN "public"."t_permission"."identification" IS '权限标识,前端用于控制按钮显隐';
COMMENT ON COLUMN "public"."t_permission"."icon" IS '权限图标';
COMMENT ON COLUMN "public"."t_permission"."jump_path" IS '跳转路径';
COMMENT ON COLUMN "public"."t_permission"."type" IS '权限类型 目录:catalogue,菜单:menu,按钮:button';
COMMENT ON COLUMN "public"."t_permission"."order_num" IS '排序';
COMMENT ON COLUMN "public"."t_permission"."remark" IS '备注';
COMMENT ON COLUMN "public"."t_permission"."created_by" IS '创建人(ID)';
COMMENT ON COLUMN "public"."t_permission"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_permission"."updated_by" IS '更新人(ID)';
COMMENT ON COLUMN "public"."t_permission"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_permission"."deleted" IS '删除，Y：删除；N：未删除';

-- DROP TABLE IF EXISTS t_role_permission;
CREATE TABLE t_role_permission(
    role_id       VARCHAR(64) NOT NULL,
    permission_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

COMMENT ON TABLE "public"."t_role_permission" IS '角色权限表';
COMMENT ON COLUMN "public"."t_role_permission"."role_id" IS '角色id';
COMMENT ON COLUMN "public"."t_role_permission"."permission_id" IS '权限id';

-- DROP TABLE IF EXISTS t_user_role;
CREATE TABLE t_user_role(
    role_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (role_id, user_id)
);

COMMENT ON TABLE "public"."t_user_role" IS '用户角色表';
COMMENT ON COLUMN "public"."t_user_role"."role_id" IS '角色id';
COMMENT ON COLUMN "public"."t_user_role"."user_id" IS '用户id';

INSERT INTO "public"."t_user" ("id", "username", "password", "mobile", "openid", "avatar_url", "status", "created_by", "created_time", "updated_by", "updated_time", "deleted") VALUES ('692d4ab7fef965435984ad47', 'admin', '$2a$10$ddosHgvslK2KinNlNEXz8Og0n7LKz6Un0hxSs8rjWPP2n3QqwjYwG', NULL, NULL, NULL, 'Y', '692d4ab7fef965435984ad47', '2025-12-01 07:58:48.528595+00', '692d4ab7fef965435984ad47', '2025-12-01 07:58:48.528595+00', 'N');
INSERT INTO "public"."t_role" ("id", "name", "created_by", "created_time", "updated_by", "updated_time", "deleted") VALUES ('6926c5bb8f79996afe1104cc', '超级管理员', '6926a1958f79b1d48e11a6f2', '2025-11-26 09:17:48.849666+00', '6926a1958f79b1d48e11a6f2', '2025-11-27 02:11:27.260294+00', 'N');
INSERT INTO "public"."t_permission" ("id", "parent_id", "name", "val", "identification", "icon", "jump_path", "type", "order_num", "remark", "created_by", "created_time", "updated_by", "updated_time", "deleted") VALUES ('6926c81d8f79d5db9a7e3103', '0', '全部权限', '/**', NULL, NULL, NULL, 'catalogue', 0, NULL, '6926a1958f79b1d48e11a6f2', '2025-11-26 09:27:57.939619+00', '6926a1958f79b1d48e11a6f2', '2025-11-26 09:27:57.939619+00', 'N');
INSERT INTO "public"."t_user_role" ("role_id", "user_id") VALUES ('6926c5bb8f79996afe1104cc', '692d4ab7fef965435984ad47');
INSERT INTO "public"."t_role_permission" ("role_id", "permission_id") VALUES ('6926c5bb8f79996afe1104cc', '6926c81d8f79d5db9a7e3103');
