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
