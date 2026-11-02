package com.github_mcp_server.dto.file;

public record GithubFileContentResponse(
        String name,
        String path,
        String sha,
        String content
) {}