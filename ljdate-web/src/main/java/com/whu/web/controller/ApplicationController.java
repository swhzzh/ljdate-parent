package com.whu.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.whu.common.entity.Application;
import com.whu.common.entity.Notification;
import com.whu.common.entity.User;
import com.whu.common.redis.RedisService;
import com.whu.common.redis.UserKey;
import com.whu.common.result.CodeMsg;
import com.whu.common.result.Result;
import com.whu.post.api.ApplicationApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Reference
    private ApplicationApi applicationApi;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisService redisService;


    /**
     * 创建申请
     *
     * @param application
     * @return
     */
    @PostMapping
    public Result<Application> create(@RequestBody Application application, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        application.setApplicant(user.getSno());
        Map<String, Object> ret = applicationApi.create(application);
        Application retApp = (Application) ret.get("application");
        if (retApp == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 1.用户在线
        Notification notification = (Notification) ret.get("notification");
        if (notification != null && redisService.exists(UserKey.getById, notification.getReceiver())) {
            // 发送通知
            messagingTemplate.convertAndSendToUser(notification.getReceiver(), "/queue/notify", notification);
        }

        return Result.success(retApp);
    }

    /**
     * 列举出某个帖子的申请
     *
     * @param request
     * @return
     */
    @GetMapping("/listByPost")
    public Result<PageInfo<Application>> listByPostId(HttpServletRequest request){
        String postId = request.getParameter("postId");
        if (postId == null){
            return Result.error(CodeMsg.REQ_PARAM_EMPTY);
        }
        String _pageNum = request.getParameter("pageNum");
        if (_pageNum == null){
            _pageNum = "1";
        }
        Integer pageNum = Integer.valueOf(_pageNum);
        String _pageSize = request.getParameter("pageSize");
        if (_pageSize == null){
            _pageSize = "10";
        }
        Integer pageSize = Integer.valueOf(_pageSize);
        return Result.success(applicationApi.listPageByPostId(postId, pageNum, pageSize));
    }

    /**
     * 列出某个用户所有的申请
     *
     * @param request
     * @param user
     * @return
     */
    @GetMapping("/list")
    public Result<PageInfo<Application>> list(HttpServletRequest request, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        String _status = request.getParameter("status");
        if (StringUtils.isEmpty(_status)){
            _status = "-1";
        }
        Integer status = Integer.valueOf(_status);
        String _pageNum = request.getParameter("pageNum");
        if (_pageNum == null){
            _pageNum = "1";
        }
        Integer pageNum = Integer.valueOf(_pageNum);
        String _pageSize = request.getParameter("pageSize");
        if (_pageSize == null){
            _pageSize = "10";
        }
        Integer pageSize = Integer.valueOf(_pageSize);
        return Result.success(applicationApi.listPageByUserId(user.getSno(), status, pageNum, pageSize));
    }

    /**
     * 查看application详情
     *
     * @param applicationId
     * @return
     */
    @GetMapping("/detail/{applicationId}")
    public Result<Application> getDetailById(@PathVariable String applicationId){
        Application application = applicationApi.getById(applicationId);
        if (application == null){
            return Result.error(CodeMsg.APPLICATION_NOT_EXIST);
        }
        return Result.success(application);
    }

    /**
     * 删除Application
     *
     * @param applicationId
     * @return
     */
    @PostMapping("/delete/{applicationId}")
    public Result<String> deleteById(@PathVariable String applicationId){
        applicationApi.deleteById(applicationId);
        return Result.success("ok");
    }

    /**
     * 清空Application
     *
     * @param reqParam
     * @param user
     * @return
     */
    @PostMapping("/clear")
    public Result<String> clear(@RequestBody JSONObject reqParam, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        Integer status = reqParam.getInteger("status");
        if (status == null){
            status = -1;
        }
        applicationApi.clear(user.getSno(), status);
        return Result.success("ok");
    }
}
