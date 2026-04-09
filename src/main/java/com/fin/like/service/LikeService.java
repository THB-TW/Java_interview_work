package com.fin.like.service;

import com.fin.like.common.exception.BusinessException;
import com.fin.like.dto.LikeRequest;
import com.fin.like.dto.LikeResponse;
import com.fin.like.dto.PagedResult;
import com.fin.like.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public List<LikeResponse> getLikeList(String userId) {
        validateUserId(userId);
        return likeRepository.getLikeList(userId);
    }

    /** 分頁查詢喜好清單（含篩選） */
    public PagedResult<LikeResponse> getLikesPaged(String userId, Long productNo, String account, int page, int size) {
        validateUserId(userId);
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        List<LikeResponse> data = likeRepository.getLikesPaged(userId, productNo, account, page, size);
        long total = likeRepository.countLikes(userId, productNo, account);
        return new PagedResult<>(data, page, size, total);
    }

    @Transactional
    public void addLike(LikeRequest request) {
        validateLikeRequest(request);

        likeRepository.addLike(
                request.getUserId(),
                request.getProductNo(),
                request.getPurchaseQuantity(),
                request.getAccount()
        );
    }

    @Transactional
    public void updateLike(Long sn, LikeRequest request) {
        validateSn(sn);
        validateLikeRequest(request);

        boolean exists = likeRepository.existsSnAndUserId(sn, request.getUserId());
        if (!exists) {
            throw BusinessException.notFound("查無此資料");
        }

        likeRepository.updateLike(
                sn,
                request.getUserId(),
                request.getProductNo(),
                request.getPurchaseQuantity(),
                request.getAccount()
        );
    }

    @Transactional
    public void deleteLike(Long sn, String userId) {
        validateSn(sn);
        validateUserId(userId);

        boolean exists = likeRepository.existsSnAndUserId(sn, userId);
        if (!exists) {
            throw BusinessException.notFound("查無此 Like 資料或資料不屬於該使用者");
        }

        likeRepository.deleteLike(sn, userId);
    }

    private void validateLikeRequest(LikeRequest request) {
        if (request == null) {
            throw BusinessException.badRequest( "請求資料不可為空");
        }

        validateUserId(request.getUserId());

        if (request.getProductNo() == null || request.getProductNo() <= 0) {
            throw BusinessException.badRequest("productNo 必須大於 0");
        }

        if (request.getPurchaseQuantity() == null || request.getPurchaseQuantity() <= 0) {
            throw BusinessException.badRequest("purchaseQuantity 必須大於 0");
        }

        if (request.getAccount() == null || request.getAccount().trim().isEmpty()) {
            throw BusinessException.badRequest("account 不可為空");
        }
    }

    private void validateSn(Long sn) {
        if (sn == null || sn <= 0) {
            throw BusinessException.badRequest("sn 必須大於 0");
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw BusinessException.badRequest("userId 不可為空");
        }
    }
}