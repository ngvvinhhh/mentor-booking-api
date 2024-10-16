package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.invititation.InvitationResponse;
import com.swd392.mentorbooking.entity.Invitation;
import com.swd392.mentorbooking.repository.InvitaionRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationService {

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    InvitaionRepository invitaionRepository;
    public Response<List<InvitationResponse>> getAllInvitationsForCurrentAccount() {
        try {
            // Get the email of the current account
            String currentEmail = accountUtils.getCurrentAccount().getEmail();
            if (currentEmail == null) {
                return new Response<>(401, "Please login first!", null);
            }

            // Get all account invitations based on email
            List<Invitation> invitations = invitaionRepository.findByEmailAndIsDeletedFalse(currentEmail);

            if (invitations.isEmpty()) {
                return new Response<>(404, "No invitations found for the current account", null);
            }

            // Convert List<Invitation> to List<InvitationResponse>
            List<InvitationResponse> invitationResponses = invitations.stream()
                    .map(invitation -> new InvitationResponse(
                            invitation.getId(),
                            invitation.getSenderEmail(), // Gán senderEmail
                            invitation.getStatus(),
                            invitation.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            return new Response<>(200, "Retrieved all invitations successfully!", invitationResponses);
        } catch (Exception e) {
            return new Response<>(500, "Failed to retrieve invitations", null);
        }
    }


    public Response<Void> deleteInvitation(Long invitationId) {
        try {
            // Find invitations by ID
            Invitation invitation = invitaionRepository.findById(invitationId)
                    .orElse(null);

            if (invitation == null) {
                return new Response<>(404, "Invitation not found", null);
            }

            // Check if the invitation has been deleted
            if (invitation.getIsDeleted()) {
                return new Response<>(400, "Invitation has already been deleted", null);
            }

            // Mark invitation as deleted
            invitation.setIsDeleted(true);
            invitaionRepository.save(invitation);

            return new Response<>(200, "Invitation deleted successfully", null);
        } catch (Exception e) {
            return new Response<>(500, "Failed to delete invitation", null);
        }
    }

}
