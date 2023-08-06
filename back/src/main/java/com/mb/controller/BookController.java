package com.mb.controller;

import com.mb.domain.Book;
import com.mb.domain.BookRecommend;
import com.mb.domain.BookLog;
import com.mb.domain.Member;
import com.mb.dto.*;
import com.mb.service.BookRecommendService;
import com.mb.service.BookLogService;
import com.mb.service.BookService;
import com.mb.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mb.enum_.BookStatus.*;

@Tag(name="BookController", description = "책 컨트롤러")
@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final MemberService memberService;
    private final BookLogService bookLogService;
    private final BookRecommendService bookRecommendService;

    @Operation(summary = "책 추가", description = "DB에 책을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody BookAddDto bookAddDto){
        Book newBook = new Book();

        newBook.setBookName(bookAddDto.getBookName());
        newBook.setBookNumber(bookAddDto.getBookNumber());
        newBook.setIsAble(true);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        newBook.setRegDate(today.format(formatter));
        newBook.setRecommend(0);
        newBook.setRentalMemberId(0L);

        Book addBook = bookService.saveBook(newBook);

        BookAddResponseDto bookAddResponseDto = new BookAddResponseDto();
        bookAddResponseDto.setName(addBook.getBookName());

        return new ResponseEntity(bookAddResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "책 전체 리스트", description = "DB에 저장된 전체 책 리스트를 가져옵니다.")
    @GetMapping("/list")
    public ResponseEntity bookList(){
        List<Book> bookList= bookService.getBooksList();
        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "책 대여", description = "해당 책을 대여불가 상태로 DB에 저장합니다.")
    @PostMapping("/rent/{bookNumber}")
    public ResponseEntity bookRent(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findByBookNumber(bookNumber);
        if(book.getIsAble()){
            book.setRentalMemberId(loginMember.getMemberId());
            book.setIsAble(false);
            bookService.saveBook(book);

            BookLog bookLog = new BookLog();
            bookLog.setBook(book);
            bookLog.setMember(loginMember);
            bookLog.setStatus(InRental.getBookStatus());
            LocalDate today = LocalDate.now();
            LocalDate twoWeeksLater = today.plusWeeks(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            bookLog.setRegDate(today.format(formatter));
            bookLog.setReturnDate(twoWeeksLater.format(formatter));
            bookLogService.addBookMember(bookLog);

            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("성공적으로 대여했습니다.");
            return new ResponseEntity(messageDto, HttpStatus.OK);
        }
        else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("대여할 수 없는 책 입니다.");
            return new ResponseEntity(messageDto,HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/recommend/{bookNumber}")
    public ResponseEntity bookRecommend(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        Optional<BookRecommend> bookRecommend = bookRecommendService.findByMemberAndBook(book, loginMember);
        if(bookRecommend.isEmpty()){
            Integer recommendCount = book.getRecommend() + 1;
            book.setRecommend(recommendCount);
            bookService.saveBook(book);
            BookRecommend newBookRecommend = new BookRecommend();
            newBookRecommend.setMember(loginMember);
            newBookRecommend.setBook(book);
            bookRecommendService.save(newBookRecommend);
            messageDto.setMessage("선택하신 책을 추천했습니다.");
        } else {
            messageDto.setMessage("이미 추천한 책입니다.");
        }
        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    @PostMapping("/recommend/cancel/{bookNumber}")
    public ResponseEntity bookRecommendCancel(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findByBookNumber(bookNumber);
        MessageDto messageDto = new MessageDto();
        Optional<BookRecommend> bookRecommend = bookRecommendService.findByMemberAndBook(book, loginMember);
        if(bookRecommend == null){
            messageDto.setMessage("추천한 책이 아닙니다.");
        } else {
            Integer recommendCount = book.getRecommend() - 1;
            book.setRecommend(recommendCount);
            bookService.saveBook(book);
            bookRecommendService.delete(bookRecommend);
            messageDto.setMessage("선택하신 책의 추천을 취소했습니다.");
        }
        return new ResponseEntity(messageDto, HttpStatus.OK);
    }
    @Operation(summary = "책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/return/{bookNumber}")
    public ResponseEntity bookReturn(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findByBookNumber(bookNumber);
        if(loginMember.getMemberId() == book.getRentalMemberId()){
            book.setIsAble(true);
            bookService.saveBook(book);
            BookLog bookHistory = new BookLog();
            bookHistory.setBook(book);
            bookHistory.setMember(loginMember);
            bookHistory.setStatus(Return.getBookStatus());
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            bookHistory.setRegDate(today.format(formatter));
            bookHistory.setReturnDate("0");
            bookLogService.addBookMember(bookHistory);

            BookLog bookHistoryLog = bookLogService.findByMemberAndBookAndStatus(loginMember, book, InRental);
            bookHistoryLog.setStatus(Rent.getBookStatus());
            bookHistoryLog.setReturnDate("0");
            bookLogService.addBookMember(bookHistoryLog);

            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("반납을 완료했습니다.");

            return new ResponseEntity(messageDto, HttpStatus.OK);
        }
        else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("해당 책을 대여하지 않았습니다.");
            return new ResponseEntity(messageDto,HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "책 검색", description = "검색값과 일치하는 제목의 책 리스트를 가져옵니다.")
    @GetMapping("/search")
    public ResponseEntity bookSearch(@RequestParam(name = "searchText") String searchText, @RequestParam(name = "page", defaultValue = "1") Integer page, Pageable pageable){
        pageable = PageRequest.of(page, 10, Sort.by("bookId").descending());
        List<Book> bookList = bookService.getBookListByKeyword(searchText, pageable.withPage(page));
        Integer totalCnt = bookService.getTotalCntBySearchText(searchText) + 1 ;

        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        bookListResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
    }

    @PostMapping("/add/excel")
    public ResponseEntity test(@RequestParam("excelFile") MultipartFile mf) throws Exception {
        List<Book> list = new ArrayList<>();

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

        bookService.addBookByExcel(list);
        return new ResponseEntity(HttpStatus.OK);
    }

    private Member getLoginMember(Authentication authentication) {
        if(authentication == null){
            System.out.println("authentication에 아무것도 없음");
            return memberService.findById(1L);
        }
        Long memberId = (Long) authentication.getPrincipal();
        Member loginMember = memberService.findById(memberId);
        return loginMember;
    }

}
