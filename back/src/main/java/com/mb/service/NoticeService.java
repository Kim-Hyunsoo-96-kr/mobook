package com.mb.service;

import com.mb.domain.Member;
import com.mb.domain.Notice;
import com.mb.domain.WebHook;
import com.mb.dto.Notice.resp.NoticeDetailResponseDto;
import com.mb.dto.Util.MessageDto;
import com.mb.dto.Notice.req.NoticeAddRequestDto;
import com.mb.dto.Notice.resp.NoticeListResponseDto;
import com.mb.repository.NoticeRepository;
import com.mb.util.WebHookUtil;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final MailService mailService;
    private final MemberService memberService;
    private final WebHookService webHookService;
    private List<Notice> getListWithPage(Pageable pageable) {
        Page<Notice> all = noticeRepository.findAll(pageable);
        List<Notice> noticeList = all.getContent();
        return noticeList;
    }
    private Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(()->new IllegalArgumentException("찾을 수 없는 공지사항입니다."));
    }
    @Transactional
    public ResponseEntity noticeAdd(NoticeAddRequestDto noticeAddRequestDto, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
            Notice notice = new Notice();
            notice.setTitle(noticeAddRequestDto.getTitle());
            notice.setContents(noticeAddRequestDto.getContents());
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            notice.setRegDate(today.format(formatter));
            notice.setEditDate(today.format(formatter));
            notice.setMemberId(loginMember.getMemberId());
            notice.setMemberName(loginMember.getName());
            noticeRepository.save(notice);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    WebHook webHook = webHookService.findById(1L);
                    String body = WebHookUtil.noticeAddHook(notice.getNoticeId());
                    try{
                        webHookService.sendWebHook(webHook, body);
                    } catch (Exception e){
                        throw new IllegalArgumentException("WEBHOOK ERROR");
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
        Pageable pageable = PageRequest.of(page, 10, Sort.by("noticeId").descending());
        List<Notice> noticeList = getListWithPage(pageable.withPage(page));
        Integer totalCnt = noticeList.size();

        NoticeListResponseDto noticeListResponseDto = new NoticeListResponseDto();
        noticeListResponseDto.setNoticeList(noticeList);
        noticeListResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(noticeListResponseDto, HttpStatus.OK);
    }

    public ResponseEntity noticeEdit(NoticeAddRequestDto noticeAddRequestDto, Member loginMember, Long noticeId) {
        MessageDto messageDto = new MessageDto();
        try{
            Notice notice = findById(noticeId);
            if(loginMember.getIsAdmin() && loginMember.getMemberId().equals(notice.getMemberId())){
                notice.setTitle(noticeAddRequestDto.getTitle());
                notice.setContents(noticeAddRequestDto.getContents());
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                notice.setEditDate(today.format(formatter));

                noticeRepository.save(notice);

                messageDto.setMessage("공지사항 수정 성공");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("해당 공지사항을 등록한 관리자만 수정이 가능합니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }

        } catch (IllegalArgumentException e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity noticeDelete(Member loginMember, Long noticeId) {
        MessageDto messageDto = new MessageDto();
        try{
            Notice notice = findById(noticeId);
            if(loginMember.getIsAdmin() && loginMember.getMemberId().equals(notice.getMemberId())){
                noticeRepository.delete(notice);

                messageDto.setMessage("공지사항 삭제 성공");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("해당 공지사항을 등록한 관리자만 삭제가 가능합니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity getNoticeDetail(Long noticeId) {
        MessageDto messageDto = new MessageDto();
        try{
            Notice notice = findById(noticeId);
            NoticeDetailResponseDto noticeDetailResponseDto = new NoticeDetailResponseDto();
            noticeDetailResponseDto.setNotice(notice);
            return new ResponseEntity(noticeDetailResponseDto, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }


    }
}
