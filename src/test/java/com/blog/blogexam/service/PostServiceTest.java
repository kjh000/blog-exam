package com.blog.blogexam.service;

import com.blog.blogexam.domain.Post;
import com.blog.blogexam.exception.PostNotFound;
import com.blog.blogexam.repository.PostRepository;
import com.blog.blogexam.request.PostCreate;
import com.blog.blogexam.request.PostEdit;
import com.blog.blogexam.request.PostSearch;
import com.blog.blogexam.response.PostResponse;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc // MockMvc 주입해줌
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("글 작성")
    void test1() {
        // given

        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        //when
        postService.write(postCreate);

        //then
        Assertions.assertThat(postRepository.count()).isEqualTo(1L);
    }


    @Test
    @DisplayName("글 한개 조회")
    void test2() {
        //given

        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();

        postRepository.save(post);

        Long postId = post.getId();

        //when
        PostResponse foundPost = postService.get(postId);


        //then
        Assertions.assertThat(foundPost).isNotNull();
    }

    @Test
    @DisplayName("글 첫 1페이지 조회")
    void test3() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i ->
                        Post.builder()
                                .title("블로그 제목 " + i)
                                .content("반포자이 " + i)
                                .build()
                )
                .toList();

        postRepository.saveAll(requestPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");

        PostSearch postSearch = PostSearch.builder()
                .page(1)
//                .size(10)
                .build();

        //when
        List<PostResponse> posts = postService.getList(postSearch);


        //then
        Assertions.assertThat(posts.size()).isEqualTo(10);
        Assertions.assertThat(posts.get(0).getTitle()).isEqualTo("블로그 제목 30");

//        // expected -> when + then
//        mockMvc.perform(MockMvcRequestBuilders.get("/posts?page=0&size=5&sort=id,desc")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
    }


    @Test
    @DisplayName("글 제목 수정")
    void test4() throws Exception {

        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("뉴제목")
                .build();

        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        System.out.println("title = " + changedPost.getTitle());
        System.out.println("content = " + changedPost.getContent());

        Assertions.assertThat(changedPost.getTitle()).isEqualTo("뉴제목");

        //나는 왜 에러 안나지???
        Assertions.assertThat(changedPost.getContent()).isEqualTo("반포자이");

    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() throws Exception {

        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .content("초가집")
                .build();

        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        System.out.println("title = " + changedPost.getTitle());
        System.out.println("content = " + changedPost.getContent());

//        Assertions.assertThat(changedPost.getTitle()).isEqualTo("제목");
        Assertions.assertThat(changedPost.getContent()).isEqualTo("초가집");

    }


    @Test
    @DisplayName("게시글 삭제")
    public void deleteTest() {

        //given
        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);


        //when
        postService.delete(post.getId());

        //then
        Assertions.assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("글 1개 조회 실패")
    public void readTest() {
        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);

        Long postId = post.getId();

        //when
//        PostResponse foundPost = postService.get(postId+2L);

//        Assertions.assertThat(foundPost).isNotNull();

        assertThrows(PostNotFound.class,()->{
            postService.get(post.getId()+2L);
        },"예외처리가 잘못되었어요.");

//        Assertions.assertThat(e.getMessage()).isEqualTo("존재하지 않는 글 입니다.");
    }

    @Test
    @DisplayName("글 1개 삭제 실패")
    public void deleteFailTest() {
        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);

        Long postId = post.getId();

        //when
//        PostResponse foundPost = postService.get(postId+2L);

//        Assertions.assertThat(foundPost).isNotNull();

        assertThrows(PostNotFound.class,()->{
            postService.delete(post.getId()+2L);
        },"예외처리가 잘못되었어요.");

//        Assertions.assertThat(e.getMessage()).isEqualTo("존재하지 않는 글 입니다.");
    }

    @Test
    @DisplayName("글 1개 수정 실패")
    public void editFailTest() {
        Post post = Post.builder()
                .title("제목")
                .content("반포자이")
                .build();

        postRepository.save(post);


        PostEdit postEdit = PostEdit.builder()
                .content("초가집")
                .build();

        //when
        assertThrows(PostNotFound.class,()->{
            postService.edit(post.getId()+2L,postEdit);
        },"예외처리가 잘못되었어요.");

    }




}