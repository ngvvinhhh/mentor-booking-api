package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.*;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Lấy tất cả blog chưa bị xoá",
            description = "Phương thức này trả về các blog chưa bị xoá để người dùng đọc.")
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

    // View featured blogs (Max 10)
    @GetMapping("/view/featured")
    public Response<List<GetBlogResponseDTO>> getFeaturedBlog() {
        return blogService.getFeaturedBlog();
    }

    // View most commented blog
    @Operation(summary = "Lấy blog có nhiều bình luận nhất",
            description = "Phương thức này trả về blog có số lượng bình luận lớn nhất từ cơ sở dữ liệu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về blog"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy blog"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/view/most-commented")
    public Response<GetBlogResponseDTO> getMostCommentedBlog() {
        return blogService.getMostCommentedBlog();
    }

    // View blogs by category
    @Operation(summary = "Lấy blog theo category",
            description = "Phương thức này trả các blog có category như đã nhập.")
    @GetMapping("/view/by-category/{category}")
    public Response<List<GetBlogResponseDTO>> getBlogsByCategory(@PathVariable BlogCategoryEnum category) {
        return blogService.getBlogsByCategory(category);
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
