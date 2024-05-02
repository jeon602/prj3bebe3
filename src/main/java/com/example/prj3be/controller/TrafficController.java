package com.example.prj3be.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class TrafficController {
    private List<String> list = new ArrayList<>();
    @GetMapping("cpu")
    public String cpu() {
        log.info("cpu");
        long value = 0;
        for (long i = 0; i < 10000000000000L; i++){
            value++;
        }
        return "ok value = " + value;
    }
    @GetMapping("/jvm")
    public String jvm() {
        log.info("jvm");
        for (int i = 0; i < 1000000; i++) {
            list.add("hello jvm!" + i);
        }
        return "ok";
    }
}
// 20240502 15: 25~
//스프링 프레임 워크를 사용해 RESTful API를 제공한느 웹 어플리케이션의 일부임
//TrafficController class : 웹 요청을 처리하기 위한 Controller정의
// cpu() 메서드는 CPU집약적인 작업을 시뮬레이션해 오랜시간동안 실행된다. 부하테스트나 성능모니터링을 위해 사용될 수 있음
// jvm() method is mapped to the HTTP GET request path '/JVM'.
// When called it repeatedly adds string to a simulate memory consumption whthin the JVM (JAVA Virtual Machine)

// @Slf4j  로깅 코드를 자동으로 생성.
// list 변수는 JVM 메모리 사용을 증가시키는 데 사용되는 ArrayList 임

//    @GetMapping("/jvm")
//    public String jvm() {
//        log.info("jvm");
//        for (int i = 0; i < 1000000; i++) {
//            list.add("hello jvm!" + i);
//        }
//        return "ok";
//    }
//}
// = 엔드포인트에 대한 get request를 처리하는 메소드임
//로깅 jvm이라는 정보 수준의 로그를 기록함
// 반복문을 통한 작업  ㅣ 0부터 999,999까지의 숫자를 반복하면서 hello jvm 문자열을 생성해 리스트에 추가함
// jvm메모리 사용을 증가시키는 작업이 이루어지는 것임
//OK 문자열을 반환시킴
// 클라이언트가 /jvm엔드포인트에 대한 request 를 보내면 서버는 리스트에 대량의 문자열을 추가해서 jvm사용량을 증가시키고 'OK'응답을 반환시킨다.