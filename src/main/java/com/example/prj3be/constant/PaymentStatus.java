package com.example.prj3be.constant;

public enum PaymentStatus {
    CARD("카드"),CASH("현금"),POINT("포인트");
    private String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
//Enum 클래스를 사용하면 결제 상태를 명확하게 정의하고 각 상태에 대한 설명을 얻을 수 있음 열거형(Enum)을 사용하여 결제 상태를 정의하는 클래스를 의미한다
//상수들의 집합을 나타냄
// Enum 상수 정의 카드 캐쉬 포인트 세개의 이넘 상수가 정의되어 있음
//인스턴스 변수 인스턴스 변수는 이넘 상수가 나타나는 결제 방법을 설명하는 문자열임
// 생성자ㅣ 페이먼트Status는 Enum상수가 나타내는 결제 방법에 대한 설명을 받아와 인스턴스 변수에 할당한다
//  메소드 : getDescription메소드는 이넘 상수가 나타내는 결제 방법에 대한 설명을 반환한다
