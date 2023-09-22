package com.mb.service;

import com.mb.domain.WebHook;
import com.mb.repository.WebHookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class WebHookService {

    private final WebHookRepository webHookRepository;

    public void sendHook(String hookUrl, String key, String token, String body){
        URI uri = UriComponentsBuilder
                .fromUriString("https://chat.googleapis.com/v1/spaces")
                .path("/" + hookUrl + "/messages")
                .queryParam("key", key)
                .queryParam("token", token)
                .encode()
                .build()
                .toUri();
        // 요청 본문 작성 (JSON 형식)
        String requestBody = body;

        RequestEntity<String> req = RequestEntity
                .post(uri)
                .header("Content-Type", "application/json")
                .body(requestBody);

        RestTemplate restTemplate = new RestTemplate();
        // 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(req, String.class);

        // 응답 확인
        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            System.out.println("응답 내용: " + responseBody);
        } else {
            System.out.println("응답 오류: " + response.getStatusCode());
        }
    }

    public WebHook findById(long webHookId) {
        return webHookRepository.findById(webHookId).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 webHook 정보 입니다."));
    }

    public void sendHookForAll(WebHook webHook, String body) {
        sendHook(webHook.getHookUrl(), webHook.getWebHookKey(), webHook.getWebHookToken(), body);
    }
}
