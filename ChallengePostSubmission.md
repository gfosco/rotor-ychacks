Rotor
Give any device a URL

Existing Backend-as-a-service offerings are great, but we often find ourselves needing "one more thing" that isn't offered.  So we stand up a server, and we're back in the same situation, having to manage infrastructure.  We wanted to build a service that is flexible, that can be used in conjunction with other services, and offer some completely new features.

Rotor provides its clients with a URL, where they can accept and respond to HTTP events.  This enables client applications to integrate in real-time with any existing service that supports Webhooks.

To show this, we've created three sample applications:

RotoryPhone, an Android service which can screen calls from Twilio.  When a call is received, Twilio sends an HTTP POST to a Rotor URL, and the phone prompts to user the accept or decline the call.  If the user accepts the call, the phone responds with TwiML to forward the call.  If the user declines the call, the phone responds with TwiML to speak the decline reason or send a busy signal to the caller.  The app could easily be extended to respond with any set of TwiML commands based on user input or device state.

AuthRotor, a two-factor login service with just one tap.  When a login request is made on the example website, an AJAX request hits a Rotor URL, and the phone prompts the user to accept or decline the login request.  The responds with the second factor to approve authentication.

SurveyRotor, a web-based survey submission and display tool.  As users answer the survey, the results are received in real-time on a display device.  This app adds real-time functionality to a Parse App.
