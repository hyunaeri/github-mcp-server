package com.github_mcp_server.tool;

import com.github_mcp_server.dto.file.GithubDirectoryEntryResponse;
import com.github_mcp_server.dto.file.GithubFileContentResponse;
import com.github_mcp_server.dto.issue.GithubIssueResponse;
import com.github_mcp_server.dto.branch.GithubRefResponse;
import com.github_mcp_server.dto.repository.GithubRepositoryResponse;
import com.github_mcp_server.service.GithubService;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @McpTool(
            name = "create_branch",
            description = "기존 브랜치를 기준으로 새로운 GitHub 브랜치를 생성합니다."
    )
    public GithubRefResponse createBranch(
            @McpToolParam(description = "GitHub 저장소 소유자", required = true)
            String owner,

            @McpToolParam(description = "GitHub 저장소 이름", required = true)
            String repo,

            @McpToolParam(description = "기준 브랜치 이름 (예: master)", required = true)
            String baseBranch,

            @McpToolParam(description = "새로 생성할 브랜치 이름 (예: feat/new-feature)", required = true)
            String newBranch
    ) {
        return githubService.createBranch(owner, repo, baseBranch, newBranch);
    }


    @McpTool(
            name = "get_file_content",
            description = "GitHub 저장소의 특정 브랜치에서 파일 내용을 조회합니다.",
            annotations = @McpTool.McpAnnotations(
                    readOnlyHint = true
            )
    )
    public GithubFileContentResponse getFileContent(
            @McpToolParam(
                    description = "GitHub 저장소 소유자",
                    required = true
            )
            String owner,

            @McpToolParam(
                    description = "GitHub 저장소 이름",
                    required = true
            )
            String repo,

            @McpToolParam(
                    description = "조회할 파일 경로 (예: README.md)",
                    required = true
            )
            String path,

            @McpToolParam(
                    description = "조회할 브랜치 이름 (예: master)",
                    required = true
            )
            String branch
    ) {
        return githubService.getFileContent(owner, repo, path, branch);
    }

    @McpTool(
            name = "list_directory",
            description = "GitHub 저장소의 특정 브랜치에서 디렉터리에 포함된 파일과 하위 디렉터리 목록을 조회합니다."
    )
    public List<GithubDirectoryEntryResponse> listDirectory(
            @McpToolParam(description = "저장소 소유자", required = true)
            String owner,

            @McpToolParam(description = "저장소 이름", required = true)
            String repo,

            @McpToolParam(description = "조회할 디렉터리 경로 (프로젝트 루트는 빈 문자열)", required = false)
            String path,

            @McpToolParam(description = "조회할 브랜치 이름", required = true)
            String branch
    ) {
        if (path == null) path = "";
        return githubService.listDirectory(owner, repo, path, branch);
    }
}
