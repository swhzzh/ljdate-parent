package com.whu.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {

    @Length(min = 13, max = 13)
    private String sno;
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private String password;
    private String phoneNo;
    @Email
    private String email;
    private Integer auth;
    private String avatar;
    private Integer credit;
    private String createTime;
    private String updateTime;
    private Integer valid;
}
