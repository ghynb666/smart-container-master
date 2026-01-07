package cn.fuguang.feign;

import cn.fuguang.constants.BaseConstants;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -1L;

    private String code;
    private String message;
    private T data;


    public static <T> BaseResponse<T> success(T data){
        return generateResult(BaseConstants.SUCCESS_CODE,null,data);
    }

    public static <T> BaseResponse<T> success(T data, String message){
        return generateResult(BaseConstants.SUCCESS_CODE,message,data);
    }

    public static <T> BaseResponse<T> success(){
        return generateResult(BaseConstants.SUCCESS_CODE,null,null);
    }

    public static <T> BaseResponse<T> success(String message){
        return generateResult(BaseConstants.SUCCESS_CODE,message,null);
    }

    public static <T> BaseResponse<T> fail(String retMsg){
        return generateResult(BaseConstants.ERROR_CODE, retMsg,null);
    }

    public static <T> BaseResponse<T> fail(String retCode, String retMsg){
        return generateResult(retCode, retMsg,null);
    }

    public static <T> BaseResponse<T>  fail(){
        return generateResult(BaseConstants.ERROR_CODE, null,null);
    }

    public static <T> BaseResponse<T> generateResult(String code, String message, T data){

        BaseResponse<T> result = new BaseResponse<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public boolean isSuccess(){
        return this.code.equals(BaseConstants.SUCCESS_CODE);
    }

}
