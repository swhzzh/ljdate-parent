package com.whu.user.api;



import com.alibaba.fastjson.JSONObject;
import com.whu.common.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserApi {

    /**
     * 注册
     *
     * @param reqParam 请求参数
     */
    String register(JSONObject reqParam);

    /**
     * 登录
     *
     * @param sno 学号
     * @param password 密码
     * @return
     */
    String login(String sno, String password);

    /**
     * 通过id获取User
     *
     * @param sno
     * @return
     */
    User getById(String sno);

    /**
     * 通过token获取user
     *
     * @param response
     * @param token
     * @return
     */
    User getByToken(HttpServletResponse response, String token);


    /**
     * 更新用户信息
     *
     * @param token
     * @param oldUser redis中获取的user
     * @param reqParam 请求参数(结构必须同User)
     * @return
     */
    User update(String token, User oldUser, JSONObject reqParam);

    /**
     * 上传头像
     *
     * @param avatar 头像图片文件字节数组
     * @return
     */
    String uploadAvatar(byte[] avatar, String extName, String token, User user);
}
