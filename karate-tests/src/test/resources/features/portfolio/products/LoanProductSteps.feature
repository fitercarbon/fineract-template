@ignore
Feature: Create Loan Product and It's charges Steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @ignore
  @createLoanProductWithOverdueChargeSteps
  Scenario: Create loan product With Overdue Charge
    Given configure ssl = true
    * def productsData = read('classpath:templates/loanProduct.json')
    Given path 'loanproducts'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request productsData.loanProductWithOverDueChargePayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def loanProductId = response.resourceId



  @ignore
  @createFlatOverdueChargeWithOutFrequencySteps
  Scenario: Create Flat Overdue Charge WithOut Frequency
    Given configure ssl = true
    * def chargesData = read('classpath:templates/loansCharges.json')
    Given path 'charges'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request chargesData.penaltyFlatChargeWithNoFrequencyPayLoad
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def chargeId = response.resourceId


  @ignore
  @createDisburseToSavingsAccountLoanChargeFeesOnApprovedAmountSteps
  Scenario: Create Disburse to savings account loan charge fees on approved amount
    Given configure ssl = true
    * def chargesData = read('classpath:templates/loansCharges.json')
    Given path 'charges'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request chargesData.disburseToSavingsLoanAccountChargeOnPercentageApprovedAmountPayLoad
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def chargeId = response.resourceId

  @ignore
  @createLoanProductWithOutChargesSteps
  Scenario: Create loan product With Overdue Charge
    Given configure ssl = true
    * def productsData = read('classpath:templates/loanProduct.json')
    Given path 'loanproducts'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request productsData.loanProductWithOutChargesPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def loanProductId = response.resourceId


  @ignore
  @OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps
  Scenario: OXY-163 Loan Schedule with interest recalculation enabled is only 3 periods long regardless of the number of repayments set
    Given configure ssl = true
    * def productsData = read('classpath:templates/loanProduct.json')
    Given path 'loanproducts'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request productsData.OXY163loanproductPayLoad
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def loanProductId = response.resourceId



