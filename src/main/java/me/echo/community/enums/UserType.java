package me.echo.community.enums;

public enum UserType {
    ORDINARY(0, "普通用户"),
    SUPERUSER(1, "超级管理员"),
    MODERATOR(2, "版主");

    private final int key;
    private final String explain;

    UserType(int key, String explain) {
        this.key = key;
        this.explain = explain;
    }

    public int getKey() {
        return key;
    }

    public String getExplain() {
        return explain;
    }
}
