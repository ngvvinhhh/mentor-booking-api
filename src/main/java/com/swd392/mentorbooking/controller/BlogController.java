package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.*;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.service.BlogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin("**")
@SecurityRequirement(name = "api")
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // ** CATEGORY SECTION ** //

    @GetMapping("/category/get-all")
    public Response<List<BlogCategoryEnum>> getAllBlogCategory() {
        List<BlogCategoryEnum> categoryList = new ArrayList<>(Arrays.asList(BlogCategoryEnum.values()));
        return new Response<>(200, "Retrieve blog's category successfully", categoryList);
    }

    // ** BLOG SECTION ** //

    // View all blogs
    @GetMapping("/view/all")
    public Response<List<GetBlogResponseDTO>> getAllBlog() {
        return blogService.viewAllBlogs();
    }

    // View current account posted blog
    @GetMapping("/view/by-account")
    public Response<List<GetBlogResponseDTO>> getAllBlogOfCurrentUser() {
        return blogService.getAllBlogOfCurrentUser();
    }

    // View blog by id
    @GetMapping("view/{blogId}")
    public Response<GetBlogResponseDTO> getBlog(@PathVariable long blogId) {
        return blogService.viewBlogById(blogId);
    }

    // Create a blog
    @PostMapping("/create")
    public Response<CreateBlogRespnseDTO> createBlog(@Valid @RequestBody CreateBlogRequestDTO createBlogRequestDTO) {
        return blogService.createBlog(createBlogRequestDTO);
    }

    // Update a blog
    @PutMapping("/update/{blogId}")
    public Response<UpdateBlogResponseDTO> updateService(@PathVariable Long blogId, @Valid @RequestBody UpdateBlogRequestDTO updateServiceRequestDTO) {
        return blogService.updateBlog(blogId, updateServiceRequestDTO);
    }

    //Delete a blog
    @DeleteMapping("/delete/{blogId}")
    public Response<UpdateBlogResponseDTO> deleteBlog(@PathVariable Long blogId) {
        return blogService.deleteBlog(blogId);
    }

}
