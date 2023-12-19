package com.example.prj3be.repository;

import com.example.prj3be.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    Long deleteCommentByMemberId(Long id);
}
