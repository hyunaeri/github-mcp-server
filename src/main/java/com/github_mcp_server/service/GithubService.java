package com.github_mcp_server.service;

import com.github_mcp_server.dto.branch.GithubRefCreateRequest;
import com.github_mcp_server.dto.branch.GithubRefResponse;
import com.github_mcp_server.dto.file.GithubDirectoryEntryResponse;
import com.github_mcp_server.dto.file.GithubFileContentResponse;
import com.github_mcp_server.dto.file.GithubFileResponse;
import com.github_mcp_server.dto.issue.GithubIssueCreateRequest;
import com.github_mcp_server.dto.issue.GithubIssueResponse;
import com.github_mcp_server.dto.repository.GithubRepositoryResponse;
import com.github_mcp_server.exception.ErrorCode;
import com.github_mcp_server.exception.GithubException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class GithubService {

    private final RestClient restClient;

    public GithubService(
            RestClient.Builder builder,
            @Value("${github.api.token}") String token
    ) {
        this.restClient = builder
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultHeader("User-Agent", "github-mcp-server")
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultStatusHandler(HttpStatusCode::isError, this::handleApiError)
                .build();
    }

    // 현재 인증된 GitHub 사용자 조회
    public String getAuthenticatedUser() {
        return restClient.get()
                .uri("/user")
                .retrieve()
                .body(String.class);
    }

    // GitHub 저장소 조회
    public GithubRepositoryResponse getRepository(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .body(GithubRepositoryResponse.class);
    }

    // GitHub Issue 생성
    public GithubIssueResponse createIssue(
            String owner,
            String repo,
            String title,
            String body
    ) {
        GithubIssueCreateRequest request = new GithubIssueCreateRequest(title, body);

        return restClient.post()
                .uri("/repos/{owner}/{repo}/issues", owner, repo)
                .body(request)
                .retrieve()
                .body(GithubIssueResponse.class);
    }

    // 기준 브랜치의 Git Reference 조회
    public GithubRefResponse getBranchRef(
            String owner,
            String repo,
            String branch
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/git/ref/heads/")
                        .pathSegment(branch.split("/"))
                        .build(owner, repo))
                .retrieve()
                .body(GithubRefResponse.class);
    }

    // 새로운 브랜치 생성
    public GithubRefResponse createBranch(
            String owner,
            String repo,
            String baseBranch,
            String newBranch
    ) {
        // 1. 기준 브랜치의 SHA 조회
        GithubRefResponse baseRef = getBranchRef(owner, repo, baseBranch);

        String sha = baseRef.object().sha();

        // 2. 새 Git reference 생성 요청
        GithubRefCreateRequest request = new GithubRefCreateRequest("refs/heads/" + newBranch, sha);

        // 3. GitHub API 호출
        return restClient.post()
                .uri("/repos/{owner}/{repo}/git/refs", owner, repo)
                .body(request)
                .retrieve()
                .body(GithubRefResponse.class);
    }

    // GitHub 저장소의 파일 내용 조회
    public GithubFileContentResponse getFileContent(
            String owner,
            String repo,
            String path,
            String branch
    ) {
        // 1. GitHub Contents API 호출
        GithubFileResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/contents/")
                        .path(path)
                        .queryParam("ref", branch)
                        .build(owner, repo))
                .retrieve()
                .body(GithubFileResponse.class);

        // 2. 응답 검증
        if (response == null) {
            throw new GithubException(ErrorCode.GITHUB_EMPTY_RESPONSE);
        }

        // 파일 타입 검증
        if (!"file".equals(response.type())) {
            throw new GithubException(ErrorCode.FILE_NOT_A_FILE);
        }

        // 인코딩 방식 검증
        if (!"base64".equals(response.encoding())) {
            throw new GithubException(ErrorCode.FILE_UNSUPPORTED_ENCODING);
        }

        // 파일 내용 검증
        if (response.content() == null) {
            throw new GithubException(ErrorCode.FILE_INVALID_CONTENT);
        }

        // 3. Base64 디코딩
        String content;

        try {
            byte[] decodedBytes = Base64.getMimeDecoder()
                    .decode(response.content());

            content = new String(
                    decodedBytes,
                    StandardCharsets.UTF_8
            );

        } catch (IllegalArgumentException e) {
            throw new GithubException(ErrorCode.FILE_INVALID_CONTENT, e);
        }

        // 4. MCP Client에 반환
        return new GithubFileContentResponse(
                response.name(),
                response.path(),
                response.sha(),
                content
        );
    }

    // 디렉토리에 포함된 파일 및 하위 디렉토리 목록 조회
    public List<GithubDirectoryEntryResponse> listDirectory(
            String owner,
            String repo,
            String path,
            String branch
    ) {
        String directoryPath = Objects.requireNonNullElse(path, "");

        List<GithubDirectoryEntryResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/contents")
                        .pathSegment(directoryPath.split("/"))
                        .queryParam("ref", branch)
                        .build(owner, repo))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return response;
    }

    private void handleApiError(
            HttpRequest request,
            ClientHttpResponse response
    ) throws IOException {

        int status = response.getStatusCode().value();

        ErrorCode errorCode = switch (status) {
            case 401 -> ErrorCode.GITHUB_UNAUTHORIZED;
            case 403 -> ErrorCode.GITHUB_FORBIDDEN;
            case 404 -> ErrorCode.GITHUB_RESOURCE_NOT_FOUND;
            case 422 -> ErrorCode.GITHUB_VALIDATION_FAILED;
            default -> ErrorCode.GITHUB_API_ERROR;
        };

        throw new GithubException(errorCode);
    }
}
