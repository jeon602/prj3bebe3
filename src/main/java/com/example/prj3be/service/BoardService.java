package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.BoardFile;
import com.example.prj3be.domain.QBoard;
import com.example.prj3be.repository.BoardFileRepository;
import com.example.prj3be.repository.BoardRepository;
//import com.example.prj3be.repository.ItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    @Value("${image.file.prefix}")
    private String urlPrefix;

    @Value("${aws.s3.bucket.name}")
    private String bucket;

    private final S3Client s3;

    public BoardService(BoardRepository boardRepository, BoardFileRepository boardFileRepository, S3Client s3){
        this.boardRepository = boardRepository;
        this.boardFileRepository = boardFileRepository;
        this.s3 = s3;
    }


    public Page<Board> boardListAll(Pageable pageable, String category, String keyword) {
        QBoard board = QBoard.board;
        BooleanBuilder builder = new BooleanBuilder();

        /* TODO: 카테고리 분류 추가하기*/
        /* TODO: 카테고리 분류 추가하기*/
        if (category != null && keyword != null) {
            if ("all".equals(category)) {
                builder.and(board.title.containsIgnoreCase(keyword));
            } else if ("CD".equals(category)) {
                builder.and(board.title.containsIgnoreCase(keyword));
            }else if ("CASSETTE".equals(category)){
                builder.and(board.title.containsIgnoreCase(keyword));
            }else if ("VINYL".equals(category)){
                builder.and(board.title.containsIgnoreCase(keyword));
            }
        }

        Predicate predicate = builder.hasValue() ? builder.getValue() : null; //삼항연산자

        if (predicate != null) {
            return boardRepository.findAll(predicate, pageable);
        } else {
            return boardRepository.findAll(pageable);
        }
    }

//    private Predicate createPredicate(String category, String keyword, QBoard board) {
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if( keyword != null && !keyword.trim().isEmpty()) {
//            builder.and(board.title.containsIgnoreCase(keyword));
//        }


//        if(!"all".equals(category)) {
//            builder.and(board.title.containsIgnoreCase(keyword));
//        }
//        return builder;
//    }


    public void save(Board board, MultipartFile[] files) throws IOException {

        boardRepository.save(board); //jpa의 save()메소드엔 파일을 넣지 못함

        Long id = board.getId();
        BoardFile boardFile = new BoardFile();
        Optional<Board> findBoard = boardRepository.findById(id);
        Board savedBoard = findBoard.get();


        for (int i = 0; i < files.length; i++) {
            String url = urlPrefix + "prj3/"+ id +"/" + files[i].getOriginalFilename();
            boardFile.setFileName(files[i].getOriginalFilename());
            boardFile.setFileUrl(url);
            boardFile.setBoard(savedBoard);
            boardFileRepository.save(boardFile);    //boardFile 테이블에 files 정보(fileName, fileUrl) 저장
            upload(files[i], id);
        }
    }

    //AWS s3에 파일 업로드
    private void upload(MultipartFile file,Long id) throws IOException {
        String key = "prj3/" + id + "/" +file.getOriginalFilename();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public String get(Long id, BoardFile boardFile) {


//        id로 board에 있는 id, title, price값을 가져와서 board에 넣음
        Optional<Board> board = boardRepository.findById(id);

        //파일 table에서 id로 파일명을 알아온 후에 boadFiles에 집어넣음
        Optional<BoardFile> boardFiles = boardFileRepository.findById(id);

        //Optional은 foreach 사용 불가. Optional은 set메소드 이용해서 내부의 값 직접 설정할수 없음
        // optional -> get메소드
        if (boardFiles.isPresent()) {
            BoardFile boardFile1 = boardFiles.get(); //보드 파일이 존재한다면 파일에 있는걸 boardFile1에 넣음
            String url = urlPrefix + "prj3/"+ id +"/" + boardFile1.getFileName(); //보드파일1의 파일name을 url에 넣음
            boardFile1.setFileUrl(url); //boardFile1에 setter로 FileUrl필드에 url값을 집어넣음
            String fileUrl = boardFile1.getFileUrl();  //boardFile에 들어간 url값을 fileUrl변수에 넣음
            return fileUrl;
        } else {
            return null;
        }

    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public Board update(Long id, Board updateBoard) {
        Optional<Board> boardById = boardRepository.findById(id);
        if (boardById.isPresent()) {
            Board board1 = boardById.get();
            board1.setTitle(updateBoard.getTitle());
            board1.setArtist(updateBoard.getArtist());
            board1.setPrice(updateBoard.getPrice());
            board1.setReleaseDate(updateBoard.getReleaseDate());
            board1.setContent(updateBoard.getContent());

//            앨범 포멧은 변경할 수 없는 걸로 해서 추가 안했어요.
            return boardRepository.save(board1);
        }
        return null;
    }

    public void update(Long id, Board updateBboard, MultipartFile uploadFiles) throws IOException {
        Board updatedBoard = update(id, updateBboard);
        boardFileRepository.deleteBoardFileByBoardId(id);

        BoardFile boardFile = new BoardFile();
        String url = urlPrefix + "prj3/"+ id +"/" + uploadFiles.getOriginalFilename();
        boardFile.setFileName(uploadFiles.getOriginalFilename());
        boardFile.setFileUrl(url);
        boardFile.setBoard(updatedBoard);
        boardFileRepository.save(boardFile);    //boardFile 테이블에 files 정보(fileName, fileUrl) 저장
        upload(uploadFiles, id);

    }

    public void delete(Long id) {
        boardFileRepository.deleteBoardFileByBoardId(id);
        boardRepository.deleteById(id);
    }

    public List<String> getBoardURL(Long id) {
        return boardFileRepository.findFileUrlsByBoardId(id);
    }


//    public void save(Board saveBoard, String imageURL) {
//        saveBoard.setImageURL(imageURL);
//        BoardFile boardFile = new BoardFile();
//        boardFile.setFileName(saveBoard.getFileName());
//        boardFile.setFileUrl(imageURL);
//        boardRepository.save(saveBoard);
//        boardFileRepository.save(boardFile);
//    }



//    public void saveWithImageURL(Board saveBoard, String imageURL) {
//        saveBoard.setImageURL(imageURL);
//        boardFileRepository.save(board);
//    }
}