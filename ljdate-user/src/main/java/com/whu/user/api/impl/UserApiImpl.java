package com.whu.user.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.whu.common.entity.User;
import com.whu.common.exception.GlobalException;
import com.whu.common.redis.RedisService;
import com.whu.common.redis.UserKey;
import com.whu.common.result.CodeMsg;
import com.whu.common.util.FastDFSClient;
import com.whu.common.util.UUIDUtil;
import com.whu.common.util.UpdateUtil;
import com.whu.user.api.UserApi;
import com.whu.user.dao.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;

@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @Value("${img.server.url}")
    private String IMG_SERVER_URL;

    /**
     * 注册
     *
     * @param reqParam 请求参数
     */
    @Override
    public void register(HttpServletResponse response,  JSONObject reqParam) {
        User user = JSON.toJavaObject(reqParam, User.class);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        userDao.insert(user);
        String token = UUIDUtil.uuid();
        // 1.添加cookie并存储token到redis
        addCookie(response, token, user);
        // 2.添加userId - user到redis中, 维护在线列表
        redisService.set(UserKey.getById, user.getSno(), user);
    }

    /**
     * 登录
     *
     * @param response
     * @param sno      学号
     * @param password 密码
     * @return
     */
    @Override
    public boolean login(HttpServletResponse response, String sno, String password) {
        if (StringUtils.isEmpty(sno) || StringUtils.isEmpty(password))
        {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        User user = userDao.getById(sno);
        if (user == null){
            throw new GlobalException(CodeMsg.USER_NOT_EXIST);
        }

        if (!user.getPassword().equals(password)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        String token = UUIDUtil.uuid();
        // 1.添加cookie并存储token到redis
        addCookie(response, token, user);
        // 2.添加userId - user到redis中, 维护在线列表
        redisService.set(UserKey.getById, sno, user);
        return true;
    }

    /**
     * 通过id获取User
     *
     * @param sno
     * @return
     */
    @Override
    public User getById(String sno) {
        // 1.从redis中获取
        User user = redisService.get(UserKey.getById, sno, User.class);
        if (user == null){
            user = userDao.getById(sno);
        }
        // 刷新redis存储时长
        redisService.set(UserKey.getById, sno, user);
        // TODO: 19-4-29 考虑是否存储到redis
        return user;
    }

    /**
     * 通过token获取user
     *
     * @param response
     * @param token
     * @return
     */
    @Override
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        if (user != null){
            // 延长有效期
            addCookie(response, token, user);
        }
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param request
     * @param oldUser redis中获取的user
     * @param reqParam 请求参数(结构必须同User)
     * @return
     */
    @Override
    public User update(HttpServletRequest request, User oldUser, JSONObject reqParam) {
        String token  = getCookieValue(request, "token");
        User user = JSON.toJavaObject(reqParam, User.class);
        // 1.复制属性
        UpdateUtil.copyProperties(oldUser, user);
        // 2.更新数据库
        userDao.update(user);
        // 3.更新缓存
        redisService.set(UserKey.token, token, user);
        redisService.set(UserKey.getById, user.getSno(), user);
        return user;
    }

    /**
     * 上传头像
     *
     * @param file 头像图片文件
     * @return
     */
    @Override
    public String uploadAvatar(MultipartFile file) {
        try {
            //1、取文件的扩展名
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //2、创建一个FastDFS的客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/fastdfs-client.conf");
            //3、执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //4、拼接返回的url和ip地址，拼装成完整的url
            String url = IMG_SERVER_URL + path;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将token添加到cookie中, 并存储到redis中
     *
     * @param response
     * @param token
     * @param user
     */
    private void addCookie(HttpServletResponse response, String token, User user){
        redisService.set(UserKey.token, token, user);
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
