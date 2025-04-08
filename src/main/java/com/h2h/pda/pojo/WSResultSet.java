package com.h2h.pda.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WSResultSet implements Serializable {
    private int count;
    private List<Header> headers;
    private List<Map<String,String>> values;
    private ProxyError error;

    public ProxyError getError() {
        return error;
    }

    public void setError(ProxyError error) {
        this.error = error;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public List<Map<String,String>> getValues() {
        return values;
    }

    public void setValues(List<Map<String,String>>  values) {
        this.values = values;
    }
}
