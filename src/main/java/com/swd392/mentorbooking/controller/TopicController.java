package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.topic.TopicRequest;
import com.swd392.mentorbooking.dto.topic.TopicResponse;
import com.swd392.mentorbooking.dto.topic.UpdateTopicRequest;
import com.swd392.mentorbooking.dto.topic.UpdateTopicResponse;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.service.TopicService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/topic")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class TopicController {
    @Autowired
    TopicService topicService;

    @PostMapping(value ="/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<TopicResponse> createTopic(@Valid @RequestBody TopicRequest topicRequest){
        return topicService.createTopic(topicRequest);
    }

    @GetMapping("/view")
    public ResponseEntity getAllTopics(){
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/view/by-account")
    public Response<List<TopicResponse>> getAllTopicsByAccount(){
        return topicService.getAllTopicsByAccount();
    }

    @PutMapping("/update/{topicId}")
    public Response<UpdateTopicResponse> updateTopic(@PathVariable Long topicId, @Valid @RequestBody UpdateTopicRequest updateTopicRequest) {
        return topicService.updateTopic(topicId, updateTopicRequest);
    }

    @DeleteMapping("/delete/{topicId}")
    public Response<String> deleteTopic(@PathVariable Long topicId) {
        return topicService.deleteTopic(topicId);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTopics(@RequestParam("file") MultipartFile file) {
        Response<String> response = topicService.addTopicsFromExcel(file);
        return ResponseEntity.status(response.getCode()).body(response.getMessage());
    }
}
