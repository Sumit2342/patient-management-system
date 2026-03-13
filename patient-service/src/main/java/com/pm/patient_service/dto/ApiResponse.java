package com.pm.patient_service.dto;

public class ApiResponse<T> {
    private String status;
    private T data;
    private String message;

    public ApiResponse(String status,T data, String message){
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data, String message){
        return new ApiResponse<>("success", data, message);
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>("error", null, message);
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
