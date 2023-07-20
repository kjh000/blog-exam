package com.blog.blogexam.service;

import com.blog.blogexam.domain.Post;
import com.blog.blogexam.domain.PostEditor;
import com.blog.blogexam.exception.PostNotFound;
import com.blog.blogexam.repository.PostRepository;
import com.blog.blogexam.request.PostCreate;
import com.blog.blogexam.request.PostEdit;
import com.blog.blogexam.request.PostSearch;
import com.blog.blogexam.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(PostCreate postCreate) {
//        postCreate -> Entity
        Post post = Post.builder()
                .content(postCreate.getContent())
                .title(postCreate.getTitle())
                .build();


        return postRepository.save(post);

    }

    public PostResponse get(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글 입니다."));

        /**
         * Controller -> Service -> Repository
         *               WebService
         *
         */

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }


    /**
     * 글이 너무 많은 경우 -> 비용이 너무 많이 든다.
     * ex) 글이 10억개 -> db 글 모두 조회하는 경우 -> 서버 터짐
     * db -> 애플리케이션 서버로 전달하는 시간, 트래픽 비용등이 많이 발생할 수 있음.
     * 따라서 db 전체를 조회하는 경우는 거의 없음
     */
    public List<PostResponse> getList(PostSearch postSearch) {

        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }


//    @Transactional
    public void edit(Long id, PostEdit postEdit) {

        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);


        PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();
        PostEditor postEditor = postEditorBuilder
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
        postRepository.save(post);

    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        postRepository.delete(post);
    }
}
