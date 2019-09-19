package com.opinta.controller;

import java.util.List;

import com.opinta.dto.PostOfficeDto;
import com.opinta.service.PostOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/post-offices")
public class PostOfficeController {
    private PostOfficeService postOfficeService;
    private final String POSTOFFICE_DOES_NOT_EXIST = "No PostOffice found for ID %d";

    @Autowired
    public PostOfficeController(PostOfficeService postOfficeService) {
        this.postOfficeService = postOfficeService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<PostOfficeDto> getPostOffices() {
        return postOfficeService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostOffice(@PathVariable("id") long id) {
        PostOfficeDto postOfficeDto = postOfficeService.getById(id);
        if (postOfficeDto == null) {
            return new ResponseEntity<>(format(POSTOFFICE_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(postOfficeDto, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public ResponseEntity<?> createPostOffice(@RequestBody PostOfficeDto postOfficeDto) {
        postOfficeDto = postOfficeService.save(postOfficeDto);
        if (postOfficeDto == null) {
            return new ResponseEntity<>("Failed to create new PostOffice using given data.", BAD_REQUEST);
        }
        return new ResponseEntity<>(postOfficeDto, OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePostOffice(@PathVariable long id, @RequestBody PostOfficeDto postOfficeDto) {
        postOfficeDto = postOfficeService.update(id, postOfficeDto);
        if (postOfficeDto == null) {
            return new ResponseEntity<>(format(POSTOFFICE_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(postOfficeDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostOffice(@PathVariable long id) {
        if (!postOfficeService.delete(id)) {
            return new ResponseEntity<>(format(POSTOFFICE_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
