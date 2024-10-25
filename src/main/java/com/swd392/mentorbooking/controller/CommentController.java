package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.comment.CreateCommentRequestDTO;
import com.swd392.mentorbooking.dto.comment.CreateCommentResponseDTO;
import com.swd392.mentorbooking.dto.comment.UpdateCommentRequestDTO;
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
    @PostMapping("/create")
    public Response<CreateCommentResponseDTO> createComment(@RequestBody CreateCommentRequestDTO createCommentRequestDTO) {
        return commentService.createComment(createCommentRequestDTO);
    }

    // Update comment
    @PutMapping("/update/{commentId}")
    public Response<CreateCommentResponseDTO> updateComment(@PathVariable Long commentId, @RequestBody UpdateCommentRequestDTO updateCommentRequestDTO) {
        return commentService.updateComment(commentId, updateCommentRequestDTO);
    }

    // Delete comment
    @DeleteMapping("/delete/{commentId}")
    public Response deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
