package com.mb.service;

import com.mb.domain.*;
import com.mb.dto.Book.req.RefreshTokenDto;
import com.mb.dto.Book.resp.BookLogResponseDto;
import com.mb.dto.Book.resp.BookRecommendLogResponseDto;
import com.mb.dto.Book.resp.BookRentLogResponseDto;
import com.mb.dto.Book.resp.BookRequestLogResponseDto;
import com.mb.dto.Member.req.ChangePasswordDto;
import com.mb.dto.Member.req.FindPasswordDto;
import com.mb.dto.Member.req.MemberLoginDto;
import com.mb.dto.Member.req.MemberSignUpDto;
import com.mb.dto.Member.resp.MemberLoginResponseDto;
import com.mb.dto.Member.resp.MemberSignUpResponseDto;
import com.mb.dto.Util.MessageDto;
import com.mb.dto.Util.SecretRequestDto;
import com.mb.repository.BookRepository;
import com.mb.repository.BookRequestRepository;
import com.mb.repository.MemberRepository;
import com.mb.util.BookLogUtil;
import com.mb.util.JwtUtil;
import com.mb.util.RentBookLog;
import com.mb.util.RequestBookLog;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.util.*;

import static com.mb.enum_.BookStatus.InRental;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final BookRecommendService bookRecommendService;
    private final BookLogService bookLogService;
    private final BookRepository bookRepository;
    private final MailService mailService;
    private final BookRequestRepository bookRequestRepository;
    private final BookRequestService bookRequestService;

    @Value("${jwt.secretKey}")
    public String accessSecretKey;
    @Value("${jwt.refreshKey}")
    public String refreshSecretKey;
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    @Transactional
    public MemberSignUpResponseDto addMember(MemberSignUpDto memberSignUpDto) {
        String name = memberSignUpDto.getName();
        String email = memberSignUpDto.getEmail();
        String password = memberSignUpDto.getPassword();

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setIsAdmin(false);
        member.setName(name);

        memberRepository.save(member);

        MemberSignUpResponseDto memberSignUpResponseDto = new MemberSignUpResponseDto();

        memberSignUpResponseDto.setEmail(member.getEmail());
        memberSignUpResponseDto.setName(member.getName());

        return memberSignUpResponseDto;
    }

    public Member findByEmail(String email) {
        Member findMember = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사용자입니다."));
        return findMember;
    }

    public Member findById(long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사용자입니다."));
        return findMember;
    }

    public String[] findAllMemberMailReceiveArray() {
        List<Member> allMemberList = memberRepository.findAll();
        List<String> emailList = new ArrayList();
        for (Member member : allMemberList) {
            String email = member.getEmail();
            emailList.add(email);
        }
        String[] receiverList = emailList.toArray(new String[0]);
        return receiverList;
    }

    public String[] findAllAdminMailReceiveArray() {
        List<Member> allAdminList = memberRepository.findByIsAdmin(true);
        List<String> emailList = new ArrayList();
        for (Member member : allAdminList) {
            String email = member.getEmail();
            emailList.add(email);
        }
        String[] receiverList = emailList.toArray(new String[0]);
        return receiverList;
    }

    public String[] findAllMember() {
        List<Member> allMemberList = memberRepository.findAll();
        List<String> emailList = new ArrayList();
        for (Member member : allMemberList) {
            String email = member.getEmail();
            emailList.add(email);
        }
        String[] receiverList = emailList.toArray(new String[0]);
        return receiverList;
    }

    @Transactional
    public ResponseEntity findPassword(FindPasswordDto findPasswordDto) {
        MessageDto messageDto = new MessageDto();
        try{
            Member member =  memberRepository.findByEmailAndName(findPasswordDto.getEmail(), findPasswordDto.getName()).orElseThrow(()->new IllegalArgumentException("일치하는 회원이 없습니다."));
            String newPassword = generateRandomPassword();
            member.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            messageDto.setMessage("이메일로 새로운 비밀번호를 발송했습니다.");
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    String[] receiveArray = {member.getEmail()};
                    Map<String, Object> model = new HashMap<>();
                    model.put("newPassword", newPassword);
                    try {
                        mailService.sendHtmlEmail(receiveArray, "[MOBOOK1.0]새로운 비밀번호 안내", "findPassword.html", model);
                    } catch (Exception e) {
                        messageDto.setMessage("메일 발송 관련 오류");
                    }
                }
            });
        } catch (IllegalArgumentException e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    public static String generateRandomPassword() {
        String characters = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
        int minLength = 8;
        int maxLength = 20;

        SecureRandom random = new SecureRandom();
        int passwordLength = random.nextInt(maxLength - minLength + 1) + minLength;

        List<Character> passwordChars = new ArrayList<>();
        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            passwordChars.add(characters.charAt(randomIndex));
        }

        StringBuilder password = new StringBuilder();
        for (Character character : passwordChars) {
            password.append(character);
        }

        return password.toString();
    }

    @Transactional
    public ResponseEntity login(MemberLoginDto memberLoginDto) {
        MessageDto messageDto = new MessageDto();

        // 입력받은 이메일로 멤버를 찾을 수 없는 경우
        try{
            Member findMember = findByEmail(memberLoginDto.getEmail());
            if (!passwordEncoder.matches(memberLoginDto.getPassword(), findMember.getPassword())) {
                messageDto.setMessage("비밀번호가 일치하지 않습니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
            String accessToken = JwtUtil.createAccessToken(findMember, accessSecretKey);
            String refreshToken = JwtUtil.createRefreshToken(findMember, refreshSecretKey);

            RefreshToken saveRefreshToken = new RefreshToken();
            saveRefreshToken.setMemberId(findMember.getMemberId());
            saveRefreshToken.setValue(refreshToken);
            refreshTokenService.addToken(saveRefreshToken);

            MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto();
            memberLoginResponseDto.setName(findMember.getName());
            memberLoginResponseDto.setAccessToken(accessToken);
            memberLoginResponseDto.setRefreshToken(refreshToken);

            return new ResponseEntity(memberLoginResponseDto, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }



    }

    public ResponseEntity joinSecret(SecretRequestDto secretRequestDto){
        MessageDto messageDto = new MessageDto();
        if(passwordEncoder.matches(secretRequestDto.getSecretKey(),passwordEncoder.encode("9146"))){
            String name = secretRequestDto.getName();
            String email = secretRequestDto.getEmail();
            String password = secretRequestDto.getPassword();
            Member member = new Member();
            member.setEmail(email);
            member.setPassword(passwordEncoder.encode(password));
            member.setIsAdmin(true);
            member.setName(name);
            member.setRentalBookQuantity(0);

            memberRepository.save(member);

            MemberSignUpResponseDto memberSignUpResponseDto = new MemberSignUpResponseDto();

            memberSignUpResponseDto.setEmail(member.getEmail());
            memberSignUpResponseDto.setName(member.getName());

            messageDto.setMessage("회원가입이 완료되었습니다.");
            return new ResponseEntity(messageDto, HttpStatus.CREATED);
        }
        else {
            messageDto.setMessage("땡");
            return new ResponseEntity(messageDto, HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity join(Member loginMember, MemberSignUpDto memberSignUpDto) {
        if(loginMember.getIsAdmin()){
            String name = memberSignUpDto.getName();
            String email = memberSignUpDto.getEmail();
            String password = memberSignUpDto.getPassword();
            Member member = new Member();
            member.setEmail(email);
            member.setPassword(passwordEncoder.encode(password));
            member.setIsAdmin(false);
            member.setName(name);
            member.setRentalBookQuantity(0);

            memberRepository.save(member);

            MemberSignUpResponseDto memberSignUpResponseDto = new MemberSignUpResponseDto();

            memberSignUpResponseDto.setEmail(member.getEmail());
            memberSignUpResponseDto.setName(member.getName());

            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("회원가입이 완료되었습니다.");
            return new ResponseEntity(messageDto, HttpStatus.CREATED);
        } else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("관리자가 아닙니다.");
            return new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public ResponseEntity refreshToken(RefreshTokenDto refreshTokenDto) {
        MessageDto messageDto = new MessageDto();
        try{
            RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenDto.getRefreshToken());
            Long memberId = JwtUtil.getMemberId(refreshToken.getValue(), refreshSecretKey);
            Member member = findById(memberId);
            String accessToken = JwtUtil.createAccessToken(member, accessSecretKey);

            MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto();
            memberLoginResponseDto.setName(member.getName());
            memberLoginResponseDto.setAccessToken(accessToken);
            memberLoginResponseDto.setRefreshToken(refreshToken.getValue());
            return new ResponseEntity(memberLoginResponseDto, HttpStatus.OK);
        } catch(IllegalArgumentException e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity logout(RefreshTokenDto refreshTokenDto) {
        MessageDto messageDto = new MessageDto();
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        messageDto.setMessage("로그아웃 하셨습니다.");
        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    public ResponseEntity myRentBook(Member loginMember) {
        BookRentLogResponseDto bookRentLogResponseDto = new BookRentLogResponseDto();
        List<RentBookLog> rentBookLogList = new ArrayList();
        List<BookLog> bookInRendtalLogList =  bookLogService.findByMemberAndStatus(loginMember, InRental);
        Integer totalCnt = bookInRendtalLogList.size();
        for (BookLog bookLog : bookInRendtalLogList) {
            Book rentBook = bookLog.getBook();
            RentBookLog rentBookLog = new RentBookLog(rentBook.getBookNumber(), rentBook.getBookLink(),
                    rentBook.getBookImageUrl(), rentBook.getBookName(),rentBook.getRecommend(), bookLog.getRegDate(), bookLog.getReturnDate());
            rentBookLogList.add(rentBookLog);
        }
        bookRentLogResponseDto.setRentBook(rentBookLogList);
        bookRentLogResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(bookRentLogResponseDto, HttpStatus.OK);
    }

    public ResponseEntity myBookLog(Member loginMember, String searchText, Integer page) {
        BookLogResponseDto bookLogResponseDto = new BookLogResponseDto();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        List<BookLog> bookLogList = bookLogService.findBookLogByMemberAndKeyword(loginMember, searchText, pageable.withPage(page));
        Integer totalCnt = bookLogService.getBookLogByMemberAndKeywordCnt(loginMember,searchText);
        List<BookLogUtil> bookLogUtilList = new ArrayList();
        for (BookLog bookLog : bookLogList) {
            String status = bookLog.getStatus();
            Book book = bookRepository.findById(bookLog.getBook().getBookId()).orElseThrow(() -> new IllegalArgumentException("등로되지 않은 책입니다."));
            String bookName = book.getBookName();
            String bookLink = book.getBookLink();
            String bookNumber = book.getBookNumber();
            String bookImageUrl = book.getBookImageUrl();
            String regDate = bookLog.getRegDate();
            BookLogUtil bookLogUtil = new BookLogUtil(bookName, bookLink, bookImageUrl, bookNumber, status, regDate);
            bookLogUtilList.add(bookLogUtil);
        }
        bookLogResponseDto.setBookLogList(bookLogUtilList);
        bookLogResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(bookLogResponseDto, HttpStatus.OK);
    }

    public ResponseEntity myRecommendBook(Member loginMember, String searchText, Integer page) {
        BookRecommendLogResponseDto bookRecommendLogResponseDto = new BookRecommendLogResponseDto();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        List<BookRecommend> bookRecommendList = bookRecommendService.findByMemberAndKeyword(loginMember, searchText, pageable.withPage(page));
        Integer totalCnt = bookRecommendService.getBookRecommendListByMemberAndKeywordCnt(loginMember,searchText);
        List<Book> recommendBookList = new ArrayList();
        for (BookRecommend bookRecommend : bookRecommendList) {
            Book book = bookRecommend.getBook();
            recommendBookList.add(book);
        }
        bookRecommendLogResponseDto.setRecommendBook(recommendBookList);
        bookRecommendLogResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(bookRecommendLogResponseDto, HttpStatus.OK);
    }

    public ResponseEntity myRequestBook(Member loginMember, String searchText, Integer page) {
        BookRequestLogResponseDto bookRequestLogResponseDto =  new BookRequestLogResponseDto();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        List<BookRequest> requestBookList =  bookRequestService.findBookRequestListByMemberAndKeyword(loginMember, searchText, pageable.withPage(page));
        Integer totalCnt = bookRequestService.getBookRequestListByMemberAndKeywordCnt(loginMember,searchText);
        List<RequestBookLog> requestBookLogList = new ArrayList();
        for (BookRequest bookRequest : requestBookList) {
            String bookName = bookRequest.getBookName();
            String bookLink = bookRequest.getBookLink();
            String requestDate = bookRequest.getRegDate();
            String completeDate = bookRequest.getCompleteDate();
            String status = bookRequest.getStatus();
            RequestBookLog requestBookLog = new RequestBookLog(bookName, bookLink, requestDate, completeDate, status);
            requestBookLogList.add(requestBookLog);
        }
        bookRequestLogResponseDto.setRequestBookLogList(requestBookLogList);
        bookRequestLogResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(bookRequestLogResponseDto, HttpStatus.OK);
    }

    public ResponseEntity changePw(Member loginMember, ChangePasswordDto changePasswordDto) {
        MessageDto messageDto = new MessageDto();
        if(passwordEncoder.matches(changePasswordDto.getOldPassword(), loginMember.getPassword())){
            if(changePasswordDto.getNewPassword().equals(changePasswordDto.getCheckNewPassword())){
                loginMember.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
                memberRepository.save(loginMember);
                messageDto.setMessage("비밀번호를 변경했습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("새로운 비밀번호를 다시 확인해주세요.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        } else{
            messageDto.setMessage("기존 비밀번호와 일치하지 않습니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}
