package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.comment.CreateCommentRequestDTO;
import com.swd392.mentorbooking.dto.comment.CreateCommentResponseDTO;
import com.swd392.mentorbooking.entity.Comment;
import com.swd392.mentorbooking.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("**")
@SecurityRequirement(name = "api")
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Create comment
    @PostMapping("/create/{blogId}")
    public Response<CreateCommentResponseDTO> createComment(@PathVariable("blogId") long blogId, @RequestBody CreateCommentRequestDTO createCommentRequestDTO) {
        return commentService.createComment(blogId, createCommentRequestDTO);
    }

    // Update comment

    // Delete comment
}
