package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.comment.CreateCommentRequestDTO;
import com.swd392.mentorbooking.dto.comment.CreateCommentResponseDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Comment;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.BlogRepository;
import com.swd392.mentorbooking.repository.CommentRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Response<CreateCommentResponseDTO> createComment(long blogId, CreateCommentRequestDTO createCommentRequestDTO) {
        Account account = checkAccount();

        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) {
            return new Response<>(200, "There is no blog with such id", null);
        }

        Comment comment = Comment.builder()
                .blog(blog)
                .account(account)
                .description(createCommentRequestDTO.getComment())
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
            throw new AuthAppException(ErrorCode.TOKEN_EXPIRED);
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return account;
    }
}
