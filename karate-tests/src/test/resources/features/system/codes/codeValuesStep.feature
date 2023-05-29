Feature: Code values creation api tests
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * configure ssl = true
    * def system = read('classpath:templates/system.json')

  @ignore
  @fetchCodeValuesStep
  Scenario: Fetch Code Values
    Given path 'codes', codeId ,'codevalues'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def listOfCodeValues = response

  @ignore
  @createCodeValueStep
  Scenario: Create Code Value Step
    Given configure ssl = true
    Given path 'codes' ,codeId ,'codevalues'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request system.createCodeValuePayload
    When method POST
    Then status 200
    Then match $ contains {resourceId : '#notnull', subResourceId : '#notnull'}
    Then def codeValueId = response.subResourceId