package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record BorrowReturnItemResponse(
    Long bookId,
    String bookName,
    BigDecimal fine,
    LocalDateTime returnDate
){

}