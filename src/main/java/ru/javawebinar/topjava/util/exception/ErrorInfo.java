package ru.javawebinar.topjava.util.exception;

import java.util.List;

public class ErrorInfo {
    private String url;
    private ErrorType type;
    private List<String> details;

    public ErrorInfo() {
    }

    public ErrorInfo(CharSequence url, ErrorType type, List<String> details) {
        this.url = url.toString();
        this.type = type;
        this.details = details;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void addDetail(String detail) {
        this.details.add(detail);
    }
}