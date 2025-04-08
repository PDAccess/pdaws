package com.h2h.pda.pojo;

import java.io.Serializable;

public class Header implements Serializable {
    private String title;

    public Header(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
