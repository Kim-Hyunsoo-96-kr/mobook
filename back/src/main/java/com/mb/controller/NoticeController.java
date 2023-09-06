package com.mb.controller;

import com.mb.domain.Member;
import com.mb.dto.Notice.req.NoticeAddRequestDto;
import com.mb.service.MemberService;
import com.mb.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name="NoticeController", description = "공지 컨트롤러")
@Controller
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    private final MemberService memberService;

    @Operation(summary = "공지사항 등록", description = "공지사항을 등록합니다.")
    @PostMapping("/add")
    public ResponseEntity noticeAdd(@RequestBody NoticeAddRequestDto noticeAddRequestDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return noticeService.noticeAdd(noticeAddRequestDto, loginMember);
    }

    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다.")
    @PostMapping("/edit/{noticeId}")
    public ResponseEntity noticeEdit(@RequestBody NoticeAddRequestDto noticeAddRequestDto, Authentication authentication, @PathVariable Long noticeId){
        Member loginMember = getLoginMember(authentication);
        return noticeService.noticeEdit(noticeAddRequestDto, loginMember, noticeId);
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    @PostMapping("/delete/{noticeId}")
    public ResponseEntity noticeDelete(Authentication authentication, @PathVariable Long noticeId){
        Member loginMember = getLoginMember(authentication);
        return noticeService.noticeDelete(loginMember, noticeId);
    }

    @Operation(summary = "공지사항 리스트를 가져옵니다.", description = "공지사항 리스트를 페이지네이션해서 가져옵니다.")
    @GetMapping("/")
    public ResponseEntity notice(@RequestParam(name = "page", defaultValue = "1") Integer page){
        return noticeService.getNoticeList(page);
    }

    @Operation(summary = "공지사항 상세", description = "공지사항 상세 페이지를 가져옵니다.")
    @GetMapping("/{noticeId}")
    public ResponseEntity noticeDetail(@PathVariable Long noticeId){
        return noticeService.getNoticeDetail(noticeId);
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
