@ignore
Feature: Create client Business
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl


  @ignore
  @createClientBusinessDetailsStep
  Scenario: Create client Business Details Step
    Given configure ssl = true
    * def clientBusinessDetailsData = read('classpath:templates/clientBusinessDetails.json')
    Given path 'clients',clientId,'businessDetail'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientBusinessDetailsData.createClientBusinessDetails
    When method POST
    Then status 200
    Then match $ contains { resourceIdentifier: '#notnull' }
    Then match $ contains { clientId: '#notnull' }
    Then def clientId = response.clientId
    Then def businessId = response.resourceIdentifier


  @ignore
  @getClientBusinessDetailsStep
  Scenario: Get client Business Details Step
    Given configure ssl = true
    Given path 'clients',clientId,'businessDetail',businessDetailId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def detail = response

  @ignore
  @getTemplatesClientDetails
  Scenario: Get client Business Details Step
    Given configure ssl = true
    Given path 'clients',clientId,'businessDetail','template'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def detailResponse = response


  @ignore
  @deleteClientBusinessDetailsStep
  Scenario: Get client Business Details Step
    Given configure ssl = true
    Given path 'clients',clientId,'businessDetail',businessDetailId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method DELETE
    Then status 200
    Then def detail = response

  @ignore
  @updateClientBusinessDetailsStep
  Scenario: Update client Business Details Step
    Given configure ssl = true
    * def clientBusinessDetailsData = read('classpath:templates/clientBusinessDetails.json')
    Given path 'clients',clientId,'businessDetail',businessDetailId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientBusinessDetailsData.updateClientBusinessDetails
    When method PUT
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then match $ contains { clientId: '#notnull' }
    Then match $ contains { changes: '#notnull' }
    Then def clientId = response.clientId
    Then def businessId = response.resourceId
