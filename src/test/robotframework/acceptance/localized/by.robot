*** Settings ***
Documentation   Localized tests
# "localized" test suites should be symlinks to (default) en locale, but git for windows has some limitations
Resource        ../../Lib.robot
Suite Setup  Setup Localized Suite



*** Test Cases ***
Localized Checks
    Perform Localized Checks
