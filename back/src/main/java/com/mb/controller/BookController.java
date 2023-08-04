package com.mb.controller;

import com.mb.domain.Book;
import com.mb.domain.BookMember;
import com.mb.domain.Member;
import com.mb.dto.*;
import com.mb.enum_.BookStatus;
import com.mb.service.BookMemberService;
import com.mb.service.BookService;
import com.mb.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mb.enum_.BookStatus.Rent;

@Tag(name="BookController", description = "책 컨트롤러")
@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final MemberService memberService;
    private final BookMemberService bookMemberService;

    @Operation(summary = "책 추가", description = "DB에 책을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody BookAddDto bookAddDto){
        Book newBook = new Book();

        newBook.setBookName(bookAddDto.getBookName());
        newBook.setBookNumber(bookAddDto.getBookNumber());
        newBook.setIsAble(true);
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        newBook.setRegDate(formatter.format(today));

        Book addBook = bookService.addBook(newBook);

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
            bookService.addBook(book);
            BookMember bookMember = new BookMember();
            bookMember.setBook(book);
            bookMember.setMember(loginMember);
            bookMember.setStatus(Rent.getBookStatus());
            bookMemberService.addBookMember(bookMember);
            BookRentResponseDto bookRentResponseDto = new BookRentResponseDto();
            bookRentResponseDto.setMemberName(loginMember.getName());
            bookRentResponseDto.setBookName(book.getBookName());

            return new ResponseEntity(bookRentResponseDto, HttpStatus.OK);
        }
        else {
            ErrorDto errorDto = new ErrorDto();
            errorDto.setErrorMessage("대여할 수 없는 책 입니다.");
            return new ResponseEntity(errorDto ,HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/{bookId}/return")
    public ResponseEntity bookReturn(@PathVariable Long bookId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findById(bookId);
        if(loginMember.getMemberId() == book.getRentalMemberId()){
            book.setIsAble(true);
            bookService.addBook(book);
            BookReturnResponseDto bookReturnResponseDto = new BookReturnResponseDto();
            bookReturnResponseDto.setMemberName(loginMember.getName());
            bookReturnResponseDto.setBookName(book.getBookName());
            bookReturnResponseDto.setMessage("반납 완료");

            return new ResponseEntity(bookReturnResponseDto, HttpStatus.OK);
        }
        else {
            ErrorDto errorDto = new ErrorDto();
            errorDto.setErrorMessage("해당 책을 대여하지 않았습니다.");
            return new ResponseEntity(errorDto ,HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "책 검색", description = "검색값과 일치하는 제목의 책 리스트를 가져옵니다.")
    @GetMapping("/search")
    public ResponseEntity bookSearch(@RequestBody BookSearchDto bookSearchDto, Pageable pageable){
        pageable = PageRequest.of(bookSearchDto.getPage(), 10, Sort.by("bookId").descending());
        List<Book> bookList = bookService.getBookListByKeyword(bookSearchDto.getSearchText(), pageable.withPage(bookSearchDto.getPage()));

        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
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
            newBook.setStars(0);
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
