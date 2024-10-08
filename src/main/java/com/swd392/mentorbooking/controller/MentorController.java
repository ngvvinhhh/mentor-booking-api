package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.mentor.UpdateSocialLinkRequestDTO;
import com.swd392.mentorbooking.dto.mentor.UpdateSocialLinkResponseDTO;
import com.swd392.mentorbooking.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("**")
@RequestMapping("/mentor")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    @PostMapping("/social-update")
    public ResponseEntity<Response<UpdateSocialLinkResponseDTO>> updateSocialLink(
            @RequestBody UpdateSocialLinkRequestDTO updateSocialLinkRequestDTO) {
        Response<UpdateSocialLinkResponseDTO> response = mentorService.updateSocialLink(updateSocialLinkRequestDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
