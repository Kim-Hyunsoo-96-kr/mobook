package com.mb.controller;

import com.mb.dto.Member.req.MemberLoginDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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
