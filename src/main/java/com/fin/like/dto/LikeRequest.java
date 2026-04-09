package com.fin.like.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class LikeRequest {

    @NotBlank(message = "userId 不可為空")
    @Size(max = 20, message = "userId 長度不可超過 20")
    private String userId;

    @NotNull(message = "productNo 不可為空")
    private Long productNo;

    @NotNull(message = "purchaseQuantity 不可為空")
    @Positive(message = "purchaseQuantity 必須大於 0")
    private Integer purchaseQuantity;

    @NotBlank(message = "account 不可為空")
    @Size(max = 20, message = "account 長度不可超過 20")
    private String account;
    // 分get set 好可以防止外部直接亂改欄位
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getProductNo() { return productNo; }
    public void setProductNo(Long productNo) { this.productNo = productNo; }

    public Integer getPurchaseQuantity() { return purchaseQuantity; }
    public void setPurchaseQuantity(Integer purchaseQuantity) { this.purchaseQuantity = purchaseQuantity; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
}
