*** Settings ***
Library         String

*** Variables ***
${FAKER_SEED}   5873663672292697529



*** Keywords ***
Perform Localized Checks
    Check Weather
    Check Credit Card Number
    Check Chuck Norris Fact
    Check Address City
    Check Address Street
    Check Address State
    Check Internet Domain Suffix
    Check Name Name
    Check Company Name
    Check Commerce Color
    Check Commerce Department
    Check Artist Name
    Check Yoda Quote


Setup Localized Suite
    ${FAKER_LOCALE}   Remove String Using Regexp  ${SUITE NAME}   .*\\.
    Import Library      Faker   ${FAKER_LOCALE}  ${FAKER_SEED}  # fixed results w/ seed


Verify String
    [Arguments]    ${v}
    Should Be String	    ${v}
    Should Not Be Empty     ${v}



Check Weather
    ${v}    Weather Description
    Verify String           ${v}

Check Credit Card Number
    ${v}    Business Credit Card Number
    Verify String           ${v}
    Should Match Regexp     ${v}    \\d{4}-\\d{4}-\\d{4}-\\d{4}

Check Chuck Norris Fact
    ${v}    Chuck Norris Fact
    Verify String           ${v}

Check Address City
    ${v}    Address City
    Verify String           ${v}

Check Address Street
    ${v}    Address Street Name
    Verify String           ${v}

Check Address State
    ${v}    Address State
    Verify String           ${v}

Check Internet Domain Suffix
    ${v}    Internet Domain Suffix
    Verify String           ${v}

Check Name Name
    ${v}    Name Name
    Verify String           ${v}

Check Company Name
    ${v}    Company Name
    Verify String           ${v}

Check Commerce Color
    ${v}    Commerce Color
    Verify String           ${v}

Check Commerce Department
    ${v}    Commerce Department
    Verify String           ${v}

Check Artist Name
    ${v}    Artist Name
    Verify String           ${v}

Check Yoda Quote
    ${v}    Yoda Quote
    Verify String           ${v}
