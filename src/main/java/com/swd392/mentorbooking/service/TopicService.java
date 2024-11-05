package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.topic.TopicRequest;
import com.swd392.mentorbooking.dto.topic.TopicResponse;
import com.swd392.mentorbooking.dto.topic.UpdateTopicRequest;
import com.swd392.mentorbooking.dto.topic.UpdateTopicResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Semester;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.exception.topic.TopicException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.SemesterRepository;
import com.swd392.mentorbooking.repository.TopicRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
public class TopicService {

    @Autowired TopicRepository topicRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    AccountUtils accountUtils;
    @Autowired
    private AccountRepository accountRepository;


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

        // Fetch the current semester
        Semester semester = semesterRepository.findByIsCurrentSemesterTrue()
                .orElseThrow(() -> new NotFoundException("Current semester not found."));

        // Create topic and set fields
        Topic topic = new Topic();
        topic.setTopicName(topicRequest.getTopicName());
        topic.setDescription(topicRequest.getDescription());
        topic.setSemester(semester);
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

    public Response<UpdateTopicResponse> updateTopic(Long topicId, @Valid UpdateTopicRequest updateTopicRequest) {

        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        // Find the topic
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) return new Response<>(404, "Topic not found", null);

        // Fetch the current semester
        Semester semester = semesterRepository.findByIsCurrentSemesterTrue()
                .orElseThrow(() -> new NotFoundException("Current semester not found."));

        // Update service fields
        topic.setUpdatedAt(LocalDateTime.now());
        if (updateTopicRequest.getTopicName() != null) topic.setTopicName(updateTopicRequest.getTopicName());
        if (updateTopicRequest.getDescription() != null) topic.setDescription(updateTopicRequest.getDescription());

        // Set the semester to the current semester
        topic.setSemester(semester);

        // Save and handle exceptions
        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            throw new TopicException("There was something wrong when updating the topic, please try again...", ErrorCode.TOPICS_NOT_FOUND);
        }

        // Return response
        UpdateTopicResponse data = new UpdateTopicResponse(
                topic.getId(),
                topic.getTopicName(),
                topic.getDescription(),
                topic.getUpdatedAt()
        );
        return new Response<>(200, "Topic updated successfully!", data);
    }


    public Response<String> deleteTopic(Long topicId) {

        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        // Find the topic by ID
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) return new Response<>(404, "Topic not found", null);

        // Check if the topic is already deleted
        if (topic.getIsDeleted()) return new Response<>(400, "Topic is already deleted", null);

        // Soft delete: Set 'isDeleted' to true and update 'updatedAt'
        topic.setIsDeleted(true);
        topic.setUpdatedAt(LocalDateTime.now());

        // Save the changes
        try {
            topicRepository.save(topic);
        } catch (Exception e) {
            throw new TopicException("There was something wrong when deleting the topic, please try again...", ErrorCode.TOPICS_NOT_FOUND);
        }

        // Return success response
        return new Response<>(200, "Topic deleted successfully!", "Topic with ID " + topicId + " was soft deleted.");
    }

    public Response<String> addTopicsFromExcel(MultipartFile file) {
        Account account = accountUtils.getCurrentAccount(); // Get current account information
        if (account == null) {
            return new Response<>(401, "Please login first", null); // Return response if not logged in
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming you are reading the first sheet
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String topicName = row.getCell(0).getStringCellValue();
                String description = row.getCell(1).getStringCellValue();

                // Fetch the current semester
                Semester semester = semesterRepository.findByIsCurrentSemesterTrue()
                        .orElseThrow(() -> new NotFoundException("Current semester not found."));

                // Create and save Topic
                Topic topic = new Topic();
                topic.setTopicName(topicName);
                topic.setDescription(description);
                topic.setSemester(semester); // Set the current semester
                topic.setAccount(account); // Set the account here
                topic.setCreatedAt(LocalDateTime.now());
                topic.setUpdatedAt(LocalDateTime.now());
                topic.setIsDeleted(false);

                topicRepository.save(topic);
            }
            return new Response<>(200, "Topics added successfully!", null);
        } catch (Exception e) {
            return new Response<>(500, "Failed to process the Excel file: " + e.getMessage(), null);
        }
    }

    public Response<List<TopicResponse>> getAllTopicsByAccount() {
        Account account = checkAccount();

        List<Topic> topicList = topicRepository.findAllByAccountAndIsDeletedFalse(account);
        List<TopicResponse> topicResponseList = new ArrayList<>();
        // Create a response entity
        for (Topic topic : topicList) {
            TopicResponse topicResponse = new TopicResponse(
                    topic.getId(),
                    topic.getTopicName(),
                    topic.getDescription(),
                    topic.getCreatedAt()
            );
            topicResponseList.add(topicResponse);
        }
        return new Response<>(200, "Retrieve data successfully!", topicResponseList);
    }

    public Account checkAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return account;
    }
}

