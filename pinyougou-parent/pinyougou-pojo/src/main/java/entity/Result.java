package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable {

    private boolean success;

    private String message;

    //错误信息
    private List<Error> errorsList= new ArrayList<>();

    public List<Error> getErrorsList() {
        return errorsList;
    }

    public void setErrorsList(List<Error> errorsList) {
        this.errorsList = errorsList;
    }

    public Result(boolean success, String message, List<Error> errorsList) {
        this.success = success;
        this.message = message;
        this.errorsList = errorsList;
    }

    public Result() {

    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
