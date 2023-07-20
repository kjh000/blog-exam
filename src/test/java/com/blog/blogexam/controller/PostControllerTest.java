package com.blog.blogexam.controller;

import com.blog.blogexam.domain.Post;
import com.blog.blogexam.repository.PostRepository;
import com.blog.blogexam.request.PostCreate;
import com.blog.blogexam.request.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest // MockMvc 주입해줌
@AutoConfigureMockMvc // MockMvc 주입해줌
@SpringBootTest
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다")
    void test() throws Exception {

        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        System.out.println(json);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ) // application/json
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());

    }

    @Test
    @DisplayName("/posts 요청시 title값은 필수다.")
    void test2() throws Exception {

        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)) // application/json
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(print());

    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test3() throws Exception {

        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)) // application/json
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Assertions.assertThat(postRepository.count()).isEqualTo(1L);

        Post post = postRepository.findAll().get(0);
        Assertions.assertThat(post.getTitle()).isEqualTo("제목입니다.");
        Assertions.assertThat(post.getContent()).isEqualTo("내용입니다.");


    }

    @Test
    @DisplayName("글 한개 조회")
    void test4() throws Exception {
        // given
        Post post = Post.builder()
                .title("12345678901234")
                .content("bar")
                .build();

        postRepository.save(post);
        // expected -> when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("1234567890"))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());

    }

//    @Test
//    @DisplayName("글 여러개 조회")
//    void test5() throws Exception {
//        // given
//        Post post1 = Post.builder()
//                .title("title1")
//                .content("bar1")
//                .build();
//
//        postRepository.save(post1);
//
//        Post post2 = Post.builder()
//                .title("title2")
//                .content("bar2")
//                .build();
//
//        postRepository.save(post2);
//
//
//        // expected -> when + then
//        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()", Matchers.is(2)))
//                .andExpect(jsonPath("$[0].id").value(post1.getId()))
//                .andExpect(jsonPath("$[0].title").value("title1"))
//                .andExpect(jsonPath("$[0].content").value("bar1"))
//                .andDo(print());
//
//    }

    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {

        // given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                        .title("블로그 제목" + i)
                        .content("반포자이" + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("블로그 제목19"))
                .andExpect(jsonPath("$[0].content").value("반포자이19"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 여러개 페이징 조회")
    void test6() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i ->
                        Post.builder()
                                .title("블로그 제목 " + i)
                                .content("반포자이 " + i)
                                .build()
                )
                .toList();

        postRepository.saveAll(requestPosts);

        // expected -> when + then
        // expected -> when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?page=1&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
//                .andExpect(jsonPath("$[0].id").value(30))
                .andExpect(jsonPath("$[0].title").value("블로그 제목 30"))
                .andDo(print());

    }


    @Test
    @DisplayName("페이지를 0으로 입력해도 제대로 첫페이지를 가져온다")
    void test7() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i ->
                        Post.builder()
                                .title("블로그 제목 " + i)
                                .content("반포자이 " + i)
                                .build()
                )
                .toList();

        postRepository.saveAll(requestPosts);

        // expected -> when + then
        // expected -> when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
//                .andExpect(jsonPath("$[0].id").value(30))
                .andExpect(jsonPath("$[0].title").value("블로그 제목 30"))
                .andDo(print());

    }

    @Test
    @DisplayName("글제목 수정")
    void test8() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("뉴제목")
                .content("반포자이")
                .build();


        // expected -> when + then
        // expected -> when + then
        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{postId}", post.getId()) //PATCH /posts/{postId}
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("글 삭제하기")
    public void deleteTest() throws Exception {
        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test9() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void test10() throws Exception{
        PostEdit postEdit = PostEdit.builder()
                .title("뉴제목")
                .content("반포자이")
                .build();


        // expected -> when + then
        // expected -> when + then
        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{postId}", 1L) //PATCH /posts/{postId}
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성시 제목에 '바보'는 포함될 수 없다.")
    void 바보() throws Exception {

        PostCreate request = PostCreate.builder()
                .title("나는 바보입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)) // application/json
                .andExpect(status().isBadRequest())
                .andDo(print());


    }
}

// API 문서 생성

// GET /posts/{postId} -> 단건 조회
// POST /posts -> 게시글 등록

// 클라이언트 입장에서는 어떤 API 있는지 모름

// Spring RestDocs
// 운영코드에 영향이 없음 
// 코드수정 -> 문서 수정 안하면 -> 코드(기능)과 문서가 달라져 신뢰성 낮아짐

// RestDocs 는 Test 케이스 실해 -> 문서 생성