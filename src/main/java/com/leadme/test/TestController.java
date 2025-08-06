package com.leadme.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/server-compare")
    public ResponseEntity<Object> processActionAndCalcVector(@RequestBody TestRequest request) {
        return ResponseEntity.ok(testService.processActionAndCalcVector(request.contentId()));
    }

    @PostMapping("/db-compare")
    public ResponseEntity<String> processActionAndRecommend(@RequestBody TestRequest request) {
        testService.processActionAndRecommend(request.contentId());
        return ResponseEntity.ok("ok");
    }
}
