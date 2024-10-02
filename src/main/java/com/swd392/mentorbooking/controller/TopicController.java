package com.swd392.mentorbooking.controller;

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
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody TopicRequest topicRequest){
        TopicResponse newTopic = topicService.createTopic(topicRequest);
        return ResponseEntity.ok(newTopic);
    }

    @GetMapping("view")
    public ResponseEntity getAllTopics(){
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }
}
