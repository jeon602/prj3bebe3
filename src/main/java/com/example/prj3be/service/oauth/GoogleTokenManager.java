package com.example.prj3be.service.oauth;

import com.example.prj3be.repository.SocialTokenRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoogleTokenManager implements SocialTokenManager {

    @Value("${social.google.client.id}")
    private String GOOGLE_SNS_CLIENT_ID;
    @Value("${social.google.client.secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;
    @Value("${social.google.token.url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;
    @Value("${social.google.revoke.url}")
    private String GOOGLE_SNS_REVOKE_TOKEN;

    private final SocialTokenRepository socialTokenRepository;
    private final RestTemplate restTemplate;

    @Override
    public String getRefreshUri(Long id) {
        String refreshToken = socialTokenRepository.findRefreshTokenById(id);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("client_id", GOOGLE_SNS_CLIENT_ID);
        queryParams.set("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        queryParams.set("refresh_token", refreshToken);
        queryParams.set("grant_type", "refresh_token");

        return UriComponentsBuilder
                .fromUriString(GOOGLE_SNS_TOKEN_BASE_URL)
                .queryParams(queryParams)
                .encode().build().toString();
    }; // 토큰 갱신 URI 생성하는 메소드

    @Override
    public ResponseEntity<String> checkAndRefreshToken(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String refreshURI = getRefreshUri(id);

        return restTemplate.exchange(refreshURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
    }; //토큰 갱신 요청하는 메소드

    @Override
    public ResponseEntity socialLogout(Long id) {
        String accessToken = socialTokenRepository.findAccessTokenById(id);
        HttpHeaders headers = new HttpHeaders();

        String expireTokenURI = UriComponentsBuilder.fromUriString(GOOGLE_SNS_REVOKE_TOKEN)
                .queryParam("token", accessToken)
                .encode().build().toString();
        return tryRevokeToken(id, headers, expireTokenURI);
    }

    @Override
    public ResponseEntity revokeToken(Long id) {
        String accessToken = socialTokenRepository.findAccessTokenById(id);
        HttpHeaders headers = new HttpHeaders();

        String revokeTokenURI = UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/revoke")
                .queryParam("token", accessToken)
                .encode().build().toString();

        return tryRevokeToken(id, headers, revokeTokenURI);
    }

    @Override
    public ResponseEntity tryRevokeToken(Long id, HttpHeaders headers, String revokeTokenURI) {
        try {
            ResponseEntity response = restTemplate.exchange(revokeTokenURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                socialTokenRepository.findAndDeleteTokenById(id);
            }
            return response;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getRawStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
