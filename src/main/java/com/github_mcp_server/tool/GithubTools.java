package com.github_mcp_server.tool;

import com.github_mcp_server.dto.GithubIssueResponse;
import com.github_mcp_server.dto.GithubRepositoryResponse;
import com.github_mcp_server.service.GithubService;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubTools {

    private final GithubService githubService;

    @McpTool(
            name = "get_authenticated_user",
            description = "현재 인증된 GitHub 사용자 정보를 조회합니다."
    )
    public String getAuthenticatedUser() {
        return githubService.getAuthenticatedUser();
    }

    @McpTool(
            name = "get_repository",
            description = "Github 저장소의 정보를 조회합니다."
    )
    public GithubRepositoryResponse getRepository(
            @McpToolParam(description = "Github 사용자 또는 조직 이름", required = true) String owner,
            @McpToolParam(description = "Github 저장소 이름", required = true) String repo) {
        return githubService.getRepository(owner, repo);
    }

    @McpTool(
            name = "create_issue",
            description = "GitHub 저장소에 새로운 이슈를 생성합니다.",
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = false,
                    destructiveHint = false,
                    idempotentHint = false
            )
    )
    public GithubIssueResponse createIssue(
            @McpToolParam(description = "저장소 소유자", required = true)
            String owner,

            @McpToolParam(description = "저장소 이름", required = true)
            String repo,

            @McpToolParam(description = "이슈 제목", required = true)
            String title,

            @McpToolParam(description = "이슈 본문", required = true)
            String body
    ) {
        return githubService.createIssue(owner, repo, title, body);
    }
}
