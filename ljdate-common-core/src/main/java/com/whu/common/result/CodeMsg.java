package com.whu.common.result;

import java.io.Serializable;

public class CodeMsg implements Serializable {
    private int code;
    private String msg;

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");

    //用户模块 5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效, 请重新登陆");
    public static CodeMsg USER_NOT_EXIST = new CodeMsg(500211, "用户不存在");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500212, "登录密码不能为空");
    public static CodeMsg USER_DUPLICATE = new CodeMsg(500213, "重复注册");

    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
    public static CodeMsg REQ_PARAM_EMPTY = new CodeMsg(500216, "参数为空");
    public static CodeMsg UPLOAD_ERROR = new CodeMsg(500217, "上传失败");

    //Post模块 5003XX
    public static CodeMsg POST_NOT_EXIST = new CodeMsg(500310, "Post不存在");

    //Application模块
    public static CodeMsg APPLICATION_NOT_EXIST = new CodeMsg(500410, "Application不存在");
    public static CodeMsg APPLICATION_DUPLICATE = new CodeMsg(500411, "重复申请");

    //Notification模块
    public static CodeMsg NOTIFICATION_NOT_EXIST = new CodeMsg(500510, "Notification不存在");


    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    @Override
    public String toString() {
        return "CodeMsg [code=" + code + ", msg=" + msg + "]";
    }
}