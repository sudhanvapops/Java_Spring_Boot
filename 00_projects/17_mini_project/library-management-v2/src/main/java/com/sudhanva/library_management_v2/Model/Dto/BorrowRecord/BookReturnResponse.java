package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;




@Builder
public record BookReturnResponse(
    Long memberId,
    String memberName,
    BigDecimal totalFine,
    List<BorrowReturnItemResponse> books
) {
    
}
