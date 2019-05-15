package com.whu.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.whu.common.entity.Notification;
import com.whu.common.entity.Post;
import com.whu.common.entity.User;
import com.whu.common.entity.UserVisitAction;
import com.whu.common.exception.GlobalException;
import com.whu.common.redis.RedisService;
import com.whu.common.redis.UserKey;
import com.whu.common.result.CodeMsg;
import com.whu.common.result.Result;
import com.whu.common.vo.PostVO;
import com.whu.post.api.PostApi;
import com.whu.user.api.UserVisitActionApi;
import javafx.geometry.Pos;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Reference
    private PostApi postApi;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisService redisService;



    /**
     * 发布Post
     *
     * @param post
     * @return
     */
    @PostMapping
    public Result<Post> create(@RequestBody Post post, User user) {
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        if (post == null){
            return Result.error(CodeMsg.REQ_PARAM_EMPTY);
        }
        post.setPoster(user.getSno());
        return Result.success(postApi.create(post));
    }

    /**
     * 上传Post图片
     *
     * @param postId
     * @param image1
     * @param image2
     * @param image3
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadImages")
    public Result<Post> uploadImages(@RequestParam("postId") String postId, @RequestParam(value = "image1", required = false)MultipartFile image1, @RequestParam(value = "image2", required = false)MultipartFile image2,@RequestParam(value = "image3", required = false)MultipartFile image3) throws IOException {
        Map<String, byte[]> images = new HashMap<>();
        //1、取文件的扩展名
        if (image1 != null){
            String originalFilename = image1.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            images.put(extName, image1.getBytes());
        }
        if (image2 != null){
            String originalFilename = image2.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            images.put(extName, image2.getBytes());
        }
        if (image3 != null){
            String originalFilename = image3.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            images.put(extName, image3.getBytes());
        }
        Post post = postApi.uploadImages(postId, images);
        if (post == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        return Result.success(post);
    }

    /**
     * 查看Post详细信息
     *
     * @param postId
     * @param user
     * @return
     */
    @GetMapping("/detail/{postId}")
    public Result<PostVO> getDetailByPostId(@PathVariable(required = true) String postId, User user){
        String userId = null;
        if (user != null){
            userId = user.getSno();
        }
        PostVO postVO = postApi.getVOById(postId, userId);
        if (postVO == null){
            return Result.error(CodeMsg.POST_NOT_EXIST);
        }
        return Result.success(postVO);
    }

    /**
     * 更新Post
     *
     * @param post
     * @return
     */
    @PostMapping("/update")
    public Result<Post> update(@RequestBody Post post, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // TODO: 19-5-1 判断 userid = poster
        if (post == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        return Result.success(postApi.update(post));
    }

    /**
     * 删除Post
     *
     * @param postId
     * @return
     */
    @PostMapping("/delete/{postId}")
    public Result<String> deleteByPostId(@PathVariable String postId, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // TODO: 19-5-1 判断userid == poster
        postApi.remove(postId);
        return Result.success("ok");
    }

    /**
     * 清空Post
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
        postApi.clear(user.getSno(), status);
        return Result.success("ok");
    }

    /**
     * 列举当前用户的Post
     *
     * @param user
     * @param request
     * @return
     */
    @GetMapping("/list/me")
    public Result<PageInfo<PostVO>> listByUserId(User user, HttpServletRequest request){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
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
        return Result.success(postApi.listByUserId(user.getSno(), pageNum, pageSize));
    }

    /**
     * 分页列举post
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public Result<PageInfo<PostVO>> list(HttpServletRequest request){

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
        return Result.success(postApi.listByPage( pageNum, pageSize));
    }


    /**
     * 搜索
     *
     * @param request
     * @param user
     * @return
     */
    @GetMapping("/search")
    public Result<PageInfo<PostVO>> search(HttpServletRequest request, User user){
        String userId = null;
        if (user != null){
            userId = user.getSno();
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
        String keyword = request.getParameter("keyword");
        return Result.success(StringUtils.isEmpty(keyword) ? postApi.listByPage(pageNum, pageSize) : postApi.search(keyword, pageNum, pageSize, userId));
    }

    /**
     * 通过category, area, startTime筛选post
     *
     * @param request
     * @return
     */
    @GetMapping("/filter")
    public Result<PageInfo<PostVO>> filter(HttpServletRequest request){
        // TODO: 19-5-4 加入日志
        String _category = request.getParameter("category");
        if (_category == null){
            _category = "-1";
        }
        Integer category = Integer.valueOf(_category);

        String _area = request.getParameter("area");
        if (_area == null){
            _area = "-1";
        }
        Integer area = Integer.valueOf(_area);

        String time = request.getParameter("startTime");
        Timestamp startTime;
        if (time == null){
            startTime = null;
        }
        else {
            // yyyy-hh-mm hh:mm:ss
            startTime = new Timestamp(Long.valueOf(time));
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
        PageInfo<PostVO> pageInfo = postApi.listByCategoryAndAreaAndStartTime(category, area, startTime, pageNum, pageSize);
        return Result.success(pageInfo);
    }




    /**
     * 更新申请状态
     *
     * @param reqParam
     * @return
     */
    @PostMapping("/handleApplication")
    public Result<String> handleApplication(@RequestBody JSONObject reqParam, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        String applicationId = reqParam.getString("applicationId");
        Integer status = reqParam.getInteger("status");
        if (applicationId == null || status == null){
            return Result.error(CodeMsg.REQ_PARAM_EMPTY);
        }
        Notification notification = postApi.handleApplication(applicationId, status);

        if (notification != null && redisService.exists(UserKey.getById, notification.getReceiver())) {
            // 发送通知
            messagingTemplate.convertAndSendToUser(notification.getReceiver(), "/queue/notify", notification);
        }

        return Result.success("ok");
    }

    /**
     * 列出post所有成员
     *
     * @param request
     * @return
     */
    @GetMapping("/listMember")
    public Result<PageInfo<User>> listMember(HttpServletRequest request){
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
        return Result.success(postApi.listMember(postId, pageNum, pageSize));
    }

    /**
     * 成员退出Post 如果是自己主动退出, memberId可为空, 如果是poster踢出, memberId必须有值.
     *
     * @param reqParam
     * @param user
     * @return
     */
    @PostMapping("/quit")
    public Result<String> quit(@RequestBody JSONObject reqParam, User user){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String memberId = reqParam.getString("memberId");
        if (memberId == null){
            memberId = user.getSno();
        }

        String postId = reqParam.getString("postId");
        if (postId == null){
            return Result.error(CodeMsg.REQ_PARAM_EMPTY);
        }
        postApi.quit(memberId, postId);
        return Result.success("ok");
    }

    /**
     * 同步数据库和索引库
     *
     * @return
     */
    @GetMapping("/synchronizeDBAndIndexDB")
    public Result<String> synchronize(){
        postApi.synchronizeDBAndIndexDB();
        return Result.success("ok");
    }
}
