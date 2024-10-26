package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.group.*;
import com.swd392.mentorbooking.email.EmailDetail;
import com.swd392.mentorbooking.email.EmailService;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.exception.auth.InvalidAccountException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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

    @Autowired
    NotificationRepository notificationRepository;


    @PersistenceContext
    private EntityManager entityManager;

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
                        group.getStudents().stream().map(Account::getId).collect(Collectors.toList()),
                        group.getQuantityMember(),
                        group.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // Return the list of GroupResponse objects in a Response object
        return new Response<>(200, "Groups retrieved successfully", groupResponses);
    }

    public Response<GroupResponse> createGroup(@Valid GroupRequest groupRequest) {

        // Get the current account of the group creator
        Account currentAccount = accountUtils.getCurrentAccount();
        if (currentAccount == null) {
            return new Response<>(401, "Please login first", null);
        }

        // Add the group creator's ID to the studentIds list if it doesn't already exist
        if (!groupRequest.getStudentIds().contains(currentAccount.getId())) {
            groupRequest.getStudentIds().add(currentAccount.getId());
        }

        // Find list of Accounts by studentIds
        List<Account> accounts = accountRepository.findByIdIn(groupRequest.getStudentIds());

        // Check if the number of accounts does not match the list of studentIds in the request
        if (accounts.size() != groupRequest.getStudentIds().size()) {
            throw new InvalidAccountException("One or more account IDs are invalid or do not exist in the database.");
        }

        // Find group topics
        Topic topic = topicRepository.findById(groupRequest.getTopicId()).orElseThrow(() -> new NotFoundException("Topic not found"));

        // Create new Group and set up information fields
        Group group = new Group();
        group.setTopicId(topic.getId());
        group.setStudents(accounts);  // List of accounts including group creator
        group.setQuantityMember(accounts.size());
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        group.setIsDeleted(false);

        // Save group to database
        try {
            groupRepository.save(group);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the group, please try again...");
        }

        // Create GroupResponse
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                groupRequest.getStudentIds(),
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
        existingGroup.setStudents(accounts);
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

    @Transactional
    public Response<GroupResponse> addAccountToGroup(AddMemberRequest addMemberRequest) {
        Account sender = accountUtils.getCurrentAccount();
        if (sender == null) return new Response<>(401, "Please login first", null);

        Group group;
        Optional<Group> existingGroup = groupRepository.findByStudentsContaining(sender);

        if (existingGroup.isPresent()) {
            group = existingGroup.get();
        } else {
            group = new Group();
            group.setStudents(new ArrayList<>());
            group.getStudents().add(entityManager.merge(sender));
            sender.setGroup(group);
            group.setTopicId(null);
            group.setQuantityMember(1);
            group.setCreatedAt(LocalDateTime.now());
            group.setUpdatedAt(LocalDateTime.now());
            group.setIsDeleted(false);
            group = groupRepository.save(group);
            accountRepository.save(sender);
        }

        if (group.getIsDeleted()) {
            return new Response<>(400, "Cannot add account to a deleted group.", null);
        }

        Optional<Account> accountOpt = accountRepository.findByEmail(addMemberRequest.getEmail());
        String token = UUID.randomUUID().toString();


        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(addMemberRequest.getEmail());


        if (accountOpt.isEmpty()) {
            emailDetail.setAttachment("https://circuit-project.vercel.app/signUp");
            emailDetail.setName("User");
        } else {
            Account account = entityManager.merge(accountOpt.get());
            String joinLink = "https://circuit-project.vercel.app/joinGroup?email=" + addMemberRequest.getEmail()
                    + "&groupId=" + group.getId() + "&token=" + token;
            emailDetail.setAttachment(joinLink);
            emailDetail.setName(account.getName());

            if (account.getGroup() != null) {
                throw new InvalidAccountException("Account is already a member of the group.");
            }

        }

        // Invitation creation and notification
        Invitation invitation = new Invitation();
        invitation.setSenderEmail(sender.getEmail());
        invitation.setEmail(addMemberRequest.getEmail());
        invitation.setGroup(group);
        invitation.setToken(token);
        invitation.setStatus(BookingStatus.PENDING);
        invitation.setIsDeleted(false);
        invitaionRepository.save(invitation);

        Notification notification = new Notification();
        notification.setAccount(accountOpt.orElse(null));
        notification.setDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        notification.setMessage(invitation.getStatus().getMessage());
        notification.setStatus(invitation.getStatus());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setInvitation(invitation);
        notification.setIsDeleted(false);
        notificationRepository.save(notification);

        emailService.sendEmailJoinGroup(emailDetail);

        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents().stream().map(Account::getId).collect(Collectors.toList()),
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Invitation sent to the account successfully!", groupResponse);
    }

    @Transactional
    public Response<GroupResponse> acceptGroupInvitation(String token) {
        Invitation invitation = invitaionRepository.findByToken(token)
                .orElseThrow(() -> new InvalidAccountException("Invalid or expired invitation token."));

        Group group = groupRepository.findById(invitation.getGroup().getId())
                .orElseThrow(() -> new InvalidAccountException("Group not found."));

        Account account = accountRepository.findByEmail(invitation.getEmail())
                .orElseThrow(() -> new InvalidAccountException("Account not found."));

        if (account.getGroup() != null) {
            throw new InvalidAccountException("Account is already a member of the group.");
        }

        group.getStudents().add(account);
        group.setQuantityMember(group.getStudents().size());
        account.setGroup(group);
        invitation.setStatus(BookingStatus.ACCEPT);
        invitaionRepository.save(invitation);

        groupRepository.save(group);
        accountRepository.save(account);



        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents().stream().map(Account::getId).collect(Collectors.toList()),
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Successfully joined the group!", groupResponse);
    }


    public Response<GroupResponse> selectTopicForGroup(Long topicId) {
        // Get current account from token
        Account currentAccount = accountUtils.getCurrentAccount();
        if (currentAccount == null) {
            return new Response<>(401, "Please login first", null);
        }

        // Find groups the current account is in
        Group group = groupRepository.findByStudentsContaining(currentAccount)
                .orElseThrow(() -> new NotFoundException("No group found for the current account."));

        // Find topic by ID
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic not found with id: " + topicId));

        // Update topic for group
        group.setTopicId(topic.getId());
        group.setUpdatedAt(LocalDateTime.now());

        // Save updated group
        try {
            groupRepository.save(group);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the group topic, please try again...");
        }

        // Tạo đối tượng phản hồi
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents().stream().map(Account::getId).collect(Collectors.toList()),
                group.getQuantityMember(),
                group.getUpdatedAt()
        );

        return new Response<>(200, "Topic updated successfully for the group!", groupResponse);
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
        group.getStudents().remove(account);
        group.getStudents().remove(removeMemberRequest.getAccountId());
        group.setQuantityMember(group.getStudents().size());

        // Save the updated group
        groupRepository.save(group);

        // Create a response entity
        GroupResponse groupResponse = new GroupResponse(
                group.getId(),
                group.getTopicId(),
                group.getStudents().stream().map(Account::getId).collect(Collectors.toList()),
                group.getQuantityMember(),
                group.getCreatedAt()
        );

        return new Response<>(200, "Account removed from group successfully!", groupResponse);
    }
}
