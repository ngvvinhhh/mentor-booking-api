package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.group.AddMemberRequest;
import com.swd392.mentorbooking.dto.group.GroupRequest;
import com.swd392.mentorbooking.dto.group.GroupResponse;
import com.swd392.mentorbooking.dto.group.UpdateGroupResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Group;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.exception.auth.InvalidAccountException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.GroupRepository;
import com.swd392.mentorbooking.repository.TopicRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TopicRepository topicRepository;

    public Response<List<GroupResponse>> getAllGroups() {
        // Fetch all groups that are not deleted
        List<Group> groups = groupRepository.findByIsDeletedFalse();

        // Check if groups are found
        if (groups.isEmpty()) {
            throw new NotFoundException("No groups found.");
        }

        // Convert Group entities to GroupResponse DTOs
        List<GroupResponse> groupResponses = groups.stream()
                .map(group -> new GroupResponse(
                        group.getId(),
                        group.getTopicId(),
                        group.getStudents(),
                        group.getQuantityMember(),
                        group.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // Return the list of GroupResponse objects in a Response object
        return new Response<>(200, "Groups retrieved successfully", groupResponses);
    }

    public Response<GroupResponse> createGroup(@Valid GroupRequest groupRequest) {

        // Find list of Accounts
        List<Account> accounts = accountRepository.findByIdIn(groupRequest.getStudentIds());

        // Check if the number of accounts retrieved is different from the number of accountId in the request
        if (accounts.size() != groupRequest.getStudentIds().size()) {
            throw new InvalidAccountException("One or more account IDs are invalid or do not exist in the database.");
        }

        // Find topic of group
        Topic topic = topicRepository.findById(groupRequest.getTopicId()).orElseThrow(null);
        if (topic == null) return new Response<>(404, "Topic not found", null);



        // Create group and set fields
        Group group = new Group();
        group.setTopicId(topic.getId());
        group.setStudents(groupRequest.getStudentIds());
        group.setAccounts(accounts);
        group.setQuantityMember(accounts.size());
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        group.setIsDeleted(false);

        // Save group
        try {
            groupRepository.save(group);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the group, please try again...");
        }

        // Create a response entity
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents(),
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Group created successfully!", groupResponse);
    }

    public Response<UpdateGroupResponse> updateGroup(Long groupId, @Valid GroupRequest groupRequest) {

        // Find the existing group by ID
        Group existingGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + groupId));

        // Find list of Accounts
        List<Account> accounts = accountRepository.findByIdIn(groupRequest.getStudentIds());

        // Check if the number of accounts retrieved is different from the number of accountId in the request
        if (accounts.size() != groupRequest.getStudentIds().size()) {
            throw new InvalidAccountException("One or more account IDs are invalid or do not exist in the database.");
        }

        // Find the new topic for the group (if provided)
        Topic topic = topicRepository.findById(groupRequest.getTopicId())
                .orElseThrow(() -> new NotFoundException("Topic not found with id: " + groupRequest.getTopicId()));

        // Update group fields
        existingGroup.setTopicId(topic.getId());
        existingGroup.setStudents(groupRequest.getStudentIds());
        existingGroup.setAccounts(accounts);
        existingGroup.setQuantityMember(accounts.size());
        existingGroup.setUpdatedAt(LocalDateTime.now());

        // Save the updated group
        try {
            groupRepository.save(existingGroup);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the group, please try again...");
        }

        // Create a response entity
        UpdateGroupResponse groupResponse = new UpdateGroupResponse(
                existingGroup.getId(),
                existingGroup.getTopicId(),
                existingGroup.getStudents(),
                existingGroup.getQuantityMember(),
                existingGroup.getUpdatedAt()
        );

        return new Response<>(200, "Group updated successfully!", groupResponse);
    }

    public Response<String> deleteGroup(Long groupId) {
        // Check if group exists
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new CreateServiceException("Group with id " + groupId + " not found.")
        );

        // Soft delete
        group.setIsDeleted(true);
        groupRepository.save(group);

        return new Response<>(200, "Group deleted successfully!", "Deleted group ID: " + groupId);
    }

    public Response<GroupResponse> addAccountToGroup(AddMemberRequest addMemberRequest) {
        // Find the group by ID
        Group group = groupRepository.findById(addMemberRequest.getGroupId())
                .orElseThrow(() -> new InvalidAccountException("Group not found."));

        // Check if the group is deleted
        if (group.getIsDeleted()) {
            throw new InvalidAccountException("Cannot add account to a deleted group.");
        }

        // Find the account by ID
        Account account = accountRepository.findById(addMemberRequest.getAccountId())
                .orElseThrow(() -> new InvalidAccountException("Account not found."));

        // Check if the student is already in the group
        if (group.getStudents().contains(addMemberRequest.getAccountId())) {
            throw new InvalidAccountException("Student is already in the group.");
        }

        // Add the account to the group
        group.getAccounts().add(account);
        group.getStudents().add(addMemberRequest.getAccountId());
        group.setQuantityMember(group.getAccounts().size());

        // Save the updated group
        groupRepository.save(group);

        // Create a response entity
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents(),
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Account added to group successfully!", groupResponse);
    }

    public Response<GroupResponse> removeAccountFromGroup(AddMemberRequest removeMemberRequest) {
        // Find the group by ID
        Group group = groupRepository.findById(removeMemberRequest.getGroupId())
                .orElseThrow(() -> new InvalidAccountException("Group not found."));

        // Check if the group is deleted
        if (group.getIsDeleted()) {
            throw new InvalidAccountException("Cannot remove account from a deleted group.");
        }

        // Find the account by ID
        Account account = accountRepository.findById(removeMemberRequest.getAccountId())
                .orElseThrow(() -> new InvalidAccountException("Account not found."));

        // Check if the student is already in the group
        if (!group.getStudents().contains(removeMemberRequest.getAccountId())) {
            throw new InvalidAccountException("Student is not in the group.");
        }

        // Remove the account from the group
        group.getAccounts().remove(account);
        group.getStudents().remove(removeMemberRequest.getAccountId());
        group.setQuantityMember(group.getAccounts().size());

        // Save the updated group
        groupRepository.save(group);

        // Create a response entity
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents(),
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Account removed from group successfully!", groupResponse);
    }



}
