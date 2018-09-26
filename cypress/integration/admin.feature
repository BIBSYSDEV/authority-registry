# encoding: utf-8
# lang: en

# API admin, superuser responsibility for user administration, creating/removing entity registries
# Registry admin, administer data in registry
# Anonymous user, download data from registry

Feature: Admin user features

  Scenario: An API admin user authenticates themselves
    Given that there is an API admin user with valid credentials
    When they provide these credentials
    Then they are authenticated and receive a valid authentication token

  Scenario: An API admin user is authorised
    Given that an API admin user has a valid authentication token
    When they submit the authentication token
    Then they are authorised to access the administration APIs
