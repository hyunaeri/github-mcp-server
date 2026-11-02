package com.github_mcp_server.dto.branch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRefResponse(
        String ref,
        GitObject object
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitObject(
            String sha,
            String type
    ){}
}
