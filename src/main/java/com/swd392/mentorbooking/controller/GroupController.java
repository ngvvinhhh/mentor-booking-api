package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.group.AddMemberRequest;
import com.swd392.mentorbooking.dto.group.GroupRequest;
import com.swd392.mentorbooking.dto.group.GroupResponse;
import com.swd392.mentorbooking.dto.group.UpdateGroupResponse;
import com.swd392.mentorbooking.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/group")
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
    public Response<GroupResponse> RemoveAccountToGroup(@Valid @RequestBody AddMemberRequest removeMemberRequest) {
        return groupService.removeAccountFromGroup(removeMemberRequest);
    }

}
