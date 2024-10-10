package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.CreateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRespnseDTO;
import com.swd392.mentorbooking.dto.topic.TopicRequest;
import com.swd392.mentorbooking.dto.topic.TopicResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.exception.topic.TopicException;
import com.swd392.mentorbooking.repository.SemesterRepository;
import com.swd392.mentorbooking.repository.TopicRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopicService {

    @Autowired TopicRepository topicRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    AccountUtils accountUtils;


    public Topic getTopicById(Long id) {
        return topicRepository.findById(id).orElseThrow(() -> new TopicException(("The topic with id " + id + "doesn't exist"), ErrorCode.TOPICS_NOT_FOUND));
    }

    public List<Topic> getAllTopics () {
        return topicRepository.findStudentsByIsDeletedFalse();
    }

    public Response<TopicResponse> createTopic(@Valid TopicRequest topicRequest) {

        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);





        // Create topic and set fields
        Topic topic = new Topic();
        topic.setTopicName(topicRequest.getTopicName());
        topic.setDescription(topicRequest.getDescription());
        topic.setSemester(semesterRepository.findById(topicRequest.getSemesterId()).orElseThrow(()
                -> new TopicException(("The semester with id " + topicRequest.getSemesterId() + "doesn't exist"), ErrorCode.SEMESTER_NOT_FOUND)));
        topic.setAccount(account);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        topic.setIsDeleted(false);

        // Save topic
        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the topic, please try again...");
        }

        // Create a response entity
        TopicResponse topicResponse = new TopicResponse(
                topic.getId(),
                topic.getTopicName(),
                topic.getDescription(),
                topic.getCreatedAt()
        );

        return new Response<>(200, "Topic created successfully!", topicResponse);
    }


}
