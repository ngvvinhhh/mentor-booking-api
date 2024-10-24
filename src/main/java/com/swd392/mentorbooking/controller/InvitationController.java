package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.invititation.InvitationResponse;
import com.swd392.mentorbooking.service.InvitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitation")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class InvitationController {
    @Autowired
    private InvitationService invitationService;

    @GetMapping("/all")
    public Response<List<InvitationResponse>> getAllInvitations() {
        return  invitationService.getAllInvitationsForCurrentAccount();
    }

    // Delete Invitation
    @DeleteMapping("/{invitationId}")
    public Response<Void> deleteInvitation(@PathVariable Long invitationId) {
        return invitationService.deleteInvitation(invitationId);
    }

}
