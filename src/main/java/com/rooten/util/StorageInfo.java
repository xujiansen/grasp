package com.rooten.util;

public class StorageInfo {
    public String path;
    public String state;
    public boolean isRemovable;
    public boolean isValid; // 可写可删

    public StorageInfo(String path) {
        this.path = path;
    }

    public boolean isMounted() {
        return "mounted".equals(state);
    }
}
