package com.mb.service;

import com.mb.domain.BookComment;
import com.mb.domain.Member;
import com.mb.domain.Notice;
import com.mb.dto.MessageDto;
import com.mb.dto.NoticeAddRequestDto;
import com.mb.repository.BookCommentRepository;
import com.mb.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public ResponseEntity noticeAdd(NoticeAddRequestDto noticeAddRequestDto, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
            Notice notice = new Notice();
            notice.setTitle(noticeAddRequestDto.getTitle());
            notice.setContents(notice.getContents());
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            notice.setRegDate(today.format(formatter));
            notice.setMember(loginMember);
            noticeRepository.save(notice);

            messageDto.setMessage("공지사항 등록 성공");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("관리자만 공지사항을 등록할 수 있습니다.");
            return new ResponseEntity(messageDto, HttpStatus.UNAUTHORIZED);
        }
    }
}
