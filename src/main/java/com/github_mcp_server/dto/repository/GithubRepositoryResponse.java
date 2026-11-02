package com.github_mcp_server.dto.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryResponse(
        String name,
        String description,

        @JsonProperty("html_url")
        String htmlUrl,

        @JsonProperty("stargazers_count")
        int stars,

        @JsonProperty("default_branch")
        String defaultBranch
) {
}
