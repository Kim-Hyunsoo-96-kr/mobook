package com.mb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.domain.*;
import com.mb.dto.Admin.resp.AdminBookLogResponseDto;
import com.mb.dto.Admin.resp.AdminRentBookLogResponseDto;
import com.mb.dto.Admin.resp.AdminRequestBookLogResponseDto;
import com.mb.dto.Book.req.BookAddDto;
import com.mb.dto.Book.req.BookEditDto;
import com.mb.dto.Book.req.BookRequestDto;
import com.mb.dto.Book.req.BookCommentRequestDto;
import com.mb.dto.Book.resp.BookListResponseDto;
import com.mb.dto.Book.resp.DashboardResponseDto;
import com.mb.dto.Book.resp.RecentBookListTop5Dto;
import com.mb.dto.Util.MessageDto;
import com.mb.dto.Util.NaverResponseDto;
import com.mb.repository.*;
import com.mb.util.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final BookCommentService bookCommentService;
    private final WebHookService webHookService;
    @Value("${naver.clientId}")
    public String naverClientId;
    @Value("${naver.clientSecret}")
    public String naverClientSecret;
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

    public List<Book> getBookListByOptionAndKeyword(String option, String keyword, Pageable pageable) {
        List<Book> bookList = new ArrayList<>();
        switch (option){
            case "title":
                bookList = bookRepository.findByBookNameContaining(keyword, pageable);
                break;
            case "number":
                bookList = bookRepository.findByBookNumberContaining(keyword, pageable);
                break;
            case "author":
                bookList = bookRepository.findByBookAuthorContaining(keyword, pageable);
                break;
            case "publisher":
                bookList = bookRepository.findByBookPublisherContaining(keyword, pageable);
                break;
            case "description":
                bookList = bookRepository.findByBookDescriptionContaining(keyword, pageable);
                break;
            case "all":
                bookList = bookRepository.findAllContainingKeyword(keyword, pageable);
                break;
            default:
                bookList = bookRepository.findByBookNameContaining(keyword, pageable);
                break;
        }

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
            newBook.setEditDate(today.format(formatter));
            newBook.setRecommend(0);
            newBook.setPopularity(0);
            newBook.setRentalMemberId(0L);
            newBook.setIsDeleted(false);

            URI uri = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com")
                    .path("/v1/search/book.json")
                    .queryParam("query", bookAddDto.getBookName())
                    .queryParam("display", 10)
                    .queryParam("start", 1)
                    .queryParam("sort", "sim")
                    .encode()
                    .build()
                    .toUri();

            RequestEntity<Void> req = RequestEntity
                    .get(uri)
                    .header("X-Naver-Client-Id", naverClientId)
                    .header("X-Naver-Client-Secret", naverClientSecret)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

            ObjectMapper om = new ObjectMapper();
            NaverResponseDto naverResponseDto = null;

            try{
                naverResponseDto = om.readValue(resp.getBody(), NaverResponseDto.class);
                if(!naverResponseDto.getItems().isEmpty()){
                    newBook.setBookLink(naverResponseDto.getItems().get(0).getLink());
                    newBook.setBookImageUrl(naverResponseDto.getItems().get(0).getImage());
                    newBook.setBookAuthor(naverResponseDto.getItems().get(0).getAuthor());
                    newBook.setBookPublisher(naverResponseDto.getItems().get(0).getPublisher());
                    newBook.setBookDescription(naverResponseDto.getItems().get(0).getDescription());
                } else {
                    newBook.setBookImageUrl("https://raw.githubusercontent.com/jootang2/MyS3/7c8c92a8b513f32b17864bf6a0779457895d0392/MOBOOK1.1/MOBOOK1.1_404.png");
                }
            } catch (Exception e){
                throw new IllegalArgumentException("Json parser 관련 오류");
            }


            bookRepository.save(newBook);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    WebHook webHook = webHookService.findById(1L);
                    String body = WebHookUtil.bookAddHook(1);
                    webHookService.sendWebHook(webHook, body);
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
                        if(cell.getRawValue() != null){
                            System.out.println(cell.getRawValue());
                            newBook.setBookNumber(cell.getRawValue());
                        }
                    }


                    // 행의 첫 번째 열(이름)
                    cell = row.getCell(1);
                    if (null != cell) {
                        if(cell.getRawValue() != null){
                            newBook.setBookName(cell.getStringCellValue());
                            URI uri = UriComponentsBuilder
                                    .fromUriString("https://openapi.naver.com")
                                    .path("/v1/search/book.json")
                                    .queryParam("query", cell.getStringCellValue())
                                    .queryParam("display", 10)
                                    .queryParam("start", 1)
                                    .queryParam("sort", "sim")
                                    .encode()
                                    .build()
                                    .toUri();

                            RequestEntity<Void> req = RequestEntity
                                    .get(uri)
                                    .header("X-Naver-Client-Id", naverClientId)
                                    .header("X-Naver-Client-Secret", naverClientSecret)
                                    .build();
                            RestTemplate restTemplate = new RestTemplate();
                            ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

                            ObjectMapper om = new ObjectMapper();
                            NaverResponseDto naverResponseDto = null;

                            try{
                                naverResponseDto = om.readValue(resp.getBody(), NaverResponseDto.class);
                                if(!naverResponseDto.getItems().isEmpty()){
                                    newBook.setBookLink(naverResponseDto.getItems().get(0).getLink());
                                    newBook.setBookImageUrl(naverResponseDto.getItems().get(0).getImage());
                                    newBook.setBookAuthor(naverResponseDto.getItems().get(0).getAuthor());
                                    newBook.setBookPublisher(naverResponseDto.getItems().get(0).getPublisher());
                                    newBook.setBookDescription(naverResponseDto.getItems().get(0).getDescription());
                                } else {
                                    newBook.setBookImageUrl("https://raw.githubusercontent.com/jootang2/MyS3/7c8c92a8b513f32b17864bf6a0779457895d0392/MOBOOK1.1/MOBOOK1.1_404.png");
                                }
                            } catch (Exception e){
                                throw new IllegalArgumentException("Json parser 관련 오류");
                            }
                        }
                    }
                    newBook.setIsAble(true);
                    Date today = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    newBook.setRegDate(formatter.format(today));
                    newBook.setEditDate(formatter.format(today));
                    newBook.setRecommend(0);
                    newBook.setPopularity(0);
                    newBook.setRentalMemberId(0L);
                    newBook.setIsDeleted(false);

                    // 리스트에 담는다.
                    list.add(newBook);
                    Thread.sleep(80); // 0.1초(100ms) 일시 정지
                }
            } catch (Exception e){
                messageDto.setMessage("엑셀 관련 오류");
                System.out.println(e);
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
                        WebHook webHook = webHookService.findById(1L);
                        String body = WebHookUtil.bookAddHook(list.size());
                        try{
                            webHookService.sendWebHook(webHook, body);
                        } catch (Exception e){
                            throw new IllegalArgumentException("WEBHOOK ERROR");
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
        bookRequest.setBookLink(bookRequestDto.getBookLink());
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
                WebHook webHook = webHookService.findById(1L);
                String body = WebHookUtil.bookRequestHook(loginMember.getName(), bookRequestDto.getBookName(), bookRequestDto.getBookLink());
                try{
                    webHookService.sendWebHook(webHook, body);
                } catch (Exception e){
                    throw new IllegalArgumentException("WEBHOOK ERROR");
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
        for (BookLog bookLog : bookLogList) {
            String email = bookLog.getMember().getEmail();
            WebHook webHook = webHookService.findByEmail(email);
            String body = WebHookUtil.bookReturnHookBefore3Days(bookLog.getMember().getName(), bookLog.getBook().getBookName());
            try{
                webHookService.sendWebHook(webHook, body);
            } catch (Exception e){
                throw new IllegalArgumentException("WEBHOOK ERROR");
            }
        }
    }

    @Transactional
    public ResponseEntity rentBook(Member loginMember, String bookNumber) {
        MessageDto messageDto = new MessageDto();
        Book book = findByBookNumber(bookNumber);
        if(book.getIsDeleted()){
            messageDto.setMessage("삭제된 책 입니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        } else {
            if (book.getIsAble()) {
                Integer popularity = book.getPopularity() + 1;
                book.setPopularity(popularity);
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

                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                    try{
                        WebHook webHook = webHookService.findByEmail(loginMember.getEmail());
                        String body = WebHookUtil.bookRentHook(loginMember.getName(), book.getBookName());
                        webHookService.sendWebHook(webHook, body);
                        //todo 관리자한테만 보내기
                    } catch (Exception e){
                        throw new IllegalArgumentException("WEBHOOK ERROR");
                    }
                    }
                });

                messageDto.setMessage("성공적으로 대여했습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("대여할 수 없는 책 입니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        }
    }

    public ResponseEntity recommendBook(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        if(book.getIsDeleted()){
            messageDto.setMessage("삭제된 책 입니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        } else {
            Optional<BookRecommend> bookRecommend = bookRecommendRepository.findByMemberAndBook(loginMember, book);
            if(bookRecommend.isEmpty()){
                Integer recommendCount = book.getRecommend() + 1;
                Integer popularity = book.getPopularity() + 1;
                book.setPopularity(popularity);
                book.setRecommend(recommendCount);
                bookRepository.save(book);
                BookRecommend newBookRecommend = new BookRecommend();
                newBookRecommend.setMember(loginMember);
                newBookRecommend.setBook(book);
                bookRecommendRepository.save(newBookRecommend);
                messageDto.setMessage("선택하신 책을 찜했습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("이미 찜한 책입니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        }
    }

    public ResponseEntity recommendCancelBook(Member loginMember, String bookNumber) {
        Book book = findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        Optional<BookRecommend> bookRecommend = bookRecommendRepository.findByMemberAndBook(loginMember, book);
        if(bookRecommend == null){
            messageDto.setMessage("찜한 책이 아닙니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        } else {
            Integer recommendCount = book.getRecommend() - 1;
            book.setRecommend(recommendCount);
            bookRepository.save(book);
            bookRecommendRepository.delete(bookRecommend.orElseThrow(()-> new IllegalArgumentException("존재하지 않는 데이터입니다.")));
            messageDto.setMessage("찜을 취소했습니다.");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        }
    }

    public ResponseEntity adminReturnBook(Member loginMember, String bookNumber) {
        if(loginMember.getIsAdmin()){
            Book book = findByBookNumber(bookNumber);
            Member rentalMember =  memberService.findById(book.getRentalMemberId());
            Integer rentalBookQuantity = rentalMember.getRentalBookQuantity() - 1;
            rentalMember.setRentalBookQuantity(rentalBookQuantity);
            memberRepository.save(rentalMember);

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

    public ResponseEntity bookSearch(String option, String searchText, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("bookId").descending());
        List<Book> bookList = getBookListByOptionAndKeyword(option, searchText, pageable.withPage(page));
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
            AdminRentBookLogResponseDto adminRentBookLogResponseDto = new AdminRentBookLogResponseDto();
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
            adminRentBookLogResponseDto.setRentBook(rentBookAdminLogList);
            adminRentBookLogResponseDto.setTotalCnt(totalCnt);
            return new ResponseEntity(adminRentBookLogResponseDto, HttpStatus.OK);
        } else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return  new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public ResponseEntity requestBookLog(Member loginMember, String searchText, Integer page) {
        if(loginMember.getIsAdmin()){
            AdminRequestBookLogResponseDto adminRequestBookLogResponseDto =  new AdminRequestBookLogResponseDto();
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
            adminRequestBookLogResponseDto.setRequestBookLogList(requestBookAdminList);;
            adminRequestBookLogResponseDto.setTotalCnt(totalCnt);
            return new ResponseEntity(adminRequestBookLogResponseDto, HttpStatus.OK);
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

    public ResponseEntity comment(Member loginMember, String bookNumber, BookCommentRequestDto bookCommentRequestDto) {
        MessageDto messageDto = new MessageDto();

        Book book = findByBookNumber(bookNumber);
        BookComment bookComment = new BookComment();
        bookComment.setBook(book);
        bookComment.setMemberName(loginMember.getName());
        bookComment.setMemberId(loginMember.getMemberId());
        LocalDateTime today = LocalDateTime .now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        bookComment.setRegDate(today.format(formatter));
        bookComment.setEditDate("1996-07-07 00:00:00");
        bookComment.setComment(bookCommentRequestDto.getComment());

        bookCommentService.save(bookComment);

        messageDto.setMessage("성공적으로 댓글을 작성했습니다.");
        return new ResponseEntity(messageDto, HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity editComment(Member loginMember, Long commentId, BookCommentRequestDto bookCommentRequestDto) {
        MessageDto messageDto = new MessageDto();
        try{
            BookComment bookComment = bookCommentService.findById(commentId);
            if(loginMember.getMemberId().equals(bookComment.getMemberId())){
                bookComment.setComment(bookCommentRequestDto.getComment());
                LocalDateTime today = LocalDateTime .now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                bookComment.setEditDate(today.format(formatter));
                bookCommentService.save(bookComment);

                messageDto.setMessage("성공적으로 댓글을 수정했습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("작성자만 댓글을 수정할 수 있습니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e){
            messageDto.setMessage("찾을 수 없는 댓글입니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity deleteComment(Member loginMember, Long commentId) {
        MessageDto messageDto = new MessageDto();
        try{
            BookComment bookComment = bookCommentService.findById(commentId);
            if(loginMember.getMemberId().equals(bookComment.getMemberId())){
                bookCommentService.delete(bookComment);
                messageDto.setMessage("성공적으로 댓글을 삭제했습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("작성자만 댓글을 삭제할 수 있습니다.");
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e){
            messageDto.setMessage("찾을 수 없는 댓글입니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity deleteBook(Long bookId, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        try {
            if (loginMember.getIsAdmin()) {
                Book book = findById(bookId);
                if(book.getIsDeleted()){
                    messageDto.setMessage("이미 삭제처리된 책입니다.");
                    return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
                } else {
                    book.setIsDeleted(true);
                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    book.setEditDate(today.format(formatter));
                    saveBook(book);

                    messageDto.setMessage("성공적으로 책을 삭제했습니다.");
                    return new ResponseEntity(messageDto, HttpStatus.OK);
                }
            } else {
                messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
                return new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
            }
        } catch (IllegalArgumentException e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity editSearchBook(BookAddDto bookAddDto, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        try{
            if(loginMember.getIsAdmin()){
                Book book = findByBookNumber(bookAddDto.getBookNumber());
                book.setBookNumber(bookAddDto.getBookNumber());
                book.setBookName(bookAddDto.getBookName());
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                book.setEditDate(today.format(formatter));

                URI uri = UriComponentsBuilder
                        .fromUriString("https://openapi.naver.com")
                        .path("/v1/search/book.json")
                        .queryParam("query", bookAddDto.getBookName())
                        .queryParam("display", 10)
                        .queryParam("start", 1)
                        .queryParam("sort", "sim")
                        .encode()
                        .build()
                        .toUri();

                RequestEntity<Void> req = RequestEntity
                        .get(uri)
                        .header("X-Naver-Client-Id", naverClientId)
                        .header("X-Naver-Client-Secret", naverClientSecret)
                        .build();

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

                ObjectMapper om = new ObjectMapper();
                NaverResponseDto naverResponseDto = null;

                try{
                    naverResponseDto = om.readValue(resp.getBody(), NaverResponseDto.class);
                    if(!naverResponseDto.getItems().isEmpty()){
                        book.setBookLink(naverResponseDto.getItems().get(0).getLink());
                        book.setBookImageUrl(naverResponseDto.getItems().get(0).getImage());
                        book.setBookAuthor(naverResponseDto.getItems().get(0).getAuthor());
                        book.setBookPublisher(naverResponseDto.getItems().get(0).getPublisher());
                        book.setBookDescription(naverResponseDto.getItems().get(0).getDescription());
                    } else {
                        book.setBookLink("");
                        book.setBookImageUrl("https://raw.githubusercontent.com/jootang2/MyS3/7c8c92a8b513f32b17864bf6a0779457895d0392/MOBOOK1.1/MOBOOK1.1_404.png");
                        book.setBookAuthor("저자 정보 없음");
                        book.setBookPublisher("출판사 정보 없음");
                        book.setBookDescription("책 정보 없음");
                    }
                } catch (Exception e){
                    throw new IllegalArgumentException("Json parser 관련 오류");
                }


                saveBook(book);

                messageDto.setMessage("성공적으로 책을 수정했습니다.");
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } else {
                messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
                return new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
            }
        } catch (Exception e){
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity editBook(BookEditDto bookEditDto, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        if(loginMember.getIsAdmin()){
            Book book = findByBookNumber(bookEditDto.getBookNumber());
            book.setBookName(bookEditDto.getBookName());
            book.setBookAuthor(bookEditDto.getBookAuthor());
            book.setBookPublisher(bookEditDto.getBookPublisher());
            book.setBookDescription(bookEditDto.getBookDescription());
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            book.setEditDate(today.format(formatter));

            saveBook(book);

            messageDto.setMessage("성공적으로 책을 수정했습니다.");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        } else {
            messageDto.setMessage("관리자만 해당 기능을 사용할 수 있습니다.");
            return new ResponseEntity(messageDto, HttpStatus.PRECONDITION_FAILED);
        }
    }
    public ResponseEntity getRecentBookList() {
        RecentBookListTop5Dto recentBookListTop5Dto = new RecentBookListTop5Dto();
        try{
            List<Book> recentBookList = bookRepository.findTop5ByOrderByBookIdDesc();
            recentBookListTop5Dto.setBookList(recentBookList);
            return new ResponseEntity(recentBookListTop5Dto, HttpStatus.OK);
        } catch (Exception e){
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity getDashboardList() {
        DashboardResponseDto dashboardResponseDto = new DashboardResponseDto();

        try{
            List<Book> recentBookList = bookRepository.findTop5ByOrderByBookIdDesc();
            List<Book> popularBookList = bookRepository.findTop5ByOrderByPopularityDesc();
            List<BookLog> bookLogList = bookLogService.findByStatus(InRental);
            List<RentBookAdminLog> rentBookLogList = new ArrayList();
            for (BookLog bookLog : bookLogList) {
                Book rentBook = bookLog.getBook();
                RentBookAdminLog rentBookAdminLog = new RentBookAdminLog(rentBook.getBookNumber(), rentBook.getBookName(),
                        rentBook.getRecommend(), bookLog.getRegDate(), bookLog.getReturnDate(), bookLog.getMember().getName());
                rentBookLogList.add(rentBookAdminLog);
            }
            dashboardResponseDto.setRecentBookList(recentBookList);
            dashboardResponseDto.setPopularBookList(popularBookList);
            dashboardResponseDto.setRentBookList(rentBookLogList);
            return new ResponseEntity(dashboardResponseDto, HttpStatus.OK);
        } catch (Exception e){
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage(e.getMessage());
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity downloadBookAddExcel(HttpServletResponse response) {
        MessageDto messageDto = new MessageDto();
            try{
                String fileName = "bookAddExample.xlsx"; // 파일 이름만 지정

                // 클래스패스 내의 리소스를 가져오기 위해 ClassLoader 사용
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream inputStream = classLoader.getResourceAsStream(fileName);

                if (inputStream == null) {
                    throw new FileNotFoundException("파일을 찾을 수 없습니다: " + fileName);
                }
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                OutputStream out = response.getOutputStream();

                int read;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                return new ResponseEntity(messageDto, HttpStatus.OK);
            } catch (Exception e){
                messageDto.setMessage(e.getMessage());
                return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
            }
    }
}
