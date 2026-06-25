package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import lombok.Builder;

@Builder
public record BorrowRecordBookRequest(
    Long bookId
) {
    
}
