
package com.github_mcp_server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // GitHub API 공통 오류
    GITHUB_EMPTY_RESPONSE(
            "GITHUB_EMPTY_RESPONSE",
            "GitHub API 응답이 비어 있습니다."
    ),

    GITHUB_UNAUTHORIZED(
            "GITHUB_UNAUTHORIZED",
            "GitHub 인증에 실패했습니다."
    ),

    GITHUB_FORBIDDEN(
            "GITHUB_FORBIDDEN",
            "GitHub API에 접근할 권한이 없거나 요청 제한에 걸렸습니다."
    ),

    GITHUB_RESOURCE_NOT_FOUND(
            "GITHUB_RESOURCE_NOT_FOUND",
            "GitHub 리소스를 찾을 수 없거나 접근할 수 없습니다."
    ),

    GITHUB_VALIDATION_FAILED(
            "GITHUB_VALIDATION_FAILED",
            "GitHub API 요청 검증에 실패했습니다."
    ),

    GITHUB_API_ERROR(
            "GITHUB_API_ERROR",
            "GitHub API 호출 중 오류가 발생했습니다."
    ),

    // GitHub 파일 관련 오류
    FILE_NOT_FOUND(
            "FILE_NOT_FOUND",
            "요청한 파일을 찾을 수 없습니다."
    ),

    FILE_NOT_A_FILE(
            "FILE_NOT_A_FILE",
            "요청한 경로가 파일이 아닙니다."
    ),

    FILE_UNSUPPORTED_ENCODING(
            "FILE_UNSUPPORTED_ENCODING",
            "지원하지 않는 파일 인코딩 방식입니다."
    ),

    FILE_INVALID_CONTENT(
            "FILE_INVALID_CONTENT",
            "파일 내용을 디코딩할 수 없습니다."
    );

    private final String code;
    private final String message;
}
