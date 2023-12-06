package com.mb.dto.Book.req;

import com.mb.domain.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class BookAddByBarcodeDto {
    private List<String> ISBNList;
}
