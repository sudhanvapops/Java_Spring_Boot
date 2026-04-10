package com.sudhanva.library.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "borrowers")
public class Borrower {

    // Columns

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = true)
    private String email;

    @Column(unique = true, updatable = false, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(nullable = false)
    private LocalDateTime membershipDate;

    // Relations

    @OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
    private Set<BorrowRecord> borrowRecords = new HashSet<>();

    // Helper Functions

    public void addBorrowRecord(BorrowRecord record) {
        if (borrowRecords.contains(record))
            return;

        borrowRecords.add(record);
        record.setBorrower(this);
    }

    public void removeBorrowRecord(BorrowRecord record) {
        throw new UnsupportedOperationException(
                "BorrowRecord cannot be removed from Borrower without deleting the record");
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    // Cause Hibernate Manages It
    // public void setId(Long borrowerId) {
    // this.id = borrowerId;
    // }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        if (this.cardNumber != null) {
            throw new IllegalStateException("Card number cannot be changed");
        }
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public LocalDateTime getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(LocalDateTime membershipDate) {
        if (membershipDate == null) {
            throw new IllegalArgumentException("Membership date cannot be null");
        }
        this.membershipDate = membershipDate;
    }

    public Set<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    @Override
    public String toString() {
        return "Borrower [borrowerId=" + id + ", email=" + email + ", cardNumber=" + cardNumber + ", name="
                + name + ", phoneNo=" + phoneNo + ", membershipDate=" + membershipDate + "]";
    }

    // Constructors

    public Borrower() {
    }

    public Borrower(String email, String cardNumber, String name, String phoneNo, LocalDateTime membershipDate) {
        setCardNumber(cardNumber);
        setName(name);
        setEmail(email);
        setPhoneNo(phoneNo);
        setMembershipDate(membershipDate);
    }

}
