Feature: Codes api tests
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * configure ssl = true

  #set parameter code id
  @ignore
  @fetchCodeByIdStep
  Scenario: Fetch Code
    Given path 'codes', codeId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def listOfCodes = response