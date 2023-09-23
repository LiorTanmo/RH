package com.lior.applicaton.rh_test.controllers;

//TODO Testing and acceess

import com.lior.applicaton.rh_test.dto.CommentDTO;
import com.lior.applicaton.rh_test.dto.UserDTO;
import com.lior.applicaton.rh_test.model.Comment;
import com.lior.applicaton.rh_test.services.CommentsService;
import com.lior.applicaton.rh_test.services.NewsService;
import com.lior.applicaton.rh_test.util.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/news/{id}/comments")
public class CommentsController {

    private final CommentsService commentsService;
    private final ErrorPrinter errorPrinter;
    private final ModelMapper modelMapper;

    public CommentsController(CommentsService commentsService, ErrorPrinter errorPrinter, ModelMapper modelMapper) {
        this.commentsService = commentsService;
        this.errorPrinter = errorPrinter;
        this.modelMapper = modelMapper;
    }
    @PostMapping
    public ResponseEntity<HttpStatus> addComment(@RequestBody @Valid CommentDTO commentDTO,
                                                 BindingResult bindingResult,
                                                 @PathVariable(name = "id") int id){
        if (bindingResult.hasErrors()) errorPrinter.printErrors(bindingResult);
        commentsService.addComment(toComment(commentDTO), id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public Page<CommentDTO> getComments(@PathVariable(name = "id") int news_id,
                                        @RequestParam Integer page,
                                        @RequestParam Integer comments_per_page) {
        return commentsService.findAll(page, comments_per_page, news_id).map(this::toDTO);
    }
    @PatchMapping
    public ResponseEntity<HttpStatus> editComment(@RequestBody @Valid CommentDTO commentDTO,
                                                 BindingResult bindingResult,
                                                 @PathVariable(name = "id") int news_id,
                                                  @RequestParam int com_num){
        if (bindingResult.hasErrors()) errorPrinter.printErrors(bindingResult);
        commentsService.editComment(toComment(commentDTO), news_id, com_num);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> removeComment(@PathVariable(name = "id") int news_id, int comm_num){
        commentsService.removeComment(news_id, comm_num);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler (ValidationFailureException e){
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler (NewsNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //DTO Converters
    private CommentDTO toDTO(Comment comment){
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTO.setComment_author(modelMapper.map(comment.getInserted_by(), UserDTO.class));
        return commentDTO;
    }

    private Comment toComment(CommentDTO commentDTO){
        return  modelMapper.map(commentDTO, Comment.class);
    }
}
