package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.group.*;
import com.swd392.mentorbooking.email.EmailDetail;
import com.swd392.mentorbooking.email.EmailService;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.InviteStatus;
import com.swd392.mentorbooking.entity.Group;
import com.swd392.mentorbooking.entity.Invitation;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.exception.auth.InvalidAccountException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.GroupRepository;
import com.swd392.mentorbooking.repository.InvitaionRepository;
import com.swd392.mentorbooking.repository.TopicRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    EmailService emailService;

    @Autowired
    InvitaionRepository invitaionRepository;

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

        // Check if the group has been deleted
        if (group.getIsDeleted()) {
            throw new InvalidAccountException("Cannot add account to a deleted group.");
        }

        // Find account by email
        Account account = accountRepository.findByEmail(addMemberRequest.getEmail())
                .orElseThrow(() -> new InvalidAccountException("Account not found."));

        // Check if the account is already in the group
        if (group.getStudents().contains(account.getId())) {
            throw new InvalidAccountException("Account is already a member of the group.");
        }

        // Generate a unique token for the invitation (could use UUID or similar)
        String token = UUID.randomUUID().toString();
        String joinLink = "https://circuit-project.vercel.app/joinGroup?email=" + addMemberRequest.getEmail() + "&groupId=" + group.getId() + "&token=" + token;

        // Store the invitation with PENDING status in a separate table or map
        Invitation invitation = new Invitation();
        invitation.setEmail(account.getEmail());
        invitation.setGroup(group);
        invitation.setToken(token);
        invitation.setStatus(InviteStatus.PENDING);
        invitaionRepository.save(invitation); // Make sure to create an InvitationRepository

        // Send invitation email
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(account.getEmail());
        emailDetail.setName(account.getName());
        emailDetail.setAttachment(joinLink);
        emailService.sendEmailJoinGroup(emailDetail);

        // Create a response entity
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents(), // List of member IDs
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Invitation sent to the account successfully!", groupResponse);
    }

    public Response<GroupResponse> acceptGroupInvitation(String email, Long groupId, String token) {
        // Find the invitation by email, groupId, and token
        Invitation invitation = invitaionRepository.findByToken(token)
                .orElseThrow(() -> new InvalidAccountException("Invalid or expired invitation token."));

        // Find the group associated with the invitation
        Group group = groupRepository.findById(invitation.getGroup().getId())
                .orElseThrow(() -> new InvalidAccountException("Group not found."));

        // Find the account associated with the invitation email
        Account account = accountRepository.findByEmail(invitation.getEmail())
                .orElseThrow(() -> new InvalidAccountException("Account not found."));

        // Check if the account is already a member
        if (group.getStudents().contains(account.getId())) {
            throw new InvalidAccountException("Account is already a member of the group.");
        }

        // Add the student's account ID to the group
        group.getStudents().add(account.getId());
        group.setQuantityMember(group.getStudents().size());

        // Update the invitation status to ACCEPTED
        invitation.setStatus(InviteStatus.ACCEPT);
        invitaionRepository.save(invitation);

        // Save the updated group
        groupRepository.save(group);

        // Create a response entity
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents(), // List of member IDs
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Successfully joined the group!", groupResponse);
    }



    public Response<GroupResponse> removeAccountFromGroup(RemoveMemberRequest removeMemberRequest) {
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
