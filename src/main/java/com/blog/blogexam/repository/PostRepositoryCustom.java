package com.blog.blogexam.repository;

import com.blog.blogexam.domain.Post;
import com.blog.blogexam.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
