package com.example.prj3be.controller;

import com.example.prj3be.domain.Cart;
import com.example.prj3be.dto.CartItemDto;
import com.example.prj3be.exception.OutOfStockException;
import com.example.prj3be.repository.MemberRepository;
import com.example.prj3be.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final MemberRepository memberRepository;

    @GetMapping("/fetch")
    public List<CartItemDto> fetchCart() {
        System.out.println("CartController.fetchCart");
        //accessToken으로부터 로그인 아이디 추출
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("===========================================================");
        System.out.println("logId = " + logId);
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        return cartService.getCartList(memberId);
    }
//  엔드 포인트를 처리하는 메소드로 get 요청을 처리하고 cart/fetch경로에 매핑
//  System.out.println("CartController.fetchCart");:
//  간단한 디버깅 목적으로 해당 메소드가 호출됨
//  String logId = SecurityContextHolder.getContext().getAuthentication().getName();
//  : 현재 인증된 사용자의 로그인 아이디를 추출하여 logId에 할당.
//  System.out.println("logId = " + logId);
//  추출된 로그인 아이디를 콘솔에 출력.
//  Long memberId = memberRepository.findIdByLogId(logId);
//  로그인 아이디 사용으로 해당 유저 멤버아이디를 검색
//  System.out.println("memberId = " + memberId);
// 검색된 멤버아이디를 콘솔에 출력
//    = 현재 인증된 사용자의 로그인 아이디를 통해 해당 사용자의 멤버를 찾고, 멤버 아이디를 이용해 장바구니 목록을 반환함

    @PostMapping("/add")
    public ResponseEntity createCartAndAddItem(Long boardId, Long stockQuantity) {
        //id = board.id (상품명)
        System.out.println("boardId = " + boardId);
        System.out.println("CartController.createCartAndAddItem");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);
        System.out.println("stockQuantity = " + stockQuantity);

        try {
            Cart cart = cartService.createCart(memberId);
            System.out.println("CartController에서 cart = " + cart.getId());
            cartService.addItemsToCart(cart, boardId, stockQuantity);
            return ResponseEntity.ok().build();
        } catch (OutOfStockException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
//  HTTP POST 요청을 처리하고 경로로 이동
//    boardId는 상품의 식별자로 사용된다
//    현재 인증된 사용자의 로그인 아이디를 추출, 그 아이디를 항
//  사용자가 요청한 상품을 장바구니에 추가하는 목적을 가짐.
//  재고 부족이나 다른 예외가 발생할 경우에 대비한 응답을 제공함
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId) {
        System.out.println("CartController.deleteCartItem");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        try {
            cartService.deleteCartItemByCartAndCartItem(memberId, cartItemId);
            return ResponseEntity.ok(cartItemId + "번 아이템 삭제 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카트 아이템 삭제 중 오류 발생");
        }
    }

    @GetMapping("/addCount/{cartItemId}")
    public void addCount(@PathVariable Long cartItemId) {
        System.out.println("========================================");
        System.out.println("CartController.addCount");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        cartService.addCountToCartItem(memberId, cartItemId);
        System.out.println("========================================");
    }

    @GetMapping("/subtractCount/{cartItemId}")
    public void subtractCount(@PathVariable Long cartItemId) {
        System.out.println("========================================");
        System.out.println("CartController.subtractCount");
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("logId = " + logId);
        //로그인 아이디로부터 멤버 아이디 추출
        Long memberId = memberRepository.findIdByLogId(logId);
        System.out.println("memberId = " + memberId);

        cartService.subtractCountFromCartItem(memberId, cartItemId);
        System.out.println("========================================");
    }


}