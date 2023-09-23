package com.lior.applicaton.rh_test.controllers;

import com.lior.applicaton.rh_test.dto.CommentDTO;
import com.lior.applicaton.rh_test.dto.CommentedNewsDTO;
import com.lior.applicaton.rh_test.dto.NewsDTO;
import com.lior.applicaton.rh_test.model.Comment;
import com.lior.applicaton.rh_test.model.News;
import com.lior.applicaton.rh_test.security.UserAccountDetails;
import com.lior.applicaton.rh_test.services.NewsService;
import com.lior.applicaton.rh_test.util.ErrorPrinter;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;


//TODO
@RestController
public class NewsController {

    private final NewsService newsService;
    private final ModelMapper modelMapper;

    private  final ErrorPrinter errorPrinter;

    @Autowired
    public NewsController(NewsService newsService, ModelMapper modelMapper, ErrorPrinter errorPrinter) {
        this.newsService = newsService;
        this.modelMapper = modelMapper;
        this.errorPrinter = errorPrinter;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid NewsDTO newsDTO,
                                             BindingResult bindingResult){
        errorPrinter.printErrors(bindingResult);
        newsService.save(toNews(newsDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> edit(@RequestBody @Valid NewsDTO newsDTO,
                                           BindingResult bindingResult,
                                           @PathVariable(name = "id") int id){
        errorPrinter.printErrors(bindingResult);
        newsService.update(id, toNews(newsDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping()
    public Page<NewsDTO> getNews(@RequestParam Integer page,
                                 @RequestParam Integer news_per_page){
        return newsService.findAll(page,news_per_page).map(this::toDTO);
    }

    @GetMapping("/{id}")
    public CommentedNewsDTO getNewsById(@PathVariable("id") int id,
                               @RequestParam Integer page,
                               @RequestParam Integer comms_per_page){
        return toDTO(newsService.findOne(id), id, page, comms_per_page);
    }



    //DTO converters
    private NewsDTO toDTO (News news){
       return modelMapper.map(news, NewsDTO.class);
    }

    private CommentedNewsDTO toDTO(News news, int id, int page, int comms_per_page){
        CommentedNewsDTO commentedNewsDTO =  modelMapper.map(news, CommentedNewsDTO.class);
        commentedNewsDTO.setComments(newsService
                .getCommentsByNewsId(id, page, comms_per_page)
                .map(comm -> modelMapper.map(comm, CommentDTO.class)));
        return commentedNewsDTO;
    }
    private News toNews(NewsDTO newsDTO) {
        return modelMapper.map(newsDTO ,News.class);
    }
}
