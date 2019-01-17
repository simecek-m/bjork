package com.example.app.bjork.wrapper;

public class DataWrapper<T> {

    private T data;
    private Integer error;

    public DataWrapper(T data) {
        this.data = data;
    }

    public DataWrapper(Integer error) {
        this.error = error;
    }

    public DataWrapper(T data, Integer error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }
}
