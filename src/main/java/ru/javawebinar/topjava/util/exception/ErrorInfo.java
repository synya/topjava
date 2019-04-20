package ru.javawebinar.topjava.util.exception;

public class ErrorInfo {
    private String url;
    private ErrorType type;
    private String detail;

    public ErrorInfo() {
    }

    public ErrorInfo(CharSequence url, ErrorType type, String detail) {
        this.url = url.toString();
        this.type = type;
        this.detail = detail;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}