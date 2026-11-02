package com.github_mcp_server.dto;

public record GithubRefCreateRequest(
        String ref,
        String sha
) {}
