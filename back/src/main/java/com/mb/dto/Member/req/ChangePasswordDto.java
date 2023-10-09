package com.mb.dto.Member.req;

import lombok.Getter;

@Getter

public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
    private String checkNewPassword;
}
