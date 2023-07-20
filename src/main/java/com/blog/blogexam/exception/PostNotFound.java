package com.blog.blogexam.exception;

/**
 * status -> 404
 */
public class PostNotFound extends ParentException{

    private static final String MESSAGE = "존재하지 않는 글 입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    public PostNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public String statusCode() {
        return "404";
    }
}
