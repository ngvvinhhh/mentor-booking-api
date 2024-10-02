package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.topic.TopicRequest;
import com.swd392.mentorbooking.dto.topic.TopicResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.topic.TopicException;
import com.swd392.mentorbooking.repository.TopicRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopicService {

    @Autowired TopicRepository topicRepository;

    @Autowired
    AccountUtils accountUtils;

    public Topic getTopicById(Long id) {
        return topicRepository.findById(id).orElseThrow(() -> new TopicException(("The topic with id " + id + "doesn't exist"), ErrorCode.TOPICS_NOT_FOUND));
    }

    public List<Topic> getAllTopics () {
        return topicRepository.findStudentsByIsDeletedFalse();
    }

    public TopicResponse createTopic (TopicRequest topicRequest){
        Account account = accountUtils.getCurrentAccount();

        LocalDateTime dateTime = LocalDateTime.now();
        Topic topic = new Topic();
        topic.setTopicName(topicRequest.getTopicName());
        topic.setDescription(topicRequest.getDescription());
        topic.setAccount(account);
        topic.setCreatedAt(dateTime);
        topic.setIsDeleted(false);
        topicRepository.save(topic);
        return new TopicResponse(topic.getId(), topic.getTopicName(), topic.getDescription(), topic.getCreatedAt());
    }

}
