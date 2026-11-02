package com.github_mcp_server.dto.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubFileResponse(
        String type,
        String name,
        String path,
        String sha,
        String encoding,
        String content
) {}
