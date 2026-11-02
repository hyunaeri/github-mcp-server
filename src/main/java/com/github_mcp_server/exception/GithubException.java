package com.github_mcp_server.exception;

import lombok.Getter;

@Getter
public class GithubException extends RuntimeException {

    private final ErrorCode errorCode;

    public GithubException(ErrorCode errorCode) {
        super(errorCode.getCode() + ": " + errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GithubException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getCode() + ": " + errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}