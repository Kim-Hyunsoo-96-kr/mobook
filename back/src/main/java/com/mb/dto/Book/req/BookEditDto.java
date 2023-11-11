package com.mb.dto.Book.req;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class BookEditDto {

    private String bookNumber;

    private String bookName;

    private String bookLink;

    private String bookAuthor;

    private String bookPublisher;

    private String bookDescription;

    private String bookImageUrl;

}
