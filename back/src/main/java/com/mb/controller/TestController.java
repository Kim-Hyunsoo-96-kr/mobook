package com.mb.controller;

import com.mb.domain.Member;
import com.mb.domain.RefreshToken;
import com.mb.dto.MemberLoginDto;
import com.mb.dto.MemberLoginResponseDto;
import com.mb.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {


    @GetMapping("/loginDto")
    public String login(@RequestBody @Valid MemberLoginDto memberLoginDto){
        return "OK";
    }
}
