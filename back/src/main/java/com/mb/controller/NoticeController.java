package com.mb.controller;

import com.mb.domain.Member;
import com.mb.dto.NoticeAddRequestDto;
import com.mb.service.MemberService;
import com.mb.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name="NoticeController", description = "공지 컨트롤러")
@Controller
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    private final MemberService memberService;

    @PostMapping("/add")
    public ResponseEntity noticeAdd(@RequestBody NoticeAddRequestDto noticeAddRequestDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return noticeService.noticeAdd(noticeAddRequestDto, loginMember);

    }
    private Member getLoginMember(Authentication authentication) {
        if(authentication == null){
            return new Member();
        }
        Long memberId = (Long) authentication.getPrincipal();
        Member loginMember = memberService.findById(memberId);
        return loginMember;
    }
}
