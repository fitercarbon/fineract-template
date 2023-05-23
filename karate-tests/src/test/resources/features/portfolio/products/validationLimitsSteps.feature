@ignore
Feature: Validation limit Api's
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @ignore
  @createValidationLimit
  Scenario: Create Validation Limit
    Given configure ssl = true
    * def validationLimit = read('classpath:templates/validationLimit.json')
    Given path 'validationlimit'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request validationLimit.createPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def code = response

  @ignore
  @list
  Scenario: Get all validation limits
    Given configure ssl = true
    Given path 'validationlimit'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def validationLimits = response

  #set parameter validationLimitId
  @ignore
  @fetchById
  Scenario: Get validation limit
    Given configure ssl = true
    Given path 'validationlimit',validationLimitId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def validationLimit = response




