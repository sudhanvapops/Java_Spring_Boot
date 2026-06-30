package com.sudhanva.library.dto;

import java.time.LocalDateTime;

import com.sudhanva.library.entity.Borrower;

public class BorrowerDTO {

    public Long id;
    public String name;
    public String email;
    public String cardNumber;
    public String phoneNo;
    public LocalDateTime membershipDate;

    public static BorrowerDTO from(Borrower borrower) {
        BorrowerDTO dto = new BorrowerDTO();
        dto.id = borrower.getId();
        dto.name = borrower.getName();
        dto.email = borrower.getEmail();
        dto.cardNumber = borrower.getCardNumber();
        dto.phoneNo = borrower.getPhoneNo();
        dto.membershipDate = borrower.getMembershipDate();
        return dto;
    }
}
