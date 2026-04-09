package com.fin.like.controller;

import com.fin.like.common.response.ApiResponse;
import com.fin.like.dto.LikeRequest;
import com.fin.like.dto.LikeResponse;
import com.fin.like.dto.PagedResult;
import com.fin.like.service.LikeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // 查詢某個 user 的 like 清單（原有功能保留）
    @GetMapping
    public ApiResponse<List<LikeResponse>> getLikeList(@RequestParam String userId) {
        List<LikeResponse> result = likeService.getLikeList(userId);
        return ApiResponse.success(result);
    }

    // 分頁查詢喜好清單（含篩選）
    @GetMapping("/paged")
    public ApiResponse<PagedResult<LikeResponse>> getLikesPaged(
            @RequestParam String userId,
            @RequestParam(required = false) Long productNo,
            @RequestParam(required = false) String account,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResult<LikeResponse> result = likeService.getLikesPaged(userId, productNo, account, page, size);
        return ApiResponse.success(result);
    }

    // 新增 like
    @PostMapping
    public ApiResponse<Void> addLike(@RequestBody LikeRequest request) {
        likeService.addLike(request);
        return ApiResponse.success(null);
    }

    // 修改 like
    @PutMapping("/{sn}")
    public ApiResponse<Void> updateLike(@PathVariable Long sn,
                                        @RequestBody LikeRequest request) {
        likeService.updateLike(sn, request);
        return ApiResponse.success(null);
    }

    // 刪除 like
    @DeleteMapping("/{sn}")
    public ApiResponse<Void> deleteLike(@PathVariable Long sn,
                                        @RequestParam String userId) {
        likeService.deleteLike(sn, userId);
        return ApiResponse.success(null);
    }
}