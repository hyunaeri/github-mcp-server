package com.github_mcp_server.dto.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubDirectoryEntryResponse(
        String name,
        String path,
        String type,
        String sha,
        Long size
) {}
