package com.lior.applicaton.rh_test.repos;

import com.lior.applicaton.rh_test.model.Comment;
import com.lior.applicaton.rh_test.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findCommentsByCommentednews(News news, PageRequest pageable);
}