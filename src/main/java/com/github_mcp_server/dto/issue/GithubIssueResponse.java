package com.github_mcp_server.dto.issue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubIssueResponse(
        Long number,
        String title,
        String state,

        @JsonProperty("html_url")
        String htmlUrl
) {}
