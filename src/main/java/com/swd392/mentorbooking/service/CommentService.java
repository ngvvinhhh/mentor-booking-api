package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.comment.CreateCommentRequestDTO;
import com.swd392.mentorbooking.dto.comment.CreateCommentResponseDTO;
import com.swd392.mentorbooking.dto.comment.UpdateCommentRequestDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Comment;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.ForbiddenException;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.auth.InvalidToken;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.BlogRepository;
import com.swd392.mentorbooking.repository.CommentRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BlogRepository blogRepository;

    public Response<CreateCommentResponseDTO> createComment(CreateCommentRequestDTO createCommentRequestDTO) {
        Account account = checkAccount();

        Blog blog = blogRepository.findById(createCommentRequestDTO.getBlogId()).orElse(null);
        if (blog == null) {
            return new Response<>(200, "There is no blog with such id", null);
        }

        Comment comment = Comment.builder()
                .blog(blog)
                .account(account)
                .description(createCommentRequestDTO.getComment())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        CreateCommentResponseDTO data = CreateCommentResponseDTO.builder()
                .commentId(comment.getId())
                .blogId(blog.getId())
                .comment(comment.getDescription())
                .author(account.getName())
                .build();

        return new Response<>(200, "Retrieve data successfully!", data);
    }
    public Account checkAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        if (!account.isAccountNonExpired()) {
            throw new InvalidToken("Token has expired!");
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return account;
    }

    public Response<CreateCommentResponseDTO> updateComment(Long commentId, UpdateCommentRequestDTO updateCommentRequestDTO) {
        Account account = checkAccount();

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return new Response<>(200, "There is no comment with such id", null);
        }
        if (comment.getAccount() != account) {
            throw new ForbiddenException("This comment does not belong to you to update!");
        }
        if (updateCommentRequestDTO.getComment().isEmpty()) {
            return new Response<>(200, "Comment is required, please input something", null);
        }
        else {
            comment.setDescription(updateCommentRequestDTO.getComment());
            comment.setUpdatedAt(LocalDateTime.now());
        }

        commentRepository.save(comment);

        CreateCommentResponseDTO data = CreateCommentResponseDTO.builder()
                .commentId(comment.getId())
                .blogId(comment.getBlog().getId())
                .comment(comment.getDescription())
                .author(account.getName())
                .build();

        return new Response<>(200, "Update comment successfully", data);
    }

    public Response deleteComment(Long commentId) {
        Account account = checkAccount();

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return new Response<>(200, "There is no comment with such id", null);
        }
        if (comment.getAccount() != account) {
            throw new ForbiddenException("This comment does not belong to you to delete!");
        }
        else {
            comment.setDeleted(true);
            comment.setUpdatedAt(LocalDateTime.now());
        }

        commentRepository.save(comment);

        return new Response<>(200, "Comment deleted successfully", null);
    }
}
