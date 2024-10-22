package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.CreateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRespnseDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogResponseDTO;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // ** BLOG SECTION ** //

    // View all blogs
    @GetMapping("view/all")
    public Response<List<Blog>> getAllBlog() {
        return blogService.viewAllBlogs();
    }

    // View blog by id
    @GetMapping("view/{blogId}")
    public Response<Blog> getBlog(@PathVariable long blogId) {
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
