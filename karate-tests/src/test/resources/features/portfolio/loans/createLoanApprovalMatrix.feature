Feature: Test loan account apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl


  @testThatICanCreateLoanApprovalMatrix
  Scenario: Test That I Can Create Loan Approval Matrix
           #- Enable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    *  def configName = 'Add-More-Stages-To-A-Loan-Life-Cycle'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(configName)' }
    *  def configurationId = response.globalConfig.id
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_global_config') { configurationsId : '#(configurationId)' }

    * def currency = 'UGX'
      #Add new Approval Matrix based on currency
    * def result = call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@createLoanApprovalMatrixStep') { currency : '#(currency)'}
    * def matrixId = result.matrixId

     # Simulate  a duplicate Currency and it should fail
    * call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@createLoanApprovalMatrixAndShouldFailDueToDuplicateCurrencyStep') { currency : '#(currency)'}

    # Update Approval Matrix based on currency
    * def update = call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@UpdateLoanApprovalMatrixStep') { currency : '#(currency)', matrixId : '#(matrixId)'}
    * def matrixId = result.matrixId

        #Get Approval Matrix By ID
    * call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@getLoanApprovalMatrixStep') { matrixId : '#(matrixId)'}

        #Get All Approval Matrix By ID
    * def allApprovalMatrices = call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@getAllLoanApprovalMatrixStep') {}
    * assert karate.sizeOf(allApprovalMatrices.response) == 1

     # Delete Loan Approval Matrix created above. We Create a single unique record by currency
    * call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@deleteLoanApprovalMatrixStep') { matrixId : '#(matrixId)'}


    #- Disable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    Then print 'Configuration ID ==> ', configurationId
    * def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(configurationId)' }
    Then print 'Configuration Response ==> ', configResponse
