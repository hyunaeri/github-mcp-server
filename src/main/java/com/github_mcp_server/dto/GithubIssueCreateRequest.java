package com.github_mcp_server.dto;

public record GithubIssueCreateRequest(
        String title,
        String body
){}
