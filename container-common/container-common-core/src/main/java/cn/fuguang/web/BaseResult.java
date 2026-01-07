package cn.fuguang.web;

import cn.fuguang.constants.BaseConstants;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = -1L;

    private String code;
    private String message;
    private T data;


    public static <T> BaseResult<T> success(T data){
        return generateResult(BaseConstants.SUCCESS_CODE,null,data);
    }

    public static <T> BaseResult<T> success(T data, String message){
        return generateResult(BaseConstants.SUCCESS_CODE,message,data);
    }

    public static <T> BaseResult<T> success(){
        return generateResult(BaseConstants.SUCCESS_CODE,null,null);
    }

    public static <T> BaseResult<T> success(String message){
        return generateResult(BaseConstants.SUCCESS_CODE,message,null);
    }

    public static <T> BaseResult<T> fail(String retMsg){
        return generateResult(BaseConstants.ERROR_CODE, retMsg,null);
    }

    public static <T> BaseResult<T> fail(){
        return generateResult(BaseConstants.ERROR_CODE, BaseConstants.ERROR_MSG,null);
    }

    public static <T> BaseResult<T> fail(String retCode, String retMsg){
        return generateResult(retCode, retMsg,null);
    }

    public static <T> BaseResult<T> generateResult(String code, String message, T data){

        BaseResult<T> result = new BaseResult<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }


}
