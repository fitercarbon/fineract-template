@ignore
Feature: Client creations steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def clientOtherInfo = read('classpath:templates/clientOtherInfo.json')


  @ignore
  @create
  Scenario: Create client other info test
    Given configure ssl = true
    Given path 'clients', clientId, 'otherInfo'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientOtherInfo.createClientOtherInfoPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def otherInfoId = response.resourceId


  # set parameter clientId, otherInfoId
  @ignore
  @update
  Scenario: Update client other info test
    Given configure ssl = true
    Given path 'clients' ,clientId, 'otherInfo' ,otherInfoId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientOtherInfo.updateClientOtherInfoPayload
    When method PUT
    Then status 200
    Then def res = response


