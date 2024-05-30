package com.bitespeed.identityreconcilation.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContactResponseDto {
    private long primaryContactId;
    private List<String> emails;
    private List<String> phoneNumbers;
    private List<Long> secondaryContactIds;

    public ContactResponseDto(Long primaryContactId, List<String> emails, List<String> phoneNumbers, List<Long> secondaryContactIds) {
        this.primaryContactId = primaryContactId;
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
        this.secondaryContactIds = secondaryContactIds;
    }

}
