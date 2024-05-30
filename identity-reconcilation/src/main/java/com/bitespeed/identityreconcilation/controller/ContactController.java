package com.bitespeed.identityreconcilation.controller;

import com.bitespeed.identityreconcilation.Service.ContactService;
import com.bitespeed.identityreconcilation.dto.ContactRequestDto;
import com.bitespeed.identityreconcilation.dto.ContactResponseDto;
import com.bitespeed.identityreconcilation.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/identify")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponseDto> identifyContact(@RequestBody ContactRequestDto contactRequest){
        String email = contactRequest.getEmail();
        String phoneNumber = contactRequest.getPhoneNumber();

        Contact contact = contactService.identifyOrCreateContact(email, phoneNumber);
        ContactResponseDto contactResponse = contactService.buildContactResponse(contact);

        return ResponseEntity.ok(contactResponse);
    }

}
