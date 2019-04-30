package com.whu.common.vo;

import com.whu.common.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostVO extends Post {

    private String username;
    private String avatar;
}
