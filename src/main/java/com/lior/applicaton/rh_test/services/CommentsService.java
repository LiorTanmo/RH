package com.lior.applicaton.rh_test.services;

import com.lior.applicaton.rh_test.model.Comment;
import com.lior.applicaton.rh_test.model.News;
import com.lior.applicaton.rh_test.repos.CommentsRepository;
import com.lior.applicaton.rh_test.repos.NewsRepository;
import com.lior.applicaton.rh_test.security.UserAccountDetails;
import com.lior.applicaton.rh_test.util.NewsNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@AllArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private  final NewsRepository newsRepository;


    public Page<Comment> findAll(int news_id, int page, int comms_per_page){
        return commentsRepository.findCommentsByCommentednews(
                newsRepository.findById(news_id).orElseThrow(NewsNotFoundException::new),
                PageRequest.of(page,comms_per_page));
    }
    public void removeComment(int comm_id){
        commentsRepository.deleteById(comm_id);
    }

    public void clearCommentsByNewsId(int id){

    }


    public void removeComment(int news_id, int com_num){
        News news = newsRepository.findById(news_id).orElseThrow(NewsNotFoundException::new);
        commentsRepository.delete(news.getComments().remove(com_num));
        newsRepository.save(news);
    }

    public void editComment(Comment updCom, int news_id, int com_num){
        News news = newsRepository.findById(news_id).orElseThrow(NewsNotFoundException::new);
        updCom.setId(news.getComments().get(com_num).getId());
        updCom.setCommentednews(news);
        commentsRepository.save(updCom);
    }

    public void addComment (Comment comment, int id){
        News commentedNews = newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
        comment.setCommentednews(commentedNews);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccountDetails userAccountDetails = (UserAccountDetails) authentication.getPrincipal();
        comment.setInserted_by(userAccountDetails.getUser());

        commentsRepository.save(comment);
    }

}
