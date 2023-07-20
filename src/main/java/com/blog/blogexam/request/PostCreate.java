package com.blog.blogexam.request;

import com.blog.blogexam.exception.InvalidRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 빌더 패턴의 장점
 * - 가독성에 좋다. (값 생성에 대한 유연함)
 * - 필요한 값만 받을 수 있다.
 *
 */

@ToString
@Setter
@Getter
public class PostCreate {


    @NotBlank(message = "타이틀을 입력해주세요.") // null 도 체크 해줌
    private String title;

    @NotBlank(message = "컨텐츠를 입력해주세요.")
    private String content;

    public PostCreate() {
    }
    
    @Builder // 어노테이션을 클래스 레벨에 붙여도 되지만 다른 어노테이션에의해 영향 받을 수 있어서 비추천
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequest("title","제목에 바보를 포함할 수 없습니다.");
        }
    }
}
