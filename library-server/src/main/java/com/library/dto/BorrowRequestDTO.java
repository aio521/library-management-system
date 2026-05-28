package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRequestDTO {
    @NotNull(message = "读者ID不能为空")
    private Long readerId;
    @NotBlank(message = "条形码不能为空")
    private String barcode;
}
