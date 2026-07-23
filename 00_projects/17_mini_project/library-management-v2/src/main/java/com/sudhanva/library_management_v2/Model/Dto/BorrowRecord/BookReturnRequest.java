package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;



@Builder
public record BookReturnRequest(
    Long memberId,
    @NotEmpty
    List<BorrowReturnItemRequest> books
) {}
