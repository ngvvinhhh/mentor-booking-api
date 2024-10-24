package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.CreateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRespnseDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogResponseDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.ForbiddenException;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.BlogRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUtils accountUtils;

    // ** BLOG SECTION ** //

    public Response<CreateBlogRespnseDTO> createBlog(@Valid CreateBlogRequestDTO createBlogRequestDTO) {
        // Check the current logged in account
        Account account = checkAccount();

        // Create blog and set fields
        Blog blog = new Blog();
        blog.setTitle(createBlogRequestDTO.getTitle());
        blog.setDescription(createBlogRequestDTO.getDescription());
        blog.setImage(createBlogRequestDTO.getImage());
        blog.setLikeCount(0);
        blog.setAccount(account);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        blog.setIsDeleted(false);

        // Save blog
        try {
            blogRepository.save(blog);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the blog, please try again...");
        }

        // Create a response entity
        CreateBlogRespnseDTO createBlogRespnseDTO = new CreateBlogRespnseDTO(blog.getTitle(), blog.getDescription(), blog.getImage(), blog.getCreatedAt());

        return new Response<>(201, "Blog created successfully!", createBlogRespnseDTO);
    }

    public Response<UpdateBlogResponseDTO> updateBlog(Long blogId, @Valid UpdateBlogRequestDTO updateBlogRequestDTO) {
        // Check the current logged in account
        Account account = checkAccount();

        // Find the service
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) return new Response<>(404, "Service not found", null);
        if (!blog.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This blog does not belong to you to update");
        }

        // Update blog fields
        blog.setUpdatedAt(LocalDateTime.now());
        if (updateBlogRequestDTO.getTitle() != null) blog.setTitle(updateBlogRequestDTO.getTitle());
        if (updateBlogRequestDTO.getDescription() != null) blog.setDescription(updateBlogRequestDTO.getDescription());
        if (updateBlogRequestDTO.getImage() != null) blog.setImage(updateBlogRequestDTO.getImage());

        // Save and handle exceptions
        try {
            blogRepository.save(blog);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the blog, please try again...");
        }

        // Return response
        UpdateBlogResponseDTO data = UpdateBlogResponseDTO.builder()
                .title(blog.getTitle())
                .description(blog.getDescription())
                .image(blog.getImage())
                .updatedAt(LocalDateTime.now())
                .build();
        return new Response<>(202, "Service updated successfully!", data);
    }

    public Response<UpdateBlogResponseDTO> deleteBlog(Long blogId) {
        // Check account
        Account account = checkAccount();

        // Get the blog
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) return new Response<>(404, "Blog not found", null);

        if (!blog.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This blog does not belong to you to delete");
        }

        blog.setIsDeleted(true);
        blogRepository.save(blog);

        // Return response
        UpdateBlogResponseDTO data = UpdateBlogResponseDTO.builder()
                .title(blog.getTitle())
                .is_deleted(blog.getIsDeleted())
                .build();

        return new Response<>(202, "Blog deleted successfully", null);
    }

    private Account checkAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return account;
    }

    public Response<List<Blog>> viewAllBlogs() {
        // Get data
        List<Blog> data = blogRepository.findAll();


        if (data.isEmpty()) {
            //Response message
            String message = "No blog were found!";
            return new Response<>(200, message, data);
        }

        //Response message
        String message = "Retrieve topics successfully!";
        return new Response<>(200, message, data);
    }

    public Response<Blog> viewBlogById(long blogId) {
        // Get data
        Blog data = blogRepository.findById(blogId).orElse(null);

        if (data == null) {
            //Response message
            String message = "No blog found with this id: " + blogId + "!";
            return new Response<>(200, message, data);
        }

        //Response message
        String message = "Retrieve topics successfully!";
        return new Response<>(200, message, data);
    }

    public Response<List<Blog>> getAllBlogOfCurrentUser() {
        Account account = checkAccount();
        // Get data
        List<Blog> data = blogRepository.findAllByAccount(account).orElse(new ArrayList<>());

        if (data.isEmpty()) {
            //Response message
            String message = "You have not posted any blog!";
            return new Response<>(200, message, data);
        }

        //Response message
        String message = "Retrieve topics successfully!";
        return new Response<>(200, message, data);

    }
}
