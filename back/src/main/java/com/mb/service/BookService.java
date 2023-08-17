package com.mb.service;

import com.mb.domain.*;
import com.mb.dto.*;
import com.mb.repository.*;
import com.mb.util.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final MemberRepository memberRepository;
    private final BookRecommendRepository bookRecommendRepository;
    private final BookRequestRepository bookRequestRepository;
    private final MailService mailService;
    private final MemberService memberService;
    private final BookRequestService bookRequestService;
    private final BookLogService bookLogService;
    public Book saveBook(Book newBook) {
        Book saveBook = bookRepository.save(newBook);
        return saveBook;
    }

    public List<Book> getBooksList() {
        List<Book> bookList = bookRepository.findAll();
        return bookList;
    }

    public Book findById(Long bookId) {
        Book findBook = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 책입니다."));
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
        Book findBook = bookRepository.findByBookNumber(bookNumber).orElseThrow(()-> new IllegalArgumentException("등록되지 않은 책입니다."));
        return findBook;
    }

    public Integer getTotalCntBySearchText(String searchText) {
        List<Book> bookList = bookRepository.findByBookNameContaining(searchText);
        return bookList.size();
    }

    @Transactional
    public ResponseEntity addBook(BookAddDto bookAddDto, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
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
                    String[] receiveArray = memberService.findAllMemberMailReceiveArray();
                    Map<String, Object> model = new HashMap<>();
                    model.put("newBook", newBook);
                    try {
                        mailService.sendHtmlEmail(receiveArray, "[MOBOOK1.0]책 추가 안내", "bookAddTemplate.html", model);
                    } catch (MessagingException | IOException e) {
                        throw new IllegalArgumentException("메시지 발송 관련 오류");
                    }
                }
            });
            messageDto.setMessage("책 추가 성공!");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }

    }

    @Transactional
    public ResponseEntity addBookByExcel(MultipartFile mf, Member loginMember) {

        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
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
                        messageDto.setMessage("비어있는 값이 있습니다.");
                        return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
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
            } catch (Exception e){
                messageDto.setMessage("엑셀 관련 오류");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }

    }

    @Transactional
    public ResponseEntity request(Member loginMember, BookRequestDto bookRequestDto) {
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

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    /**매일 아침 10시에 반납예정인 책을 대여자의 계정 이메일로 발송*/
    @Scheduled(cron = "0 0 10 * * ?")
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
    public ResponseEntity rentBook(Member loginMember, String bookNumber) {
        MessageDto messageDto = new MessageDto();
        Book book = findByBookNumber(bookNumber);
        if (book.getIsAble()) {
            book.setRentalMemberId(loginMember.getMemberId());
            book.setIsAble(false);
            bookRepository.save(book);

            if(loginMember.getRentalBookQuantity() > 2){
                messageDto.setMessage("최대 대여 수는 3권입니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            } else {
                Integer rentalBookQuantity = loginMember.getRentalBookQuantity() + 1;
                loginMember.setRentalBookQuantity(rentalBookQuantity);
                memberRepository.save(loginMember);
            }

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
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("대여할 수 없는 책 입니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity recommendBook(Member loginMember, String bookNumber) {
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
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("이미 추천한 책입니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity recommendCancelBook(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        Optional<BookRecommend> bookRecommend = bookRecommendRepository.findByMemberAndBook(loginMember, book);
        if(bookRecommend == null){
            messageDto.setMessage("추천한 책이 아닙니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        } else {
            Integer recommendCount = book.getRecommend() - 1;
            book.setRecommend(recommendCount);
            bookRepository.save(book);
            bookRecommendRepository.delete(bookRecommend.orElseThrow(()-> new IllegalArgumentException("존재하지 않는 데이터입니다.")));
            messageDto.setMessage("추천을 취소했습니다.");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        }
    }

    public ResponseEntity adminReturnBook(Member loginMember, String bookNumber) {
        if(loginMember.getIsAdmin()){
            Book book = findByBookNumber(bookNumber);
            MessageDto messageDto = new MessageDto();
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

                Integer rentalBookQuantity = loginMember.getRentalBookQuantity() - 1;
                loginMember.setRentalBookQuantity(rentalBookQuantity);
                memberRepository.save(loginMember);

                BookLog bookHistoryLog = bookLogRepository.findByBookAndStatus(book, InRental.getBookStatus()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로그입니다."));
                bookHistoryLog.setStatus(Rent.getBookStatus());
                bookHistoryLog.setReturnDate("0");
                bookLogRepository.save(bookHistoryLog);

                messageDto.setMessage("반납을 완료했습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public ResponseEntity returnBook(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        if(loginMember.getMemberId().equals(book.getRentalMemberId())) {
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

            Integer rentalBookQuantity = loginMember.getRentalBookQuantity() - 1;
            loginMember.setRentalBookQuantity(rentalBookQuantity);
            memberRepository.save(loginMember);

            BookLog bookHistoryLog = bookLogRepository.findByMemberAndBookAndStatus(loginMember, book, InRental.getBookStatus()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로그입니다."));
            bookHistoryLog.setStatus(Rent.getBookStatus());
            bookHistoryLog.setReturnDate("0");
            bookLogRepository.save(bookHistoryLog);

            messageDto.setMessage("반납을 완료했습니다.");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        }
        else {
            messageDto.setMessage("해당 책을 대여하지 않았습니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
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

    public ResponseEntity extendPeriod(Member loginMember, String bookNumber) {
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
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("대여 중인 책이 아닙니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity bookSearch(String searchText, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("bookId").descending());
        List<Book> bookList = getBookListByKeyword(searchText, pageable.withPage(page));
        Integer totalCnt = getTotalCntBySearchText(searchText);

        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        bookListResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
    }

    public ResponseEntity requestComplete(Member loginMember, Long bookRequestId) {
        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
            BookRequest bookRequest = bookRequestService.findById(bookRequestId);
            if(bookRequest.getStatus().equals(Request.getBookStatus())){
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                bookRequest.setCompleteDate(today.format(formatter));
                bookRequest.setStatus(RequestComplete.getBookStatus());
                bookRequestService.save(bookRequest);
                messageDto.setMessage("성공적으로 처리완료했습니다.");

                return new ResponseEntity(messageDto, HttpStatus.OK);
            }
            else {
                messageDto.setMessage("이미 처리완료한 요청 건 입니다.");
                return  new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        }
        else {
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public ResponseEntity bookLog(Member loginMember, String searchText, Integer page) {
        if(loginMember.getIsAdmin()){
            AdminBookLogResponseDto adminBookLogResponseDto = new AdminBookLogResponseDto();
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            List<BookLog> bookLogList = bookLogService.findAllBookLogByKeyword(searchText, pageable.withPage(page));
            Integer totalCnt = bookLogService.getAllBookLogByKeywordCnt(searchText);
            List<BookLogAdminUtil> bookLogAdminUtilList = new ArrayList();
            for (BookLog bookLog : bookLogList) {
                String status = bookLog.getStatus();
                String bookName = bookRepository.findById(bookLog.getBook().getBookId()).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 책입니다.")).getBookName();
                String bookNumber = bookRepository.findById(bookLog.getBook().getBookId()).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 책입니다.")).getBookNumber();
                String regDate = bookLog.getRegDate();
                String userName = bookLog.getMember().getName();
                BookLogAdminUtil bookLogAdminUtil = new BookLogAdminUtil(bookName, bookNumber, status, regDate, userName);
                bookLogAdminUtilList.add(bookLogAdminUtil);
            }
            adminBookLogResponseDto.setBookLogList(bookLogAdminUtilList);
            adminBookLogResponseDto.setTotalCnt(totalCnt);
            return new ResponseEntity(adminBookLogResponseDto, HttpStatus.OK);
        } else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public ResponseEntity rentBookLog(Member loginMember, String searchText, Integer page) {
        if(loginMember.getIsAdmin()){
            RentBookAdminLogResponseDto rentBookAdminLogResponseDto = new RentBookAdminLogResponseDto();
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            List<RentBookAdminLog> rentBookAdminLogList = new ArrayList();
            List<BookLog> bookInRendtalLogList =  bookLogService.findRentalBookLogByStatusAndKeyword(InRental, searchText, pageable.withPage(page));
            Integer totalCnt = bookLogService.getRentalBookLogByStatusAndKeywordCnt(InRental, searchText);
            for (BookLog bookLog : bookInRendtalLogList) {
                Book rentBook = bookLog.getBook();
                RentBookAdminLog rentBookAdminLog = new RentBookAdminLog(rentBook.getBookNumber(), rentBook.getBookName(),
                        rentBook.getRecommend(), bookLog.getRegDate(), bookLog.getReturnDate(), bookLog.getMember().getName());
                rentBookAdminLogList.add(rentBookAdminLog);
            }
            rentBookAdminLogResponseDto.setRentBook(rentBookAdminLogList);
            rentBookAdminLogResponseDto.setTotalCnt(totalCnt);
            return new ResponseEntity(rentBookAdminLogResponseDto, HttpStatus.OK);
        } else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public ResponseEntity requestBookLog(Member loginMember, String searchText, Integer page) {
        if(loginMember.getIsAdmin()){
            RequestBookAdminLogResponseDto requestBookAdminLogResponseDto =  new RequestBookAdminLogResponseDto();
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
            List<BookRequest> requestBookList =  bookRequestService.findAllBookRequestListAndKeyword(searchText, pageable.withPage(page));
            Integer totalCnt = bookRequestService.getAllBookRequestListAndKeywordCnt(searchText);
            List<RequestBookAdminLog> requestBookAdminList = new ArrayList();
            for (BookRequest bookRequest : requestBookList) {
                String bookName = bookRequest.getBookName();
                String requestDate = bookRequest.getRegDate();
                String completeDate = bookRequest.getCompleteDate();
                String status = bookRequest.getStatus();
                String userName = bookRequest.getMember().getName();
                RequestBookAdminLog requestBookAdminLog = new RequestBookAdminLog(bookName,  requestDate, completeDate, status, userName);
                requestBookAdminList.add(requestBookAdminLog);
            }
            requestBookAdminLogResponseDto.setRequestBookLogList(requestBookAdminList);;
            requestBookAdminLogResponseDto.setTotalCnt(totalCnt);
            return new ResponseEntity(requestBookAdminLogResponseDto, HttpStatus.OK);
        } else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public ResponseEntity adminExtendPeriod(Member loginMember, String bookNumber) {
        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
            Book book = findByBookNumber(bookNumber);
            Optional<BookLog> bookLog = bookLogRepository.findByBookAndStatus(book, InRental.getBookStatus());
            if(!bookLog.isEmpty()){
                BookLog log = bookLog.get();
                LocalDate today = LocalDate.now();
                LocalDate twoWeeksLater = today.plusWeeks(2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                log.setReturnDate(twoWeeksLater.format(formatter));
                bookLogRepository.save(log);
                messageDto.setMessage("대출 기한이 연장되었습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("대여 중인 책이 아닙니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        } else {
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }
}
