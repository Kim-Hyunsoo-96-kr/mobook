package com.mb.controller;

import com.mb.domain.*;
import com.mb.dto.*;
import com.mb.service.*;
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
    private final BookRequestService bookRequestService;

    @Operation(summary = "책 추가", description = "DB에 책을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody BookAddDto bookAddDto){
        BookAddResponseDto bookAddResponseDto = bookService.addBook(bookAddDto);

        return new ResponseEntity(bookAddResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "책 대여", description = "해당 책을 대여불가 상태로 DB에 저장합니다.")
    @PostMapping("/rent/{bookNumber}")
    public ResponseEntity bookRent(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.rentBook(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }
    @PostMapping("/recommend/{bookNumber}")
    public ResponseEntity bookRecommend(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.recommendBook(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    @PostMapping("/recommend/cancel/{bookNumber}")
    public ResponseEntity bookRecommendCancel(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.recommendCancelBook(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }
    @Operation(summary = "책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/return/{bookNumber}")
    public ResponseEntity bookReturn(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.returnBook(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
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

    @PostMapping("/request")
    public ResponseEntity bookRequest(@RequestBody BookRequestDto bookRequestDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);

        BookRequest bookRequest = new BookRequest();
        bookRequest.setBookName(bookRequestDto.getBookName());
        bookRequest.setBookWriter(bookRequestDto.getBookWriter());
        bookRequest.setBookPublisher(bookRequestDto.getBookPublisher());
        bookRequest.setStatus(Request.getBookStatus());
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bookRequest.setRegDate(today.format(formatter));
        bookRequest.setCompleteDate("0");
        bookRequestService.save(bookRequest);

        MessageDto messageDto = new MessageDto();
        messageDto.setMessage("성공적으로 책을 요청했습니다.");

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    @PostMapping("/request/complete/{bookRequestId}")
    public ResponseEntity requestComplete(@PathVariable Long bookRequestId){
        BookRequest bookRequest = bookRequestService.findById(bookRequestId);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bookRequest.setCompleteDate(today.format(formatter));
        bookRequest.setStatus(RequestComplete.getBookStatus());
        bookRequestService.save(bookRequest);
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage("성공적으로 처리완료했습니다.");

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    private Member getLoginMember(Authentication authentication) {
        if(authentication == null){
            System.out.println("authentication에 아무것도 없음");
            return new Member();
        }
        Long memberId = (Long) authentication.getPrincipal();
        Member loginMember = memberService.findById(memberId);
        return loginMember;
    }

}
