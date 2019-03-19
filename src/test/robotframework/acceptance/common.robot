*** Settings ***
Documentation   Locale-independent tests.
Resource        ../Lib.robot
Library         Faker   en  ${FAKER_SEED}     # fixed results w/ seed


*** Test Cases ***
Full Number
    ${v}    Faker.Number Digits   ${7}
    Verify String     ${v}

Number of 7 Digits
    ${v}    Number Digits   ${7}
    Verify String     ${v}

Number Between
    ${v}    Number Number Between   ${100000}   ${100099}
    Should Be True	    ${v} >= 100000 and ${v} <= 100099

Lorem Sentence of Several Words
    ${v}    Lorem Sentence   ${7}   ${12}
    Verify String     ${v}

Lorem Several Paragraphs
    ${v}    Lorem Paragraph    ${3}
    Verify String     ${v}

Lorem Several Paragraphs Collection
    ${v}    Lorem Paragraphs    ${3}
    Should Not Be Empty         ${v}
    Length Should Be            ${v}    ${3}
