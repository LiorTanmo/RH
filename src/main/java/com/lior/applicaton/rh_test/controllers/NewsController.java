package com.lior.applicaton.rh_test.controllers;

import com.lior.applicaton.rh_test.dto.CommentDTO;
import com.lior.applicaton.rh_test.dto.NewsDTO;
import com.lior.applicaton.rh_test.dto.UserDTO;
import com.lior.applicaton.rh_test.model.News;
import com.lior.applicaton.rh_test.services.NewsService;
import com.lior.applicaton.rh_test.util.ErrorPrinter;
import com.lior.applicaton.rh_test.util.ErrorResponse;
import com.lior.applicaton.rh_test.util.NewsNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;


//TODO
@RestController
@RequestMapping("/news")
@AllArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final ModelMapper modelMapper;
    private  final ErrorPrinter errorPrinter;

    @PostMapping("/post")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid NewsDTO newsDTO,
                                             BindingResult bindingResult){
        errorPrinter.printFieldErrors(bindingResult);
        newsService.save(toNews(newsDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> edit(@RequestBody @Valid NewsDTO newsDTO,
                                           BindingResult bindingResult,
                                           @PathVariable(name = "id") int id){
        errorPrinter.printFieldErrors(bindingResult);
        newsService.update(id, toNews(newsDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping()
    public Page<NewsDTO> getNews(@RequestParam Integer page,
                                 @RequestParam Integer news_per_page){
        return newsService.findAll(page,news_per_page).map(this::toDTO);
    }

    @GetMapping("/{id}")
    public NewsDTO getNewsById(@PathVariable("id") int id,
                               @RequestParam Integer page,
                               @RequestParam Integer comms_per_page){
        return toDTO(newsService.findOne(id), id, page, comms_per_page);
    }

    @GetMapping("/search")
    public List<NewsDTO> newsSearch(@RequestParam String query){
        return newsService.search(query).stream().map((this::toDTO)).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable(name = "id") int id){
        newsService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler (NewsNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    //DTO converters
    private NewsDTO toDTO (News news){
       return modelMapper.map(news, NewsDTO.class);
    }

    private NewsDTO toDTO(News news, int id, int page, int comms_per_page){
        NewsDTO NewsDTO =  modelMapper.map(news, NewsDTO.class);
        NewsDTO.setComments(newsService
                .getCommentsByNewsId(id, page, comms_per_page)
                .map((comm) -> {
                    CommentDTO comDTO = modelMapper.map(comm, CommentDTO.class);
                    comDTO.setComment_author(modelMapper.map(comm.getInserted_by(), UserDTO.class));
                    return comDTO;
                }));
        return NewsDTO;
    }

    private News toNews(NewsDTO newsDTO) {
        return modelMapper.map(newsDTO, News.class);
    }
}
