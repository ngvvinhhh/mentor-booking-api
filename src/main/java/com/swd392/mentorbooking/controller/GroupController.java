package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.invititation.AcceptInviteRequest;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.group.*;
import com.swd392.mentorbooking.service.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/group")
@SecurityRequirement(name = "api")
public class GroupController {
    @Autowired
    GroupService groupService;

    @GetMapping("view")
    public Response<List<GroupResponse>> getAllGroups() {
        return groupService.getAllGroups();
    }

    @PostMapping("create")
    public Response<GroupResponse> createGroup(@Valid @RequestBody GroupRequest groupRequest){
        return groupService.createGroup(groupRequest);
    }

    @PutMapping("update/{groupId}")
    public Response<UpdateGroupResponse> updateTopic(@PathVariable Long groupId, @Valid @RequestBody GroupRequest groupRequest) {
        return groupService.updateGroup(groupId, groupRequest);
    }

    @DeleteMapping("delete/{groupId}")
    public Response<String> deleteGroup(@PathVariable Long groupId) {
        return groupService.deleteGroup(groupId);
    }

    @PostMapping("/add-account")
    public Response<GroupResponse> addAccountToGroup(@Valid @RequestBody AddMemberRequest addMemberRequest) {
        return groupService.addAccountToGroup(addMemberRequest);
    }

    @PostMapping("/remove-account")
    public Response<GroupResponse> RemoveAccountToGroup(@Valid @RequestBody RemoveMemberRequest removeMemberRequest) {
        return groupService.removeAccountFromGroup(removeMemberRequest);
    }

    @PostMapping("/accept-invite")
    public Response<GroupResponse> acceptGroupInvitation(@Valid @RequestBody AcceptInviteRequest acceptInviteRequest) {
        return groupService.acceptGroupInvitation(
                acceptInviteRequest.getToken()
        );
    }

    @GetMapping("/select-topic/{topicId}")
    public Response<GroupResponse> rejectBooking(@PathVariable Long topicId) {
        return groupService.selectTopicForGroup(topicId);
    }

}
