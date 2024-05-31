# Identity Reconciliation Service

## Overview
This service provides functionality to reconcile user identities based on email and phone number information. It identifies or creates user contacts, merges duplicate entries, and returns structured contact information.

## Exposed Endpoint

### `/identify`
This endpoint allows you to identify or create a contact based on the provided email and phone number. If the contact already exists, it merges duplicate entries and returns the consolidated contact information.

#### Method
`POST`

#### Request
The request should be a JSON object containing the `email` and `phoneNumber` fields.
