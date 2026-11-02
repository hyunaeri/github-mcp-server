package com.github_mcp_server.dto.branch;

public record GithubRefCreateRequest(
        String ref,
        String sha
) {}
