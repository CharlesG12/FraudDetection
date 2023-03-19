package com.mastercard.fraud.exception;

import lombok.Data;

@Data
public class AjaxResponse {
    private boolean isOk;
    private int code;
    private String message;
    private Object data;

    private AjaxResponse(){}

    public static AjaxResponse success(Object obj) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        ajaxResponse.setOk(true);
        ajaxResponse.setCode(200);
        ajaxResponse.setMessage("Get Successes");
        ajaxResponse.setData(obj);
        return ajaxResponse;
    }

//    public static AjaxResponse fail(Object obj) {
//        AjaxResponse ajaxResponse = new AjaxResponse();
//        ajaxResponse.setOk(true);
//        ajaxResponse.setCode(200);
//        ajaxResponse.setMessage("Invalid Input");
//        ajaxResponse.setData(obj);
//        return ajaxResponse;
//    }

    public static AjaxResponse error(CustomException e) {
        AjaxResponse ajaxResponse = new AjaxResponse();
        ajaxResponse.setOk(false);
        ajaxResponse.setCode(e.getCode());
        ajaxResponse.setMessage(e.getMessage());
        return ajaxResponse;
    }
}
