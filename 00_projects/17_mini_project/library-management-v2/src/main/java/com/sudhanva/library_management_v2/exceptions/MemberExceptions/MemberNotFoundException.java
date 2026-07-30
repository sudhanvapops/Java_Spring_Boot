package com.sudhanva.library_management_v2.exceptions.MemberExceptions;


public class MemberNotFoundException extends RuntimeException{

    public MemberNotFoundException(Long memberId){
        super("Member not found with id: "+memberId);
    }
}
