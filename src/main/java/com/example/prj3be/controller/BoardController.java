package com.example.prj3be.controller;

import com.example.prj3be.domain.*;
import com.example.prj3be.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("list")
    public Page<Board> list(Pageable pageable,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) AlbumFormat albumFormat,
                            @RequestParam(required = false) String[] albumDetails,
                            @RequestParam(required = false) String minPrice,
                            @RequestParam(required = false) String maxPrice,
                            @RequestParam(required = false) Long stockQuantity
                        ) {

        List<AlbumDetail> albumDetailList = (albumDetails == null) ? null : Arrays.stream(albumDetails).map(AlbumDetail::valueOf).collect(Collectors.toList());

        Page<Board> boardListPage = boardService.boardListAll(pageable, title, albumFormat, albumDetailList, minPrice, maxPrice, stockQuantity);


        // stackoverflowerror 발생 가능한 지점
        return boardListPage;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("add")
    public void add(@Validated Board saveBoard,
                    @RequestParam(required = false) String[] albumDetails,
                    @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files) throws IOException {

        List<AlbumDetail> AlbumDetailList = Arrays.stream(albumDetails)
                .map(AlbumDetail::valueOf)
                .collect(Collectors.toList());

        boardService.save(saveBoard, AlbumDetailList , files);
    }


    @GetMapping("id/{id}")
    public Optional<Board> get(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }
    @GetMapping("file/id/{id}")
    public List<String> getURL(@PathVariable Long id) {
        return boardService.getBoardURL(id);
    }


    @PutMapping("edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void update(@PathVariable Long id,
                       Board updateBboard,
                       @RequestParam(value = "uploadFiles", required = false) MultipartFile uploadFiles) throws IOException {
        System.out.println("updateBboard = " + updateBboard);
        System.out.println("uploadFiles = " + uploadFiles);
        System.out.println("updateBboard.getStockQuantity() = " + updateBboard.getStockQuantity());

        if (uploadFiles == null) {
            boardService.update(id, updateBboard);

        } else {
            boardService.update(id, updateBboard, uploadFiles);
        }
    }


    @DeleteMapping("remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        boardService.delete(id);
    }


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

}
//스프링 프레임워크를 사용한 RESTful* Api(인터페이스) 를 제공하는 컨트롤러 클래스임
//list 메소드 : HTTP 의 Get 메소그를 통해 게시판 목록을 페이징 하여 반환한다
//여러 파라미터를 받아와서 이를 기반으로 게시판 목록을 검색한다

//** 특히 **
// 게시판에 관련한 다양한 기능을 제공하는 클래스로 각각의메소드는   HTTP 요청을 통해 해당기능을 수행한다
//어노테이션 PreAuthorize를 사용하여 권한이 있는 사용자만이 특정 기능을 수행할 수 있도록 함

//여기서 궁금증 > 인증 인가를 포함한 스프링 시큐리티 전반의 내용은 이제야 접근하기 시작함. (20240207) 16시 07분
// 스프링 시큐리티라고 하는 인증 인가를 위한 초기 설정과 나머지 클래스들의 설정 프로세스는 어떻게 되는 지 알고 싶음


// 16시 11분 : 스프링 시큐리티 : 관리자 및 일반 사용자 역할을 부여하여 인증 및 인가를 구현하려면
//1. 의존성 추가 pom.xml파일에 스프링 시큐리티 의존성을 추가함.
//2. 스프링 시큐리티 구성 클래스의 작성이 필요함 (아래의 코드는 예시를 위한 것. )
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {-----
//
//3.권한 기반 접근 제어 설정 HTTPsecurity 객체를 사용하여 URL별 접근 제어를 설정한다. 관리자 권한이 필요한 URL과 그 이외의 URL을 구분하여 설정함
//4.사용자 인증 정보 설정 : `AuthenticationManagerBuilder`를 사용하여 사용자의 인증 정보를 설정한다 실제 데이터베이스나 LDAP* 와 같은 외부 소스를 사용할 수 있음
//5.컨트롤러에 접근 제어 설정 : 관리자 권한이 필요한 기능을 가진 컨트롤러에 어노테이션 `@PreAuthorize 를 붙여 접근 제어 설정함.(아래 코드는 이해를 위한 예시 임 )
//@RestController
//@RequestMapping("/admin")
//public class AdminController {
//
//    @PostMapping("/product/add")
//    @PreAuthorize("hasRole('ADMIN')")
//    public void addProduct() {
//        // 상품 추가 기능 구현
//    }
//
//    // 기타 관리자 기능에 대한 컨트롤러 메소드들
//}



//보드 컨트롤러에서 관리자 권한에 관한 설명을 했지만, 다시 관리자 클래스에 돌아가서 이 내용을 파악하는 게 필요해 보임

