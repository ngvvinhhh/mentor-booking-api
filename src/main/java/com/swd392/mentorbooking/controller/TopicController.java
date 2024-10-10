package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.CreateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRespnseDTO;
import com.swd392.mentorbooking.dto.topic.TopicRequest;
import com.swd392.mentorbooking.dto.topic.TopicResponse;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
@CrossOrigin("**")
public class TopicController {
    @Autowired
    TopicService topicService;

    @PostMapping("create")
    public Response<TopicResponse> createTopic(@Valid @RequestBody TopicRequest topicRequest){
        return topicService.createTopic(topicRequest);
    }

    @GetMapping("view")
    public ResponseEntity getAllTopics(){
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }
}
