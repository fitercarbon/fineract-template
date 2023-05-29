@ignore
Feature: Client creations steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def clientdata = read('classpath:templates/client.json')


  #set parameter clientCreationDate
  @ignore
  @create
  Scenario: Create client test
    Given configure ssl = true
    Given path 'clients'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.createClientPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def clientId = response.resourceId

  # set parameter clientCreationDate
  @ignore
  @findbyid
  Scenario: Get client by id test
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(clientCreationDate)' }
    * def clientId = result.clientId
    Given path 'clients',clientId
    Given configure ssl = true
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def client = response

  @ignore
  @list
  Scenario: Get all clients
    Given configure ssl = true
    Given path 'clients'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200

  # set parameter clientId
  @ignore
  @findbyclientid
  Scenario: Get client by id test
    Given path 'clients',clientId
    Given configure ssl = true
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def client = response

  # set parameter clientId
  @ignore
  @activateClientStep
  Scenario: Activate client test
    Given configure ssl = true
    Given path 'clients' ,clientId
    And params {command:'activate'}
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.activateClientPayload
    When method POST
    Then status 200
    Then def res = response

  # set parameter clientId
  @ignore
  @update
  Scenario: Update client test
    Given configure ssl = true
    Given path 'clients' ,clientId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.updateClientPayload
    When method PUT
    Then status 200
    Then def res = response

  #set parameter clientCreationDate, constitutionId
  @ignore
  @createEntityStep
  Scenario: Create Entity client test
    Given configure ssl = true
    Given path 'clients'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.createEntityClient
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def clientId = response.resourceId

  # set parameter clientId, accountNo
  @ignore
  @updateEntityStep
  Scenario: Update Entity client test
    Given configure ssl = true
    Given path 'clients' ,clientId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.updateEntityClientPayload
    When method PUT
    Then status 200
    Then def res = response

  #set parameter savingsProductId
  @ignore
  @createClientWithSavingsStep
  Scenario: Create client with saving account test
    Given configure ssl = true
    Given path 'clients'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.createClientWithSavingsPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull', savingsId : '#notnull' }
    Then def client = response

  #set parameter clientCreationDate, dailyWithdrawLimit, singleWithdrawLimit
  @ignore
  @createClientWithWithdrawalLimitsStep
  Scenario: Create client with Withdrawal limits
    Given configure ssl = true
    Given path 'clients'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.createClientWithdrawalLimitsPayload
    When method POST
    Then status 400
    Then match $ contains { developerMessage: '#notnull' }}


