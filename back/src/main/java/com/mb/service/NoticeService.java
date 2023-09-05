package com.mb.service;

import com.mb.domain.Book;
import com.mb.domain.BookComment;
import com.mb.domain.Member;
import com.mb.domain.Notice;
import com.mb.dto.BookListResponseDto;
import com.mb.dto.MessageDto;
import com.mb.dto.NoticeAddRequestDto;
import com.mb.dto.NoticeListResponseDto;
import com.mb.repository.BookCommentRepository;
import com.mb.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final MailService mailService;
    private final MemberService memberService;
    private List<Notice> getListWithPage(Pageable pageable) {
        List<Notice> noticeList = (List<Notice>) noticeRepository.findAll(pageable);
        return noticeList;
    }
    @Transactional
    public ResponseEntity noticeAdd(NoticeAddRequestDto noticeAddRequestDto, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
            Notice notice = new Notice();
            notice.setTitle(noticeAddRequestDto.getTitle());
            notice.setContents(notice.getContents());
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            notice.setRegDate(today.format(formatter));
            notice.setMemberId(loginMember.getMemberId());
            noticeRepository.save(notice);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    String[] receiveArray =  memberService.findAllMember();
                    Map<String, Object> model = new HashMap<>();
                    try {
                        mailService.sendHtmlEmail(receiveArray, "[MOBOOK1.1]공지사항 등록", "NoticeAddTemplate.html", model);
                    }  catch (Exception e) {
                        throw new IllegalArgumentException("메일 발송 관련 오류");
                    }
                }
            });

            messageDto.setMessage("공지사항 등록 성공");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("관리자만 공지사항을 등록할 수 있습니다.");
            return new ResponseEntity(messageDto, HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity getNoticeList(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("bookId").descending());
        List<Notice> noticeList = getListWithPage(pageable.withPage(page));
        Integer totalCnt = noticeList.size();

        NoticeListResponseDto noticeListResponseDto = new NoticeListResponseDto();
        noticeListResponseDto.setNoticeList(noticeList);
        noticeListResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(noticeListResponseDto, HttpStatus.OK);
    }

}
