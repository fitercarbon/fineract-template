
Feature: Manage Client Household Expenses Lifecycle
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

    * def householdExpenses = read('classpath:templates/householdexpenses.json')
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(clientCreationDate)' }
    * def clientId = result.response.resourceId
    * def otherExpensesTypeId = 24

      #- Fetch codeValue for otherExpenses
    * def otherExpensesName = 'OtherExpenses'
    * def otherExpensesCode = call read('classpath:features/system/codes/codesStep.feature@fetchCodeByNameStep') { codeName : '#(otherExpensesName)' }
    * def otherExpensesCodeId = otherExpensesCode.codeName.id
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(otherExpensesCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : otherExpensesCodeId, name : 'Test1'});
    * def otherExpensesId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print otherExpensesId

  @updatehouseholdexpenses
  Scenario: Update Client Household Expenses
    * def result = call read('classpath:features/portfolio/clients/householdexpenses.feature@householdexpensessteps') { clientId : '#(clientId)',otherExpensesId : '#(otherExpensesId)' }
    * def householdExpenseId = result.response.resourceId
    Given configure ssl = true
    Given path 'clients',clientId,'householdExpenses',householdExpenseId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request householdExpenses.updatePayload
    When method PUT
    Then status 200
    Then match $ contains { resourceId: '#notnull'}
    Then def client = response


  @deletehouseholdexpenses
  Scenario: Update Client Household Expenses
    * def result = call read('classpath:features/portfolio/clients/householdexpenses.feature@householdexpensessteps') { clientId : '#(clientId)',otherExpensesId : '#(otherExpensesId)' }
    * def householdExpenseId = result.response.resourceId
    Given configure ssl = true
    Given path 'clients',clientId,'householdExpenses',householdExpenseId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method DELETE
    Then status 200


  @findhouseholdexpenses
  Scenario: Find Client Household Expenses
    Given configure ssl = true
    Given path 'clients',clientId,'householdExpenses'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When call read('classpath:features/portfolio/clients/householdexpenses.feature@householdexpensessteps'){ clientId : '#(clientId)',otherExpensesId : '#(otherExpensesId)' }
    When method GET
    Then status 200
    And response.length >0
    And match each response[*].otherExpensesList.length> 0
    And match each response[*].foodExpensesAmount> 0

