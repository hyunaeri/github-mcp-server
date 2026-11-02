package com.github_mcp_server.dto.issue;

public record GithubIssueCreateRequest(
        String title,
        String body
){}
