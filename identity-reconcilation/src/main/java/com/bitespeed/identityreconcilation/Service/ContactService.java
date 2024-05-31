package com.bitespeed.identityreconcilation.Service;

import com.bitespeed.identityreconcilation.Repository.ContactRepository;
import com.bitespeed.identityreconcilation.dto.ContactResponseDto;
import com.bitespeed.identityreconcilation.dto.WrappedContactResponseDto;
import com.bitespeed.identityreconcilation.enums.LinkPrecedence;
import com.bitespeed.identityreconcilation.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    public Contact identifyOrCreateContact(String email, String phoneNumber){
        Contact existingContact = identifyContact(email, phoneNumber);

        if(existingContact != null)
            return existingContact;

        return createContact(email, phoneNumber);
    }

    public Contact chooseContactByEmailOrPhoneNumber(List<Contact> contacts) {
        // If the list is empty, return null
        if (contacts.isEmpty()) {
            return null;
        }

        for (Contact contact : contacts) {
            // choosing the contact with PRIMARY link precedence
            if (contact.getLinkPrecedence() == LinkPrecedence.PRIMARY) {
                return contact;
            }
        }

        // If no primary contact found, return the first contact in the list
        return contacts.get(0);
    }

    public Contact identifyContact(String email, String phoneNumber){
        if(email!=null && phoneNumber!=null){
            Contact chosenContactByEmail = chooseContactByEmailOrPhoneNumber(contactRepository.findByEmail(email));
            Contact chosenContactByPhone = chooseContactByEmailOrPhoneNumber(contactRepository.findByPhoneNumber(phoneNumber));

            if(chosenContactByEmail != null && chosenContactByPhone != null){
                if (!Objects.equals(chosenContactByEmail.getId(), chosenContactByPhone.getId())) {
                    return mergeContacts(chosenContactByEmail, chosenContactByPhone);
                }
                return chosenContactByEmail;
            }
            else if(chosenContactByEmail != null){
                return createSecondaryContact(chosenContactByEmail, null, phoneNumber);
            }
            else if(chosenContactByPhone != null)
                return createSecondaryContact(chosenContactByPhone, email, null);
        }
        else if(email!=null){
            return chooseContactByEmailOrPhoneNumber(contactRepository.findByEmail(email));
        }
        else if(phoneNumber!=null){
            return chooseContactByEmailOrPhoneNumber(contactRepository.findByPhoneNumber(phoneNumber));
        }
        return null;
    }

    private Contact createContact(String email, String phoneNumber){
        Contact contact = new Contact();
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);
        contact.setLinkedId(null);
        contact.setLinkPrecedence(LinkPrecedence.PRIMARY);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());

        return contactRepository.save(contact);
    }

    private Contact createSecondaryContact(Contact primaryContact, String email, String phoneNumber) {
        Contact secondaryContact = new Contact();
        secondaryContact.setEmail(email != null ? email : primaryContact.getEmail());
        secondaryContact.setPhoneNumber(phoneNumber != null ? phoneNumber : primaryContact.getPhoneNumber());
        secondaryContact.setLinkedId(primaryContact.getLinkedId() != null ? primaryContact.getLinkedId() : primaryContact.getId());
        secondaryContact.setLinkPrecedence(LinkPrecedence.SECONDARY);
        LocalDateTime now = LocalDateTime.now();
        secondaryContact.setCreatedAt(now);
        secondaryContact.setUpdatedAt(now);
        return contactRepository.save(secondaryContact);
    }


    public Contact updateContact(Contact contact){
        contact.setUpdatedAt(LocalDateTime.now());
        return contactRepository.save(contact);
    }

    public Contact mergeContacts(Contact primaryContact, Contact secondaryContact){
        if(primaryContact.getCreatedAt().isAfter(secondaryContact.getCreatedAt())){
            Contact temp = primaryContact;
            primaryContact = secondaryContact;
            secondaryContact  = temp;
        }

        secondaryContact.setLinkedId(primaryContact.getId());
        secondaryContact.setLinkPrecedence(LinkPrecedence.SECONDARY);
        updateContact(secondaryContact);

        List<Contact> secondaryContacts = contactRepository.findByLinkedId(secondaryContact.getId());
        for (Contact contact: secondaryContacts){
            contact.setLinkedId(primaryContact.getId());
            updateContact(contact);
        }
        return primaryContact;
    }

    public WrappedContactResponseDto buildContactResponse(Contact contact) {
        List<Contact> secondaryContacts;
        Set<String> emails = new HashSet<>();
        Set<String> phoneNumbers = new HashSet<>();
        List<Long> secondaryContactIds = new ArrayList<>();

        if (contact.getLinkedId() != null) {
            Optional<Contact> parentContactOpt = contactRepository.findById(contact.getLinkedId());
            if (parentContactOpt.isPresent()) {
                Contact parentContact = parentContactOpt.get();
                emails.add(parentContact.getEmail());
                phoneNumbers.add(parentContact.getPhoneNumber());

                secondaryContacts = contactRepository.findByLinkedId(parentContact.getId());
                for (Contact secondary : secondaryContacts) {
                    if (secondary.getEmail() != null) {
                        emails.add(secondary.getEmail());
                    }
                    if (secondary.getPhoneNumber() != null) {
                        phoneNumbers.add(secondary.getPhoneNumber());
                    }
                    secondaryContactIds.add(secondary.getId());
                }
                ContactResponseDto contactResponseDto =  new ContactResponseDto(parentContact.getId(), new ArrayList<>(emails), new ArrayList<>(phoneNumbers), secondaryContactIds);
                return new WrappedContactResponseDto(contactResponseDto);
            }
        } else {
            emails.add(contact.getEmail());
            phoneNumbers.add(contact.getPhoneNumber());
            secondaryContacts = contactRepository.findByLinkedId(contact.getId());
            for (Contact secondary : secondaryContacts) {
                if (secondary.getEmail() != null) {
                    emails.add(secondary.getEmail());
                }
                if (secondary.getPhoneNumber() != null) {
                    phoneNumbers.add(secondary.getPhoneNumber());
                }
                secondaryContactIds.add(secondary.getId());
            }
            ContactResponseDto contactResponseDto = new ContactResponseDto(contact.getId(), new ArrayList<>(emails), new ArrayList<>(phoneNumbers), secondaryContactIds);
            return new WrappedContactResponseDto(contactResponseDto);
        }
        return null;
    }

}