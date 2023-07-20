package com.blog.blogexam.controller;

import com.blog.blogexam.domain.Post;
import com.blog.blogexam.exception.InvalidRequest;
import com.blog.blogexam.request.PostCreate;
import com.blog.blogexam.request.PostEdit;
import com.blog.blogexam.request.PostSearch;
import com.blog.blogexam.response.PostResponse;
import com.blog.blogexam.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 데이터를 검증하는 이유
 * 1. client 개발자가 실수 할 수 있음
 * 2. client 에 버그가 있을 수 있음
 * 3. 외부의 나쁜사람이 값을 임의로 조작해서 보낼 수 있음
 * 4. DB에 값을 저장할 때 의도치 않은 오류가 발생할 수 있음
 * 5. 서버 개발자의 편의를 위해서
 * <p>
 * <p>
 * Request 클래스와 Response 클래스를 분리하는 것이 좋음
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public String init() {
        return "success";
    }

    @PostMapping("/posts")
    public void get(@RequestBody @Valid PostCreate request) throws Exception {
//        log.info("title = {}, content={}",title,content);

//        if (result.hasErrors()) {
//            List<FieldError> fieldErrors = result.getFieldErrors();
//            FieldError firstFieldError = fieldErrors.get(0);
//            String fieldName = firstFieldError.getField();
//            String errorMessage = firstFieldError.getDefaultMessage();
//
//            Map<String,String> error = new HashMap<>();
//            error.put(fieldName,errorMessage);
//
//            return error;
//        }
//        log.info("params = {}",params.toString());
//        return "Hello World";
//        위는 너무옜날방식임

        // Case 1. 저장한 데이터 Entity -> response 로 응답하기
        // Case 2. 저장한 데이터의 primary id 만 응답하기

        request.validate();
        postService.write(request);


    }

    /**
     * /posts -> 글 전체 조회(검색 + 페이징)
     * /posts/{postId} -> 글 한개만 조회
     */

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit request) {
        postService.edit(postId,request);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }
}
