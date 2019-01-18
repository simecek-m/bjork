package com.example.app.bjork.wrapper;

public class DataWrapper<T> {

    private T data;
    private String mesaage;
    private Integer error;

    public DataWrapper(T data) {
        this.data = data;
    }

    public DataWrapper(Integer error) {
        this.error = error;
    }

    public DataWrapper(T data, String message) {
        this.data = data;
        this.mesaage = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMesaage() {
        return mesaage;
    }

    public void setMesaage(String mesaage) {
        this.mesaage = mesaage;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "DataWrapper{" +
                "data=" + data +
                ", mesaage='" + mesaage + '\'' +
                ", error=" + error +
                '}';
    }
}
