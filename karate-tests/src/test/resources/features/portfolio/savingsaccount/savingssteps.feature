@ignore
Feature: Savings Creation Steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl


  # set parameter submittedOnDate, clientCreationDate
  @ignore
  @create
  Scenario: Create savings accounts
    #create savings product step
    * def savingsProduct = call read('classpath:features/portfolio/products/savingsproduct.feature@fetchdefaultproduct')
    #create client step with clientCreationDate
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(clientCreationDate)' }
    Given configure ssl = true
    #now create savings here
    Given path 'savingsaccounts'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def clientId = result.clientId
    * def savingsProductId = savingsProduct.savingsProductId
    * def savingsData = read('classpath:templates/savings.json')
    And request savingsData.savingsAccount
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def savingsId = response.resourceId

  @ignore
  @approve
  Scenario: Approve savings account
    Given configure ssl = true
    * def savingsData = read('classpath:templates/savings.json')
    Given path 'savingsaccounts',savingsId
    And params {command:'approve'}
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request savingsData.approve
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#(savingsId)' }



  @ignore
  @activate
  Scenario: Activate savings account
    Given configure ssl = true
    * def savingsData = read('classpath:templates/savings.json')
    Given path 'savingsaccounts',savingsId
    And params {command:'activate'}
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request savingsData.activate
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#(savingsId)' }
    Then def activeSavingsId = response.resourceId


  #add parameters savingsId, command(withdraw/deposit), transactionDate and transactionAmount
  @ignore
  @transaction
  Scenario: Savings transaction
    Given configure ssl = true
    * def transactionDate = transactionDate
    * def transactionAmount = transactionAmount
    * def command = command
    * def savingsData = read('classpath:templates/savings.json')
    Given path 'savingsaccounts',savingsId,'transactions'
    And params { command : '#(command)' }
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request savingsData.transaction
    When method POST
    Then status 200
    Then match $ contains { savingsId: '#(savingsId)' }
    Then def activeSavingsId = response.savingsId

      #add parameters savingsId, command(withdraw/deposit), transactionDate and transactionAmount
  @ignore
  @transactionWithBadRequest
  Scenario: Savings transaction With Bad Request Exception
    Given configure ssl = true
    * def transactionDate = transactionDate
    * def transactionAmount = transactionAmount
    * def command = command
    * def savingsData = read('classpath:templates/savings.json')
    Given path 'savingsaccounts',savingsId,'transactions'
    And params { command : '#(command)' }
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request savingsData.transaction
    When method POST
    Then status 403
    Then match $ contains { developerMessage: '#notnull' }

  @ignore
  @findsavingsbyid
  Scenario: Get savings account by id
    Given configure ssl = true
    Given path 'savingsaccounts',savingsId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    * def savingsAccount = response

  @ignore
  @createSavingsAccountStep
  Scenario: Create savings accounts Step
    #create savings product step
    * def savingsProduct = call read('classpath:features/portfolio/products/savingsproduct.feature@fetchdefaultproduct')
    Given configure ssl = true
    #now create savings here
    Given path 'savingsaccounts'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def clientId = result.clientId
    * def savingsProductId = savingsProduct.savingsProductId
    * def savingsData = read('classpath:templates/savings.json')
    And request savingsData.savingsAccount
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def savingsId = response.resourceId