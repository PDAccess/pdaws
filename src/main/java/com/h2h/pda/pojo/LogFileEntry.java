package com.h2h.pda.pojo;

import java.io.File;
import java.nio.file.Path;

public class LogFileEntry {
    private String parentName;
    private String fileName;
    private long lastModified;
    private long length;

    public LogFileEntry() {

    }

    public LogFileEntry(Path p) {
        File file = p.toFile();
        setFileName(file.getName());
        setLastModified(file.lastModified());
        setLength(file.length());
        setParentName(file.getParentFile().getName());
    }

    public String getParentName() {
        return parentName;
    }

    public LogFileEntry setParentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public LogFileEntry setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public long getLastModified() {
        return lastModified;
    }

    public LogFileEntry setLastModified(long lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public long getLength() {
        return length;
    }

    public LogFileEntry setLength(long length) {
        this.length = length;
        return this;
    }
}
