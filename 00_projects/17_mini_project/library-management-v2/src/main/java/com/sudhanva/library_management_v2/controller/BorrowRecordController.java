package com.sudhanva.library_management_v2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;



@RestController
@RequestMapping("/api/borrowrecord/")
public class BorrowRecordController {
    
    final private BorrowRecordRepo bRRepo;

    public BorrowRecordController(BorrowRecordRepo bRRepo){
        this.bRRepo = bRRepo;
    }

}
