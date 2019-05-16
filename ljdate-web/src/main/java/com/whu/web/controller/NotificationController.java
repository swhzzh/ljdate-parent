package com.whu.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.whu.common.entity.Notification;
import com.whu.common.entity.User;
import com.whu.common.result.CodeMsg;
import com.whu.common.result.Result;
import com.whu.post.api.NotificationApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Reference
    private NotificationApi notificationApi;

    /**
     * 列出用户的通知
     *
     * @param request
     * @param user
     * @return
     */
    @GetMapping("/list")
    public Result<PageInfo<Notification>> list(HttpServletRequest request, User user){
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
            _pageNum = "0";
        }
        Integer pageNum = Integer.valueOf(_pageNum);
        String _pageSize = request.getParameter("pageSize");
        if (_pageSize == null){
            _pageSize = "10";
        }
        Integer pageSize = Integer.valueOf(_pageSize);
        return Result.success(notificationApi.listByPageAndUserIdAndStatus(user.getSno(), status, pageNum, pageSize));
    }

    /**
     * 删除Notification
     *
     * @param notificationId
     * @return
     */
    @PostMapping("/delete/{notificationId}")
    public Result<String> deleteById(@PathVariable String notificationId){
        notificationApi.deleteById(notificationId);
        return Result.success("ok");
    }

    /**
     * 清空Notification
     *
     * @param reqParam
     * @param user
     * @return
     */
    @PostMapping("/clear")
    public Result<String> clear(@RequestBody JSONObject reqParam, User user){
        if ( user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        Integer status = reqParam.getInteger("status");
        if (status == null){
            status = -1;
        }
        notificationApi.clearByStatus(user.getSno(), status);
        return Result.success("ok");
    }

    /**
     * 获取详情, 如果未读 则改为已读
     *
     * @param notificationId
     * @return
     */
    @GetMapping("/detail/{notificationId}")
    public Result<Notification> getById(@PathVariable String notificationId){
        Notification notification = notificationApi.getById(notificationId);
        if (notification == null){
            return Result.error(CodeMsg.NOTIFICATION_NOT_EXIST);
        }
        return Result.success(notification);
    }
}
