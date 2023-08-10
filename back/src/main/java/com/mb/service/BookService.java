package com.mb.service;

import com.mb.domain.*;
import com.mb.dto.*;
import com.mb.repository.BookLogRepository;
import com.mb.repository.BookRecommendRepository;
import com.mb.repository.BookRepository;
import com.mb.repository.BookRequestRepository;
import com.mb.util.RequestBookLog;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.mb.enum_.BookStatus.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookLogRepository bookLogRepository;
    private final BookRecommendRepository bookRecommendRepository;
    private final BookRequestRepository bookRequestRepository;
    private final MailService mailService;
    private final MemberService memberService;
    public Book saveBook(Book newBook) {
        Book saveBook = bookRepository.save(newBook);
        return saveBook;
    }

    public List<Book> getBooksList() {
        List<Book> bookList = bookRepository.findAll();
        return bookList;
    }

    public Book findById(Long bookId) {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("등로되지 않은 책입니다."));
        return findBook;
    }

    public List<Book> getBookListByKeyword(String keyword, Pageable pageable) {
        List<Book> bookList = bookRepository.findByBookNameContaining(keyword, pageable);
        return bookList;
    }

    public List<Book> findByRentalMemberId(Long memberId) {
        List<Book> bookList = bookRepository.findByRentalMemberId(memberId);
        return bookList;
    }

    public Book findByBookNumber(String bookNumber) {
        Book findBook = bookRepository.findByBookNumber(bookNumber).orElseThrow(()-> new IllegalArgumentException("등로되지 않은 책입니다."));
        return findBook;
    }

    public Integer getTotalCntBySearchText(String searchText) {
        List<Book> bookList = bookRepository.findByBookNameContaining(searchText);
        return bookList.size();
    }

    @Transactional
    public BookAddResponseDto addBook(BookAddDto bookAddDto) {
        Book newBook = new Book();

        newBook.setBookName(bookAddDto.getBookName());
        newBook.setBookNumber(bookAddDto.getBookNumber());
        newBook.setIsAble(true);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        newBook.setRegDate(today.format(formatter));
        newBook.setRecommend(0);
        newBook.setRentalMemberId(0L);

        bookRepository.save(newBook);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                String[] receiveArray =  memberService.findAllMemberMailReceiveArray();
                Map<String, Object> model = new HashMap<>();
                model.put("newBook", newBook);
                try {
                    mailService.sendHtmlEmail(receiveArray, "[MOBOOK1.0]책 추가 안내", "bookAddTemplate.html", model);
                }  catch (MessagingException | IOException e) {
                    throw new IllegalArgumentException("메시지 발송 관련 오류");
                }
            }
        });

        BookAddResponseDto bookAddResponseDto = new BookAddResponseDto();
        bookAddResponseDto.setName(newBook.getBookName());
        return bookAddResponseDto;
    }

    @Transactional
    public MessageDto addBookByExcel(MultipartFile mf) {

        MessageDto messageDto = new MessageDto();
        List<Book> list = new ArrayList<>();
        try{
            OPCPackage opcPackage = OPCPackage.open(mf.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i=1; i<sheet.getLastRowNum() + 1; i++) {

                Book newBook = new Book();

                XSSFRow row = sheet.getRow(i);

                // 행이 존재하지 않으면 패스한다.
                if (null == row) {
                    continue;
                }

                // 행의 첫 번째 열(이름)
                XSSFCell cell = row.getCell(0);
                if (null != cell) {
                    if(cell.getRawValue() != null) newBook.setBookNumber(cell.getRawValue().split("\\.")[0]);
                }

                // 행의 첫 번째 열(이름)
                cell = row.getCell(1);
                if (null != cell) {
                    if(cell.getStringCellValue() != null) newBook.setBookName(cell.getStringCellValue());
                }
                newBook.setIsAble(true);
                Date today = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                newBook.setRegDate(formatter.format(today));
                newBook.setRecommend(0);
                newBook.setRentalMemberId(0L);
                // 리스트에 담는다.
                list.add(newBook);

            }
        } catch (IOException | InvalidFormatException e){
            messageDto.setMessage("엑셀 관련 오류");
        }

        try{
            for (Book book : list) {
                bookRepository.save(book);
            }
            messageDto.setMessage("책 추가 성공");
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    String[] receiveArray =  memberService.findAllMemberMailReceiveArray();
                    Map<String, Object> model = new HashMap<>();
                    model.put("newBookList", list);
                    try {
                        mailService.sendHtmlEmail(receiveArray, "[MOBOOK1.0]책 추가 안내", "bookAddExcelTemplate.html", model);
                    }  catch (MessagingException | IOException e) {
                        throw new IllegalArgumentException("메일 발송 관련 오류");
                    }
                }
            });
        } catch (Exception e){
            messageDto.setMessage("DB관련 오류 : " + e);
        }

        return messageDto;
    }

    @Transactional
    public MessageDto request(Member loginMember, BookRequestDto bookRequestDto) {
        MessageDto messageDto = new MessageDto();
        BookRequest bookRequest = new BookRequest();

        bookRequest.setBookName(bookRequestDto.getBookName());
        bookRequest.setBookWriter(bookRequestDto.getBookWriter());
        bookRequest.setBookPublisher(bookRequestDto.getBookPublisher());
        bookRequest.setStatus(Request.getBookStatus());
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bookRequest.setRegDate(today.format(formatter));
        bookRequest.setCompleteDate("0");
        bookRequest.setMember(loginMember);
        bookRequestRepository.save(bookRequest);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                String[] receiveArray =  memberService.findAllAdminMailReceiveArray();
                Map<String, Object> model = new HashMap<>();
                model.put("member", loginMember);
                model.put("bookRequest", bookRequest);
                try {
                    mailService.sendHtmlEmail(receiveArray, "[MOBOOK1.0]책 신청 안내", "bookRequestTemplate.html", model);
                }  catch (Exception e) {
                    throw new IllegalArgumentException("메일 발송 관련 오류");
                }
            }
        });

        messageDto.setMessage("성공적으로 책을 요청했습니다.");

        return messageDto;
    }

    /**매일 아침 10시에 반납예정인 책을 대여자의 계정 이메일로 발송*/
    @Scheduled(cron = "0 17 18 * * ?")
    public void returnBookMail(){
        System.out.println("이메일 발송");
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate returnDate = today.plusDays(3);
        List<BookLog> bookLogList = bookLogRepository.findByReturnDate(returnDate.format(formatter));
        List<String> emailList = new ArrayList();
        for (BookLog bookLog : bookLogList) {
            String email = bookLog.getMember().getEmail();
            emailList.add(email);
        }
        String[] receiverArray = emailList.toArray(new String[0]);
        Map<String, Object> model = new HashMap<>();
        model.put("bookLogList", bookLogList);
        try {
            mailService.sendHtmlEmail(receiverArray, "[MOBOOK1.0]책 반납 일정 안내", "bookReturnTemplate.html", model);
        }  catch (Exception e) {
            throw new IllegalArgumentException("메일 발송 관련 오류");
        }
    }

    @Transactional
    public MessageDto rentBook(Member loginMember, String bookNumber) {
        MessageDto messageDto = new MessageDto();
        Book book = findByBookNumber(bookNumber);
        if (book.getIsAble()) {
            book.setRentalMemberId(loginMember.getMemberId());
            book.setIsAble(false);
            bookRepository.save(book);

            BookLog bookLog = new BookLog();
            bookLog.setBook(book);
            bookLog.setMember(loginMember);
            bookLog.setStatus(InRental.getBookStatus());
            LocalDate today = LocalDate.now();
            LocalDate twoWeeksLater = today.plusWeeks(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            bookLog.setRegDate(today.format(formatter));
            bookLog.setReturnDate(twoWeeksLater.format(formatter));
            bookLogRepository.save(bookLog);
            messageDto.setMessage("성공적으로 대여했습니다.");
            return messageDto;
        } else {
            messageDto.setMessage("대여할 수 없는 책 입니다.");
            return messageDto;
        }
    }

    public MessageDto recommendBook(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        Optional<BookRecommend> bookRecommend = bookRecommendRepository.findByMemberAndBook(loginMember, book);
        if(bookRecommend.isEmpty()){
            Integer recommendCount = book.getRecommend() + 1;
            book.setRecommend(recommendCount);
            bookRepository.save(book);
            BookRecommend newBookRecommend = new BookRecommend();
            newBookRecommend.setMember(loginMember);
            newBookRecommend.setBook(book);
            bookRecommendRepository.save(newBookRecommend);
            messageDto.setMessage("선택하신 책을 추천했습니다.");
        } else {
            messageDto.setMessage("이미 추천한 책입니다.");
        }
        return messageDto;
    }

    public MessageDto recommendCancelBook(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        Optional<BookRecommend> bookRecommend = bookRecommendRepository.findByMemberAndBook(loginMember, book);
        if(bookRecommend == null){
            messageDto.setMessage("추천한 책이 아닙니다.");
        } else {
            Integer recommendCount = book.getRecommend() - 1;
            book.setRecommend(recommendCount);
            bookRepository.save(book);
            bookRecommendRepository.delete(bookRecommend.orElseThrow(()-> new IllegalArgumentException("존재하지 않는 데이터입니다.")));
            messageDto.setMessage("선택하신 책의 추천을 취소했습니다.");
        }
        return messageDto;
    }

    public MessageDto returnBook(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        if(loginMember.getMemberId() == book.getRentalMemberId()) {
            book.setIsAble(true);
            book.setRentalMemberId(0L);
            bookRepository.save(book);
            BookLog bookLog = new BookLog();
            bookLog.setBook(book);
            bookLog.setMember(loginMember);
            bookLog.setStatus(Return.getBookStatus());
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            bookLog.setRegDate(today.format(formatter));
            bookLog.setReturnDate("0");
            bookLogRepository.save(bookLog);

            BookLog bookHistoryLog = bookLogRepository.findByMemberAndBookAndStatus(loginMember, book, InRental.getBookStatus()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로그입니다."));
            bookHistoryLog.setStatus(Rent.getBookStatus());
            bookHistoryLog.setReturnDate("0");
            bookLogRepository.save(bookHistoryLog);

            messageDto.setMessage("반납을 완료했습니다.");
        }
        else {
            messageDto.setMessage("해당 책을 대여하지 않았습니다.");
        }
        return messageDto;
    }

    public RequestBookLogResponseDto findMyRequesyBookList(Member member) {
        List<BookRequest> requestBookList =  bookRequestRepository.findByMember(member);
        List<RequestBookLog> requestBookLogList = new ArrayList();
        RequestBookLogResponseDto requestBookLogResponseDto = new RequestBookLogResponseDto();
        for (BookRequest bookRequest : requestBookList) {
            String bookName = bookRequest.getBookName();
            String requestDate = bookRequest.getRegDate();
            String completeDate = bookRequest.getCompleteDate();
            String status = bookRequest.getStatus();
            RequestBookLog requestBookLog = new RequestBookLog(bookName, requestDate, completeDate, status);
            requestBookLogList.add(requestBookLog);
        }
        requestBookLogResponseDto.setRequestBookLogList(requestBookLogList);
        return requestBookLogResponseDto;
    }

    public MessageDto extendPeriod(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        Optional<BookLog> bookLog = bookLogRepository.findByMemberAndBookAndStatus(loginMember, book, InRental.getBookStatus());
        if(!bookLog.isEmpty()){
            BookLog log = bookLog.get();
            LocalDate today = LocalDate.now();
            LocalDate twoWeeksLater = today.plusWeeks(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            log.setReturnDate(twoWeeksLater.format(formatter));
            bookLogRepository.save(log);
            messageDto.setMessage("대출 기한이 연장되었습니다.");
        } else {
            messageDto.setMessage("대여 중인 책이 아닙니다.");
        }
        return messageDto;
    }
}
