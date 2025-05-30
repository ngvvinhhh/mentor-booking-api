package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.*;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        blog.setBlogCategoryEnum(createBlogRequestDTO.getBlogCategoryEnum());
        blog.setIsDeleted(false);

        // Save blog
        try {
            blogRepository.save(blog);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the blog, please try again...");
        }

        // Create a response entity
        CreateBlogRespnseDTO createBlogRespnseDTO = new CreateBlogRespnseDTO(blog.getId(), blog.getTitle(), blog.getDescription(), blog.getImage(), blog.getCreatedAt(), blog.getBlogCategoryEnum());

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
        if (updateBlogRequestDTO.getBlogCategoryEnum() != null)
            blog.setBlogCategoryEnum(updateBlogRequestDTO.getBlogCategoryEnum());

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

        return new Response<>(202, "Blog deleted successfully", data);
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

    public Response<List<GetBlogResponseDTO>> viewAllBlogs() {
        // Get data
        List<Blog> allBlogs = blogRepository.findAllByIsDeletedFalse();
        List<GetBlogResponseDTO> data = new ArrayList<>();

        for (Blog blog : allBlogs) {
            GetBlogResponseDTO getBlogResponseDTO = returnOneBlogResponseData(blog);
            data.add(getBlogResponseDTO);
        }

        if (data.isEmpty()) {
            //Response message
            String message = "No blog were found!";
            return new Response<>(200, message, data);
        }

        //Response message
        String message = "Retrieve blogs successfully!";
        return new Response<>(200, message, data);
    }

    public Response<GetBlogResponseDTO> viewBlogById(long blogId) {
        // Get data
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            //Response message
            String message = "No blog found with this id: " + blogId + "!";
            return new Response<>(200, message, null);
        }

        GetBlogResponseDTO data = returnOneBlogResponseData(blog);

        if (data == null) {
            //Response message
            String message = "No blog found with this id: " + blogId + "!";
            return new Response<>(200, message, data);
        }

        //Response message
        String message = "Retrieve blogs successfully!";
        return new Response<>(200, message, data);
    }

    public Response<List<GetBlogResponseDTO>> getAllBlogOfCurrentUser() {
        Account account = checkAccount();

        // Get data
        List<Blog> allBlogs = blogRepository.findAllByAccountAndIsDeletedFalse(account);

        if (allBlogs.isEmpty()) {
            return new Response<>(200, "No blogs found!", Collections.emptyList());
        }

        // Convert blogs to DTOs
        List<GetBlogResponseDTO> data = allBlogs.stream()
                .map(this::returnOneBlogResponseData)
                .collect(Collectors.toList());

        // Response message
        String message = "Retrieve blogs successfully!";
        return new Response<>(200, message, data);
    }


    public Response<List<GetBlogResponseDTO>> getFeaturedBlog() {
        // Get data
        List<Blog> allBlogs = blogRepository.findRandomBlogs();

        List<GetBlogResponseDTO> data = new ArrayList<>();

        for (Blog blog : allBlogs) {
            GetBlogResponseDTO getBlogResponseDTO = returnOneBlogResponseData(blog);
            data.add(getBlogResponseDTO);
        }

        if (data.isEmpty()) {
            //Response message
            String message = "No blog were found!";
            return new Response<>(200, message, data);
        }

        //Response message
        String message = "Retrieve 4 random blogs successfully!";
        return new Response<>(200, message, data);
    }

    public Response<GetBlogResponseDTO> getMostCommentedBlog() {

        Blog blog = blogRepository.findBlogWithMostComments().orElse(null);

        if (blog == null) {
            return new Response<>(404, "There is no blog with that such request!", null);
        }

        GetBlogResponseDTO data = returnOneBlogResponseData(blog);

        //Response message
        String message = "Retrieve blogs successfully!";
        return new Response<>(200, message, data);
    }

    private GetBlogResponseDTO returnOneBlogResponseData(Blog blog) {
        List<GetCommentResponseDTO> comments = blog.getComments().stream()
                .filter(comment -> !comment.isDeleted())
                .map(comment -> GetCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorId(comment.getAccount().getId())
                        .authorName(comment.getAccount().getName())
                        .authorAvatarUrl(comment.getAccount().getAvatar())
                        .description(comment.getDescription())
                        .build())
                .collect(Collectors.toList());

        return GetBlogResponseDTO.builder()
                .id(blog.getId())
                .authorId(blog.getAccount().getId())
                .authorName(blog.getAccount().getName())
                .authorAvatarUrl(blog.getAccount().getAvatar())
                .title(blog.getTitle())
                .image(blog.getImage())
                .category(blog.getBlogCategoryEnum())
                .description(blog.getDescription())
                .likeCount(blog.getLikeCount())
                .createdAt(blog.getCreatedAt())
                .isDeleted(blog.getIsDeleted())
                .comments(comments)
                .build();
    }


    public Response<List<GetBlogResponseDTO>> getBlogsByCategory(BlogCategoryEnum category) {
        // Get data
        List<Blog> blogs = blogRepository.findBlogsByBlogCategoryEnum(category);

        if (blogs.isEmpty()) {
            return new Response<>(200, "No blogs with category " + category + " found!", null);
        }
        List<GetBlogResponseDTO> data = new ArrayList<>();

        for (Blog blog : blogs) {
            GetBlogResponseDTO getBlogResponseDTO = returnOneBlogResponseData(blog);
            data.add(getBlogResponseDTO);
        }

        //Response message
        String message = "Retrieve blogs successfully!";
        return new Response<>(200, message, data);
    }
}
