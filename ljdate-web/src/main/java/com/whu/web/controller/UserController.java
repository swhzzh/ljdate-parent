package com.whu.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.whu.common.entity.Notification;
import com.whu.common.entity.User;
import com.whu.common.redis.RedisService;
import com.whu.common.redis.UserKey;
import com.whu.common.result.CodeMsg;
import com.whu.common.result.Result;
import com.whu.post.api.NotificationApi;
import com.whu.user.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserApi userApi;

    @Reference
    private NotificationApi notificationApi;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisService redisService;

    /**
     * 注册
     *
     * @param reqParam
     * @param response
     * @return
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody JSONObject reqParam, HttpServletResponse response){
        String token = userApi.register(reqParam);
        addCookie(response, token);
        return Result.success("ok");
    }


    /**
     * 登陆
     *
     * @param reqParam
     * @param response
     * @return
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody JSONObject reqParam, HttpServletResponse response){
        if (reqParam != null){
            String sno  = reqParam.getString("sno");
            String password = reqParam.getString("password");
            String token = userApi.login(sno, password);
            addCookie(response, token);
            return Result.success("ok");
        }
        return Result.error(CodeMsg.REQ_PARAM_EMPTY);
    }


    /**
     * 获取用户详情
     *
     * @param user
     * @return
     */
    @GetMapping("/detail")
    public Result<User> getDetailById(User user){
        if (user == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @param reqParam
     * @param request
     * @return
     */
    @PostMapping("/update")
    public Result<User> update(User user, @RequestBody JSONObject reqParam, HttpServletRequest request){
        if (user == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        String token = getCookieValue(request, "token");
        User newUser = userApi.update(token, user, reqParam);
        return Result.success(newUser);
    }


    /**
     * 上传头像
     *
     * @param user
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/uploadAvatar")
    public Result<String> uploadAvatar(User user, MultipartFile file, HttpServletRequest request){
        if (user == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        String token  = getCookieValue(request, "token");

        //1、取文件的扩展名
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        String url = null;
        try {
            url = userApi.uploadAvatar(file.getBytes(), extName, token, user);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.UPLOAD_ERROR);
        }
        if (url == null){
            return Result.error(CodeMsg.UPLOAD_ERROR);
        }
        return Result.success(url);
    }


    /**
     * 刷新所有未读通知
     *
     * @param user
     * @return
     */
    @GetMapping("/flushNotifications")
    public Result<String> flushNotifications(User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        List<Notification> notifications = notificationApi.listByUserIdAndStatus(user.getSno(), 0);
        for (Notification notification : notifications) {
            messagingTemplate.convertAndSendToUser(notification.getReceiver(), "/queue/notify", notification);
        }
        return Result.success("ok");
    }


    /**
     * 登出
     *
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String token = getCookieValue(request, "token");
        redisService.delete(UserKey.token, token);
        redisService.delete(UserKey.getById, user.getSno());
        return Result.success("ok");
    }


    /**
     * 将token添加到cookie中, 并存储到redis中
     *
     * @param response
     * @param token
     */
    private void addCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 获取cookie
     *
     * @param request
     * @param cookiName
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }


}
