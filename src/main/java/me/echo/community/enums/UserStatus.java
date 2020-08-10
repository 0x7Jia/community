package me.echo.community.enums;

public enum UserStatus {
    INACTIVATED(0, "未激活"), ACTIVATED(1, "已激活");

    private final int key;
    private final String explain;

    UserStatus(int key, String explain) {
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
