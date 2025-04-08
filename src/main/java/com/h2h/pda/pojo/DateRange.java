package com.h2h.pda.pojo;

import java.sql.Timestamp;

public class DateRange {
    Timestamp start;
    Timestamp end;

    public DateRange() {
    }

    public DateRange(Timestamp start, Timestamp end) {
        this.start = start;
        this.end = end;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }
}
