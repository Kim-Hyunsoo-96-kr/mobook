package com.mb.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.domain.*;
import com.mb.dto.Book.req.BookCommentRequestDto;
import com.mb.dto.Book.req.BookRequestDto;
import com.mb.dto.Util.MessageDto;
import com.mb.dto.Util.NaverResponseDto;
import com.mb.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Tag(name="BookController", description = "책 컨트롤러")
@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final AmazonS3 amazonS3;
    private final BookService bookService;
    private final MemberService memberService;
    @Value("${naver.clientId}")
    public String naverClientId;
    @Value("${naver.clientSecret}")
    public String naverClientSecret;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping("/test")
    public ResponseEntity test(@RequestParam("images") MultipartFile multipartFile){
        try {
            String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(multipartFile.getInputStream().available());

            amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);

            String url = amazonS3.getUrl(bucket, s3FileName).toString();
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 200 : 성공 : 메세지 O
     * 500 : 에러 : 메일 관련 오류 : 메세지 X
     * */
    @Operation(summary = "책 요청", description = "사용자가 원하는 책을 관리자에게 요청합니다.")
    @PostMapping("/request")
    public ResponseEntity bookRequest(@RequestBody BookRequestDto bookRequestDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.request(loginMember, bookRequestDto);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 에러 : 대여 불가능한 경우, 3권을 넘겨서 대여하는 경우: 메세지 O
     * */
    @Operation(summary = "책 대여", description = "해당 책을 대여불가 상태로 DB에 저장합니다.")
    @PostMapping("/rent/{bookNumber}")
    public ResponseEntity bookRent(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.rentBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 이미 추천한 경우 : 메세지 O
     */
    @PostMapping("/recommend/{bookNumber}")
    public ResponseEntity bookRecommend(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.recommendBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 추천한 책이 아닌 경우 : 메세지 O
     * 500 : 로그가 없는 경우 : 메세지 X
     */
    @PostMapping("/recommend/cancel/{bookNumber}")
    public ResponseEntity bookRecommendCancel(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.recommendCancelBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 대여한 책이 아닌 경우 : 메세지 O
     * 500 : 로그가 없는 경우 : 메세지 X
     */
    @Operation(summary = "책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/return/{bookNumber}")
    public ResponseEntity bookReturn(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.returnBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 X, 응답 O
     */
    @Operation(summary = "책 검색", description = "검색값과 일치하는 제목의 책 리스트를 가져옵니다.")
    @GetMapping("/search")
    public ResponseEntity bookSearch(@RequestParam(name = "option")String option, @RequestParam(name = "searchText") String searchText, @RequestParam(name = "page", defaultValue = "1") Integer page){
        return bookService.bookSearch(option, searchText, page);
    }

    /**
     * 200 : 성공 : 메세지 X, 응답 O
     */
    @Operation(summary = "최신 등록 책", description = "최신 등록 책 5개를 가져옵니다.")
    @GetMapping("/recentBookList")
    public ResponseEntity recentBookList(){
        return bookService.getRecentBookList();
    }

    @Operation(summary = "대시보드", description = "대시보드에서 보여줄 리스트들을 가져옵니다.")
    @GetMapping("/dashboard")
    public ResponseEntity dashboard(){
        return bookService.getDashboardList();
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 대여 중인 책이 아닌 경우 : 메세지 O
     */
    @PostMapping("/extend/{bookNumber}")
    public ResponseEntity extendPeriod(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.extendPeriod(loginMember, bookNumber);
    }

    @Operation(summary = "책 댓글 달기", description = "책에 댓글을 달 수 있습니다.")
    @PostMapping("/comment/{bookNumber}")
    public ResponseEntity bookComment(@PathVariable String bookNumber, @RequestBody BookCommentRequestDto bookCommentRequestDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.comment(loginMember, bookNumber, bookCommentRequestDto);
    }

    @Operation(summary = "책 댓글 수정", description = "책 댓글을 수정합니다.")
    @PostMapping("/comment/edit/{commentId}")
    public ResponseEntity editComment(@PathVariable Long commentId, @RequestBody BookCommentRequestDto bookCommentRequestDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.editComment(loginMember, commentId, bookCommentRequestDto);
    }

    @Operation(summary = "책 댓글 삭제", description = "책 댓글을 삭제합니다.")
    @PostMapping("/comment/delete/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commentId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.deleteComment(loginMember, commentId);
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
