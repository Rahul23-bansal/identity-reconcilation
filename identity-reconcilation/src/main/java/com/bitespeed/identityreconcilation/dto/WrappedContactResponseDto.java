package com.bitespeed.identityreconcilation.dto;

public class WrappedContactResponseDto {
    private ContactResponseDto contact;

    public WrappedContactResponseDto(ContactResponseDto contact) {
        this.contact = contact;
    }

    public ContactResponseDto getContact() {
        return contact;
    }

    public void setContact(ContactResponseDto contact) {
        this.contact = contact;
    }
}
