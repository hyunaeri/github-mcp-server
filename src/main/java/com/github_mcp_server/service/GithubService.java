package com.github_mcp_server.service;

import com.github_mcp_server.dto.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
}
