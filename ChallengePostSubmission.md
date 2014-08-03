Rotor
Give any device a URL

Existing Backend-as-a-service offerings are great, but I often find myself needing "one more thing" that isn't offered.  So I stand up a server, and I'm back in the same situation, having to manage infrastructure.  I wanted to build a service that is flexible, that can be used in conjunction with other services, and offer some completely new features.

Rotor provides its clients with a URL, where they can accept and respond to HTTP events.  This enables client applications to integrate with any existing service that supports Webhooks.

To show this, we've created two sample applications:

RotoryPhone, an Android service which can screen calls from Twilio.  When a call is received, Twilio sends an HTTP POST to a Rotor URL, and the phone prompts to user the accept or decline the call.  If the user accepts the call, the phone responds with TwiML to forward the call.  If the user declines the call, the phone responds with TwiML to speak the decline reason or send a busy signal to the caller.

AuthRotor, a two-factor login service with just one tap.  When a login request is made on the example website, an AJAX request hits a Rotor URL, and the phone prompts the user to accept or decline the login request.