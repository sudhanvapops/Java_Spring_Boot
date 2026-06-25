package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record BorrowTransactionRequest(
    @NotNull(message = "Member id is required")
    Long memberId,

    @NotEmpty(message = "At least one book must be selected")
    List<BorrowTransactionItemRequest> books
) { }