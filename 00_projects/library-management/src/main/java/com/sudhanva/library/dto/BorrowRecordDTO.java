package com.sudhanva.library.dto;

import java.time.LocalDateTime;

import com.sudhanva.library.entity.BorrowRecord;

public class BorrowRecordDTO {

    public Long id;
    public String borrowerCardNumber;
    public String bookIsbn;
    public LocalDateTime borrowDate;
    public LocalDateTime dueDate;
    public LocalDateTime returnDate;
    public boolean active;

    public static BorrowRecordDTO from(BorrowRecord record) {
        BorrowRecordDTO dto = new BorrowRecordDTO();
        dto.id = record.getId();
        dto.borrowerCardNumber = record.getBorrower().getCardNumber();
        dto.bookIsbn = record.getBook().getIsbn();
        dto.borrowDate = record.getBorrowDate();
        dto.dueDate = record.getDueDate();
        dto.returnDate = record.getReturnDate();
        dto.active = record.isActive();
        return dto;
    }
}
