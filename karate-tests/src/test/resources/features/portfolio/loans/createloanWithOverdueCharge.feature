Feature: Test loan account apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseByCash
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges and disburse it by Cash
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburse') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
        # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

      # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges',totalCharges)
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue


    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 12
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 1

  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseBySavingsAccount
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges And Disburse it on a savings Account
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId



        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId



    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)' }
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
         # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
       #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
     # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges',totalCharges)
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue


         # Loop through the Repayment Schedule Object  --> Double value is failing to be casted as integer - WIP
    * def assertRepaymentSchedulePenaltyChargeAmount =
      """
         function(repaymentSchedules, expectedTotalAmountToBeCharged) {
          for (var i = 0; i < repaymentSchedules.length; i++) {
            var penaltyChargesDue = repaymentSchedules[i].penaltyChargesDue;

            if (typeof penaltyChargesDue === 'number' && !isNaN(penaltyChargesDue)) {
              expectedTotalAmountToBeCharged += penaltyChargesDue;
            }
          }
          return expectedTotalAmountToBeCharged;
        }
      """
    * karate.log('*******************  Penalty Charges ******************',loanResponse.loanAccount.summary.penaltyChargesOverdue)
    * def expectedTotalAmountToBeCharged = 0;
    * def totalChargesApplied = assertRepaymentSchedulePenaltyChargeAmount(loanResponse.loanAccount.repaymentSchedule.periods,expectedTotalAmountToBeCharged);
    * karate.log('Total Charges Applied on Repayment Schedule',totalChargesApplied)
    * assert totalChargesApplied == loanResponse.loanAccount.summary.penaltyChargesOverdue



        #Get Savings Account details and check if money hads been deposited
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert loanAmount == savingsResponse.savingsAccount.summary.availableBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 12
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 1

    # Make Transactions Prepay Loan Account
    * def repaymentAmount = loanResponse.loanAccount.summary.totalOverdue
    * def repaymentDate = df.format(faker.date().past(2, 1, TimeUnit.DAYS))
    * def loanTransaction = call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(repaymentAmount)', repaymentDate : '#(repaymentDate)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    * assert loanResponse.loanAccount.status.value == 'Closed (obligations met)'
    * assert loanResponse.loanAccount.status.id == 600
    * assert loanResponse.loanAccount.status.active == false
    * assert loanResponse.loanAccount.status.closedObligationsMet == true
    * assert loanResponse.loanAccount.status.closed == true
    * assert loanResponse.loanAccount.status.overpaid == false
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 2

  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseBySavingsAccountWithDisburseToSavingsCharge
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges And Disburse it on a savings Account With Disburse to Savings Charge
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

       # Create Disburse to Savings Account charge
    * def disburseSavingsCharge = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createDisburseToSavingsAccountLoanChargeFeesOnApprovedAmountSteps') { chargeAmount : '#(chargeAmount)' }
    * def disburseSavingsAccountChargeId = disburseSavingsCharge.chargeId
    * def disburseToSavingsChargeAmount = 10

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanAccountWithDisburseToSavingsAccountChargeAndPenaltyChargeSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)', disburseSavingsAccountChargeId : '#(disburseSavingsAccountChargeId)', disburseToSavingsChargeAmount : '#(disburseToSavingsChargeAmount)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
        # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
     # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges and Fees',totalCharges)
     # Expectation Disbursement to Savings Account and Penalty/Overdue Charge
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue + loanResponse.loanAccount.summary.feeChargesOverdue

        #Get Savings Account details and check if money hads been deposited
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert loanAmount == savingsResponse.savingsAccount.summary.availableBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert loanResponse.loanAccount.summary.feeChargesOverdue == 850
    * assert loanResponse.loanAccount.summary.feeChargesOutstanding == 850
    * assert loanResponse.loanAccount.summary.feeChargesCharged == 850
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 13
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    # Expecting Two Transactions Disbursement Transaction and Disbursement charge
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 2

    # Make Transactions Prepay Loan Account
    * def repaymentAmount = loanResponse.loanAccount.summary.totalOverdue
    * def repaymentDate = df.format(faker.date().past(2, 1, TimeUnit.DAYS))
    * def loanTransaction = call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(repaymentAmount)', repaymentDate : '#(repaymentDate)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    * assert loanResponse.loanAccount.status.value == 'Closed (obligations met)'
    * assert loanResponse.loanAccount.status.id == 600
    * assert loanResponse.loanAccount.status.active == false
    * assert loanResponse.loanAccount.status.closedObligationsMet == true
    * assert loanResponse.loanAccount.status.closed == true
    * assert loanResponse.loanAccount.status.overpaid == false
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 3

  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseBySavingsAccountWithDisburseToSavingsChargeAndWaiveCharges
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges And Disburse it on a savings Account With Disburse to Savings Charge And Waive All Charges
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

       # Create Disburse to Savings Account charge
    * def disburseSavingsCharge = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createDisburseToSavingsAccountLoanChargeFeesOnApprovedAmountSteps') { chargeAmount : '#(chargeAmount)' }
    * def disburseSavingsAccountChargeId = disburseSavingsCharge.chargeId
    * def disburseToSavingsChargeAmount = 10

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanAccountWithDisburseToSavingsAccountChargeAndPenaltyChargeSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)', disburseSavingsAccountChargeId : '#(disburseSavingsAccountChargeId)', disburseToSavingsChargeAmount : '#(disburseToSavingsChargeAmount)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
      # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
     # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges and Fees',totalCharges)
     # Expectation Disbursement to Savings Account and Penalty/Overdue Charge
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue + loanResponse.loanAccount.summary.feeChargesOverdue

        #Get Savings Account details and check if money hads been deposited
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert loanAmount == savingsResponse.savingsAccount.summary.availableBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert loanResponse.loanAccount.summary.feeChargesOverdue == 850
    * assert loanResponse.loanAccount.summary.feeChargesOutstanding == 850
    * assert loanResponse.loanAccount.summary.feeChargesCharged == 850
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 13
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    # Expecting Two Transactions Disbursement Transaction and Disbursement charge
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 2


    # Waive all 12 Penalty/Overdue Charge and 1 Disburse to Savings fee/ charge
     #disbursal to savings charges worth 850
    * def chargeId_1 = loanResponse.loanAccount.charges[0].id
    * def WaiveChargeResponse = call read('classpath:features/portfolio/loans/loansteps.feature@waiveLoanAccountChargesAndFeesSteps') { loanId : '#(loanId)', chargeId : '#(chargeId_1)'}
         #fetch loan details here
    * def responseAfterDisburseToSavingsIsWaived = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    # Assert that disburse to savings charge is waived
    * assert responseAfterDisburseToSavingsIsWaived.loanAccount.summary.feeChargesOverdue == 0
    * assert responseAfterDisburseToSavingsIsWaived.loanAccount.summary.feeChargesOutstanding == 0
    * assert responseAfterDisburseToSavingsIsWaived.loanAccount.summary.feeChargesWrittenOff == 0
    * assert responseAfterDisburseToSavingsIsWaived.loanAccount.summary.feeChargesCharged == 850
    * assert responseAfterDisburseToSavingsIsWaived.loanAccount.summary.feeChargesWaived == 850

    #     Here Waive Overdue charge I have 12 penalty charges waive some

    #     Waive penalty each applied == 100 * 12 times/size
    * def chargeId_2 = loanResponse.loanAccount.charges[1].id
    * def WaiveChargeResponse_2 = call read('classpath:features/portfolio/loans/loansteps.feature@waiveLoanAccountChargesAndFeesSteps') { loanId : '#(loanId)', chargeId : '#(chargeId_2)'}
    #         fetch loan details here
    * def responseAfterDisburseToSavingsIsWaived_2 = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    #     Assert that disburse to savings charge is waived
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.feeChargesOverdue == 0
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.feeChargesOutstanding == 0
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.feeChargesWrittenOff == 0
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.feeChargesCharged == 850
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.feeChargesWaived == 850
    #    Penalty Assert
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.penaltyChargesOverdue == 1100
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.penaltyChargesOutstanding == 1100
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.penaltyChargesWrittenOff == 0
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.penaltyChargesCharged == 1200
    * assert responseAfterDisburseToSavingsIsWaived_2.loanAccount.summary.penaltyChargesWaived == 100

  @testThatICanCreateLoanAccountWithDisburseBySavingsAccountWithDisburseToSavingsCharge
  Scenario: Test That I Can Create Loan Account With Disburse to savings account Charges
    * def chargeAmount = 10;
       # Create Disburse to Savings Account charge
    * def disburseSavingsCharge = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createDisburseToSavingsAccountLoanChargeFeesOnApprovedAmountSteps') { chargeAmount : '#(chargeAmount)' }
    * def disburseSavingsAccountChargeId = disburseSavingsCharge.chargeId
    * def disburseToSavingsChargeAmount = chargeAmount

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOutChargesSteps')
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def transferAmount = 850
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanAccountWithDisburseToSavingsAccountChargeAndPenaltyChargeSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', savingsAccountId : '#(savingsId)', disburseSavingsAccountChargeId : '#(disburseSavingsAccountChargeId)', disburseToSavingsChargeAmount : '#(disburseToSavingsChargeAmount)'}
    * def loanId = loan.loanId
      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }
      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
      # Pay Disbursement to Savings Account Charges pulling money from savings account to loan loan Account
    * def accountTransferResponse = call read('classpath:features/portfolio/loans/loansteps.feature@accountTransferFromSavingsAccountToLoanAccountSteps') { fromAccountId : '#(savingsId)', fromClientId : '#(clientId)', toAccountId : '#(loanId)', toClientId : '#(clientId)', transferAmount : '#(transferAmount)', transferDate : '#(submittedOnDate)'}

       #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
      #Get Savings Account details and check if money had been deposited
    * def balanceAfterDisbursementToSavings = (loanAmount - 850)

    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert balanceAfterDisbursementToSavings == savingsResponse.savingsAccount.summary.availableBalance
    * assert balanceAfterDisbursementToSavings == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId
    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.feeChargesPaid == 850
    * assert loanResponse.loanAccount.summary.feeChargesOutstanding == 0
    * assert loanResponse.loanAccount.summary.feeChargesWaived == 0
    * assert loanResponse.loanAccount.summary.feeChargesWrittenOff == 0
    * assert loanResponse.loanAccount.summary.feeChargesCharged == 850
    * assert loanResponse.loanAccount.summary.feeChargesOverdue == 0
    * assert loanResponse.loanAccount.status.value == 'Active'
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    # Expecting Three Transactions Disbursement Transaction and Disbursement charge
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 3

  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseBySavingsAccountWithDisburseToSavingsChargeAndRunAccountTransferToMakePaymentOfTheCharges
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges And Disburse it on a savings Account With Disburse to Savings Charge and run Account Transfer to pay disbursement to savings account charge
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

       # Create Disburse to Savings Account charge
    * def disburseSavingsCharge = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createDisburseToSavingsAccountLoanChargeFeesOnApprovedAmountSteps') { chargeAmount : '#(chargeAmount)' }
    * def disburseSavingsAccountChargeId = disburseSavingsCharge.chargeId
    * def disburseToSavingsChargeAmount = 10

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def transferAmount = 850
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanAccountWithDisburseToSavingsAccountChargeAndPenaltyChargeSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)', disburseSavingsAccountChargeId : '#(disburseSavingsAccountChargeId)', disburseToSavingsChargeAmount : '#(disburseToSavingsChargeAmount)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
     # Pay Disbursement to Savings Account Charges pulling money from savings account to loan loan Account
    * def accountTransferResponse = call read('classpath:features/portfolio/loans/loansteps.feature@accountTransferFromSavingsAccountToLoanAccountSteps') { fromAccountId : '#(savingsId)', fromClientId : '#(clientId)', toAccountId : '#(loanId)', toClientId : '#(clientId)', transferAmount : '#(transferAmount)', transferDate : '#(submittedOnDate)'}

      # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
     # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges and Fees',totalCharges)

        #Get Savings Account details and check if money hads been deposited
    * def balanceAfterDisbursementToSavings = (loanAmount - 850)

    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert balanceAfterDisbursementToSavings == savingsResponse.savingsAccount.summary.availableBalance
    * assert balanceAfterDisbursementToSavings == savingsResponse.savingsAccount.summary.accountBalance
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1100
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1100
    * assert loanResponse.loanAccount.summary.penaltyChargesPaid == 100
    * assert loanResponse.loanAccount.summary.feeChargesPaid == 750
    * assert loanResponse.loanAccount.summary.feeChargesOutstanding == 100
    * assert loanResponse.loanAccount.summary.feeChargesWaived == 0
    * assert loanResponse.loanAccount.summary.feeChargesWrittenOff == 0
    * assert loanResponse.loanAccount.summary.feeChargesCharged == 850
    * assert loanResponse.loanAccount.summary.feeChargesOverdue == 100
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 13
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    # Expecting Two Transactions Disbursement Transaction and Disbursement charge
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 3

  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndWaiveInterest
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges and waive interest
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburse') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
        # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

      # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges',totalCharges)
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue


    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 12
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 1
    # Waive Interest
    * def transactionAmount = loanResponse.loanAccount.summary.interestOutstanding;
    * def waiveInterestResponse = call read('classpath:features/portfolio/loans/loansteps.feature@waiveInterestOnLoanAccountSteps') { transactionDate : '#(submittedOnDate)', transactionAmount : '#(transactionAmount)', loanId : '#(loanId)'}
    #fetch loan details here
    * def waivedLoanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert waivedLoanResponse.loanAccount.summary.interestOutstanding == 0
    * assert waivedLoanResponse.loanAccount.summary.interestOverdue == 0
    * assert waivedLoanResponse.loanAccount.summary.interestWaived == transactionAmount
    * assert karate.sizeOf(waivedLoanResponse.loanAccount.transactions) == 2
    * assert loanResponse.loanAccount.status.value == 'Active'
    # WriteOff Loan Account
    * def writeOffLoanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@writeOffOnLoanAccountSteps') { transactionDate : '#(submittedOnDate)', loanId : '#(loanId)'}

       #fetch loan details here
    * def writeOffResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    * assert writeOffResponse.loanAccount.status.value == 'Closed (written off)'
    * assert writeOffResponse.loanAccount.summary.principalOverdue == 8500
    * assert writeOffResponse.loanAccount.summary.principalWrittenOff == 8500
    * assert writeOffResponse.loanAccount.summary.principalOutstanding == 0
    * assert writeOffResponse.loanAccount.summary.interestOverdue == 0
    * assert writeOffResponse.loanAccount.summary.interestOutstanding == 0
    * assert writeOffResponse.loanAccount.summary.feeChargesOutstanding == 0
    * assert writeOffResponse.loanAccount.summary.feeChargesOverdue == 0
    * assert writeOffResponse.loanAccount.summary.penaltyChargesOutstanding == 0
    * assert writeOffResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert writeOffResponse.loanAccount.summary.penaltyChargesWrittenOff == 1200

        # RecoveryPayment Loan Account
    * def recoveryPaymentLoanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@recoveryPaymentLoanAccountSteps') { transactionDate : '#(submittedOnDate)', loanId : '#(loanId)', transactionAmount : '#(loanAmount)'}
           #fetch loan details here
    * def recoveryPaymentLoanAccountResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert recoveryPaymentLoanAccountResponse.loanAccount.summary.totalRecovered == loanAmount

  @testThatICanCreateLoanAccountWithFlatOverduePenaltyWithFeesAndMakeRepayment
  Scenario: Test That I Can Create Loan Account with flat overdue penalty with fees and make repayment
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

       # Create Disburse to Savings Account charge
    * def disburseSavingsCharge = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createDisburseToSavingsAccountLoanChargeFeesOnApprovedAmountSteps') { chargeAmount : '#(chargeAmount)' }
    * def disburseSavingsAccountChargeId = disburseSavingsCharge.chargeId
    * def disburseToSavingsChargeAmount = 10

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanAccountWithDisburseToSavingsAccountChargeAndPenaltyChargeSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)', disburseSavingsAccountChargeId : '#(disburseSavingsAccountChargeId)', disburseToSavingsChargeAmount : '#(disburseToSavingsChargeAmount)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
      # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
     # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges and Fees',totalCharges)
     # Expectation Disbursement to Savings Account and Penalty/Overdue Charge
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue + loanResponse.loanAccount.summary.feeChargesOverdue

        #Get Savings Account details and check if money hads been deposited
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert loanAmount == savingsResponse.savingsAccount.summary.availableBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert loanResponse.loanAccount.summary.feeChargesOverdue == 850
    * assert loanResponse.loanAccount.summary.feeChargesOutstanding == 850
    * assert loanResponse.loanAccount.summary.feeChargesCharged == 850
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 13
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    # Expecting Two Transactions Disbursement Transaction and Disbursement charge
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 2

     #Loan Repayment Date
    * def repaymentDate = df.format(faker.date().past(2, 1, TimeUnit.DAYS))


    # Make Repayments for  each schedule period
    * def totalOutstanding_1 = loanResponse.loanAccount.summary.totalOutstanding

    * def repaymentSchedule_1 = loanResponse.loanAccount.repaymentSchedule.periods[1].totalDueForPeriod
    Then print 'repaymentSchedule_1',repaymentSchedule_1
    * call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(repaymentSchedule_1)', repaymentDate : '#(repaymentDate)'}
    * def loanResponse_1 = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert karate.sizeOf(loanResponse_1.loanAccount.transactions) == 3
    * assert loanResponse_1.loanAccount.summary.totalOutstanding == (totalOutstanding_1 - repaymentSchedule_1)


     #make payment repaymentDate, repaymentAmount
    * def totalOutstanding_2 = loanResponse_1.loanAccount.summary.totalOutstanding

    * def repaymentSchedule_2 = loanResponse.loanAccount.repaymentSchedule.periods[2].totalDueForPeriod
    Then print 'repaymentSchedule_2',repaymentSchedule_2
    * call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(repaymentSchedule_2)', repaymentDate : '#(repaymentDate)'}
    * def loanResponse_2 = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert karate.sizeOf(loanResponse_2.loanAccount.transactions) == 4
    * assert loanResponse_2.loanAccount.summary.totalOutstanding == (totalOutstanding_2 - repaymentSchedule_2)


     #make payment repaymentDate, repaymentAmount
    * def totalOutstanding_3 = loanResponse_2.loanAccount.summary.totalOutstanding

    * def repaymentSchedule_3 = loanResponse.loanAccount.repaymentSchedule.periods[3].totalDueForPeriod
    Then print 'repaymentSchedule_3',repaymentSchedule_3
    * call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(repaymentSchedule_3)', repaymentDate : '#(repaymentDate)'}
    * def loanResponse_3 = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert karate.sizeOf(loanResponse_3.loanAccount.transactions) == 5
    * assert loanResponse_3.loanAccount.summary.totalOutstanding == (totalOutstanding_3 - repaymentSchedule_3)
  @OXY-163-test-that-a-virtual-schedule-should-not-be-added-when-number-of-repayment-are-greater-than-one-and-advancePaymentInterestForExactDaysInPeriod-is-true
  Scenario: OXY-163 Loan Schedule with interest recalculation enabled is only 3 periods long regardless of the number of repayments set with 12 schedule, a virtual schedule should not be added
        # Create Loan Product
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps')
    * def loanProductId = loanProduct.loanProductId

    #Loan and client creation date
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId


    * def loanAmount = 10000
    * def loanTermFrequency = 12
    * def numberOfRepayments = 12
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@400-OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', loanTermFrequency : '#(loanTermFrequency)', numberOfRepayments : '#(numberOfRepayments)'}



  @OXY-163-test-that-a-virtual-schedule-should-be-added-when-number-of-repayment-are-equal-to-one-and-advancePaymentInterestForExactDaysInPeriod-is-true
  Scenario: OXY-163 Loan Schedule with interest recalculation enabled is only 3 periods long regardless of the number of repayments set with 12 schedule, a virtual schedule should be added
        # Create Loan Product
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps')
    * def loanProductId = loanProduct.loanProductId

    #Loan and client creation date
    * def submittedOnDate = df.format(faker.date().past(7, 5, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId


    * def loanAmount = 10000
    * def loanTermFrequency = 1
    * def numberOfRepayments = 1
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', loanTermFrequency : '#(loanTermFrequency)', numberOfRepayments : '#(numberOfRepayments)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburse') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}

    #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 1
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == 2
      #Loan Repayment Date
    * def repaymentDate = df.format(faker.date().past(4, 3, TimeUnit.DAYS))


    # Make Repayments for  each schedule period
    * def totalOutstanding_1 = loanResponse.loanAccount.repaymentSchedule.periods[1].interestDue/2

    Then print 'repaymentDate',repaymentDate
    Then print 'totalOutstanding_1',totalOutstanding_1

    * call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(totalOutstanding_1)', repaymentDate : '#(repaymentDate)'}
        #fetch loan details here
    * def loanResponseAfterRepayment = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    * assert karate.sizeOf(loanResponseAfterRepayment.loanAccount.repaymentSchedule.periods) >2
    * assert karate.sizeOf(loanResponseAfterRepayment.loanAccount.transactions) == 2



  @OXY-163-test-that-a-virtual-schedule-should-be-added-when-number-of-repayment-are-equal-to-one-and-advancePaymentInterestForExactDaysInPeriod-is-true-and-update-to-12-should-fail
  Scenario: OXY-163 Loan Schedule with interest recalculation enabled is only 3 periods long regardless of the number of repayments set with 12 schedule, a virtual schedule should be added-and-update-to-12-should-fail
        # Create Loan Product
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps')
    * def loanProductId = loanProduct.loanProductId

    #Loan and client creation date
    * def submittedOnDate = df.format(faker.date().past(7, 5, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId


    * def loanAmount = 10000
    * def loanTermFrequency = 1
    * def numberOfRepayments = 1
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', loanTermFrequency : '#(loanTermFrequency)', numberOfRepayments : '#(numberOfRepayments)'}
    * def loanId = loan.loanId

    # Update should fail
    * def loanTermFrequency = 12
    * def numberOfRepayments = 12
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@Update-400-OXY163loanScheduleWithInterestRecalculationEnabledIsOnly3PeriodsLongRegardlessOfTheNumberOfRepaymentsSetSteps') {loanId : '#(loanId)', submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', loanTermFrequency : '#(loanTermFrequency)', numberOfRepayments : '#(numberOfRepayments)'}


  @testThatICanHoldFundsOnASavingsAccountWhenTheLinkedLoanAccountIsInArrearsAndTheOtherSavingsAccountOfTheClientShouldNotBePutOnHold
  Scenario: Test that i can hold funds on a savings account when the linked loan account is in arrears and the other savings account of the client should not be put on hold
    # https://fiterio.atlassian.net/browse/OXY-232
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId



        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId



    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)' }
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
         # Run Clone Job
    * def applyPenaltyCharge = call read('classpath:features/portfolio/loans/loansteps.feature@runCloneJobForLoanPenalty') { loanId : '#(loanId)'}
       #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    #- Add Global Configuration --- enforce_loan_overdue_amount_min_balance_check  mentioned here https://fiterio.atlassian.net/browse/OXY-42
    * def enforce_loan_overdue_amount_min_balance_check_id = 48
    * def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_enforce_loan_overdue_amount_min_balance_check_step') { configurationsId : '#(enforce_loan_overdue_amount_min_balance_check_id)' }
    #- Create Savings Account Two without relationship with Loan Account

      #Create Savings Account Product and Savings Account
    * def savingsAccount_2 = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId_2 = savingsAccount_2.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId_2)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings_2 = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId_2)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId_2 = activateSavings_2.activeSavingsId

    * def tx_amount = 1000
    * def tx_date = df.format(faker.date().past(15, TimeUnit.DAYS))
    #-Deposit on savings account two
    * def deposit_account2_requestVariables = { savingsId : '#(activeSavingsId_2)', transactionAmount : '#(tx_amount)', transactionDate : '#(tx_date)', command : 'deposit' }
    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@transaction') deposit_account2_requestVariables

    #-Withdraw
    * def withdraw_account2_requestVariables = { savingsId : '#(activeSavingsId_2)', transactionAmount : '#(tx_amount)', transactionDate : '#(tx_date)', command : 'withdrawal' }
    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@transaction') withdraw_account2_requestVariables


    * def deposit_account1_depositAmount = 10
    * def account1_transactionDate = df.format(faker.date().past(15, TimeUnit.DAYS))
     #-Deposit on savings account one which is linked to Loan Account . Withdrawal must fail since there is outstanding balance on linked loan account
    * def deposit_account1_requestVariables = { savingsId : '#(savingsId)', transactionAmount : '#(deposit_account1_depositAmount)', transactionDate : '#(account1_transactionDate)', command : 'deposit' }
    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@transaction') deposit_account1_requestVariables

    #-Withdraw
    * def withdraw_account1_Amount = 8500
    * def withdraw_account1_requestVariables = { savingsId : '#(savingsId)', transactionAmount : '#(withdraw_account1_Amount)', transactionDate : '#(account1_transactionDate)', command : 'withdrawal' }
    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@transactionWithBadRequest') withdraw_account1_requestVariables

    #- Disable configuration  ---enforce_loan_overdue_amount_min_balance_check
    * def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_enforce_loan_overdue_amount_min_balance_check_step') { configurationsId : '#(enforce_loan_overdue_amount_min_balance_check_id)' }