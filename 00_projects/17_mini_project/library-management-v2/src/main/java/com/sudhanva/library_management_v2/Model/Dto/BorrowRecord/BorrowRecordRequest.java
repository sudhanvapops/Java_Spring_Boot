package com.sudhanva.library_management_v2.Model.Dto.BorrowRecord;

import java.util.List;

import lombok.Builder;

@Builder
public record BorrowRecordRequest(
    Long memberId,
    List<BorrowRecordBookRequest> books
) { }