package com.apon.taalmaatjes.backend.facade;

public class Result {
    private boolean hasErrors;
    private String errorMessage;

    public Result() {
        hasErrors = false;
    }

    public Result(String errorMessage) {
        hasErrors = true;
        this.errorMessage = errorMessage;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
