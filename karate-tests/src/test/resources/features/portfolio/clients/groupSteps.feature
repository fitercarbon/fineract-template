@ignore
Feature: Group creations steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def groupdata = read('classpath:templates/group.json')



  @ignore
  @createGroupStep
  Scenario: Create Group Step
    Given configure ssl = true
    Given path 'groups'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request groupdata.groupNewPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def groupId = response.resourceId
