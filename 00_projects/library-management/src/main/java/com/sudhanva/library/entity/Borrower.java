package com.sudhanva.library.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Borrower {

    // Relations

    @OneToMany(mappedBy = "borrower")
    private Set<BorrowRecord> borrowRecords = new HashSet<>();

    // Helper Functions

    // ? DOnt know Why its there study More
    public void addBorrowRecord(BorrowRecord record) {
        if (borrowRecords.contains(record))
            return;

        borrowRecords.add(record);
        record.setBorrower(this);
    }

    public void removeBorrowRecord(BorrowRecord record) {
        if (!borrowRecords.contains(record))
            return;

        borrowRecords.remove(record);
        record.setBorrower(null);
    }
}
