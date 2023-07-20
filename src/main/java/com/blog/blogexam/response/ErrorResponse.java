package com.blog.blogexam.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * {
 *     "code" : "400",
 *     "message" : "잘못된 요청입니다.",
 *     "validation" : {
 *         "title" : "값을 입력해주세요"
 *     }
 * }
 */
//@JsonInclude(value = JsonInclude.Include.NON_EMPTY) // 비어있지 않은 데이터만 내려줌. 그래도 비어있는 데이터까지 넘겨주는게 좋아보임
@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final Map<String,String> validation; // Map 을쓰지말고 다이나믹하게 json이 생성될수 있도록 개선 필요

    @Builder
    public ErrorResponse(String code, String message,Map<String,String> validation) {
        this.code = code;
        this.message = message;
        this.validation = validation != null ? validation : new HashMap<>();
//        this.validation = validation;
    }

    public void addValidation(String fieldName, String errorMessage) {
        validation.put(fieldName, errorMessage);
    }


}
