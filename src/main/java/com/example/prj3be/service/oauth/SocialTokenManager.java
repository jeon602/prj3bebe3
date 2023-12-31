package com.example.prj3be.service.oauth;

import com.example.prj3be.constant.SocialLoginType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SocialTokenManager {
    ResponseEntity<String> checkAndRefreshToken(Long id); //토큰 갱신 요청하는 메소드
    String getRefreshUri(Long id); // 토큰 갱신 URI 생성하는 메소드

    //접근 토큰을 이용한 토큰 만료 요청 - 로그아웃용
    ResponseEntity socialLogout(Long id);

    //접근 토큰을 이용한 연동 해제 요청 및 토큰 데이터 삭제 - 탈퇴용
    ResponseEntity revokeToken(Long id);

    //토큰 만료 신청하고 응답 리턴하는 메소드 - 로그아웃 & 탈퇴 공용
    ResponseEntity tryRevokeToken(Long id, HttpHeaders headers, String revokeTokenURI);


    default SocialLoginType type() {
        if(this instanceof GoogleTokenManager) {
            return SocialLoginType.GOOGLE;
        } else if (this instanceof NaverTokenManager) {
            return SocialLoginType.NAVER;
        } else if (this instanceof KakaoTokenManager) {
            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }

}
