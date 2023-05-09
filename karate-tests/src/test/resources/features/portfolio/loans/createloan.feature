Feature: Test loan account apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @createanddisburseloan
  Scenario: Create approve and disburse loan
      #to choose an earlier date use faker.date().past(20, TimeUnit.DAYS)
      #loan creation made 30 days back
    * def submittedOnDate = df.format(faker.date().past(370, 369, TimeUnit.DAYS))
    * def loanAmount = 1000
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createloan') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)' }
    * def loanId = loan.loanId
      #approval
    * def approvalDate = submittedOnDate
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(approvalDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disbursementDate = submittedOnDate
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburse') { loanAmount : '#(loanAmount)', disbursementDate : '#(disbursementDate)', loanId : '#(loanId)' }
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyid') { loanId : '#(loanId)' }
    * def outstandingBalance = loanResponse.loanAccount.summary.totalExpectedRepayment
    * def maturityDate = loanResponse.loanAccount.timeline.expectedMaturityDate
    * print maturityDate
    * def expectedRepaymentDate = calendar
    * print maturityDate[0]+"-"+maturityDate[1]+"-"+maturityDate[2]
    * expectedRepaymentDate.set(maturityDate[0], maturityDate[1] - 1, maturityDate[2])
    * print expectedRepaymentDate
    * def repaymentDate = df.format(expectedRepaymentDate.getTime())
     #make payment repaymentDate, repaymentAmount
    * def repayment = call read('classpath:features/portfolio/loans/loansteps.feature@loanRepayment') { loanId : '#(loanId)', repaymentDate :  '#(repaymentDate)', dateFormat : '#(dateFormat)', repaymentAmount : '#(outstandingBalance)' }
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyid') { loanId : '#(loanId)' }
    * assert outstandingBalance == loanResponse.loanAccount.summary.totalRepayment
    * assert loanResponse.loanAccount.status.closed == true
    * assert loanResponse.loanAccount.status.overpaid == false
    * assert loanResponse.loanAccount.status.closedObligationsMet == true

  @testThatICanCreateAndDisburseLoanToSavingsAccount
  Scenario: Test That I Can Create And Disburse Loan To savings account

    * def loanProduct = call read('classpath:features/portfolio/products/loanproduct.feature@fetchdefaultproduct')
    * def loanProductId = loanProduct.loanProductId
    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))

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
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', savingsAccountId : '#(savingsId)' }
    * def loanId = loan.loanId

      #approval
    * def approvalDate = submittedOnDate
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(approvalDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disbursementDate = submittedOnDate
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(disbursementDate)', loanId : '#(loanId)' }
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    #Get Savings Account details and check if money hads been deposited
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert loanAmount == savingsResponse.savingsAccount.summary.availableBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 1
    * assert loanResponse.loanAccount.transactions[0].type.value == 'Disbursement'
    * assert loanResponse.loanAccount.transactions[0].amount == loanAmount
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    # plus one is the disbursement transaction returned in the schedule
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    # Undo Disbursal of this Loan Account
    * def undisbursedLoanAccountReponse = call read('classpath:features/portfolio/loans/loansteps.feature@unDisburseLoanAccountStep') { loanId : '#(loanId)' }
    * def loanAccountNotActiveReponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert loanAccountNotActiveReponse.loanAccount.status.value == 'Approved'
    #  Un-Approve Loan Account
    * def undApproveLoanAccountReponse = call read('classpath:features/portfolio/loans/loansteps.feature@unApproveLoanAccountStep') { loanId : '#(loanId)' }
    * def loanAccountNotApprovedReponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * assert loanAccountNotApprovedReponse.loanAccount.status.value == 'Submitted and pending approval'
     #  Reject Loan Account
    * def rejectedLoanAccountReponse = call read('classpath:features/portfolio/loans/loansteps.feature@rejectedLoanAccountStep') { loanId : '#(loanId)', rejectedOnDate : '#(submittedOnDate)' }
    * def loanAccounteRectedReponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)'}
    * assert loanAccounteRectedReponse.loanAccount.status.value == 'Rejected'

  @testThatLoanAccountCreationCannotViolateProductSettingsConfiguration
  Scenario: Test That Loan Account Creation Can not Violate Product Settings Configuration

    * def loanProduct = call read('classpath:features/portfolio/products/loanproduct.feature@fetchdefaultproduct')
    * def loanProductId = loanProduct.loanProductId
    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId
    # Principal Amount should not be greater than the maximum principal set on the product
    * def loanAmount = 8500000
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createloanTemplate400Step') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanProductId : '#(loanProductId)', clientId : '#(clientId)'}

    # Loan Account can not be created with Date before the client creation
    * def LoanCreationDate = df.format(faker.date().past(50, 29, TimeUnit.DAYS))
    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createloanTemplate403Step') { submittedOnDate : '#(LoanCreationDate)', loanAmount : '#(loanAmount)', loanProductId : '#(loanProductId)', clientId : '#(clientId)'}

  @testUndoLoanReschedule
  Scenario: Test Undo Loan Reschedule
    Given configure ssl = true
     * def today=java.time.LocalDate.now()
    * def loansData = read('classpath:templates/loans.json')
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def rescheduledFrom = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def adjustedDueDate=df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def loanAmount = 8500000
    * def loanProduct = call read('classpath:features/portfolio/products/loanproduct.feature@fetchdefaultproduct')
    * def loanProductId = loanProduct.loanProductId

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.

    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createloanTemplate400Step') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanProductId : '#(loanProductId)', clientId : '#(clientId)'}
    * def loanId = loan.loanId
    * def loanResponseBeforeReschedule = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    * def loanReschedule = call read('classpath:features/portfolio/loans/loansteps.feature@loanRescheduleSteps') { submittedOnDate : '#(submittedOnDate)', rescheduledFrom : '#(rescheduledFrom)', adjustedDueDate : '#(adjustedDueDate)', loanId : '#(loanId)' }

    Given  path 'rescheduleloans',loanId,
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loanSearchPayload.loanPreviousSearchPayload
    When call read('classpath:features/portfolio/loans/createloan.feature@createanddisburseloan')
    And request loansData.undoLoanReschedulePayLoad
    When method POST
    Then status 200
    Then match $ contains { loanId: '#notnull' }
    And response.loanId=loanId

    * def loanResponseAfterUndoReschedule = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    #Compare first and last repayment schedule before and after undo loan reschedule
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[0].fromDate == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[0].fromDate
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[0].dueDate == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[0].dueDate
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[0].principalDue == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[0].principalDue
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[0].daysInPeriod == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[0].daysInPeriod

    #Compare the last repayments
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[-1].fromDate == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[-1].fromDate
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[-1].dueDate == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[-1].dueDate
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[-1].principalDue == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[-1].principalDue
    * assert loanResponseAfterUndoReschedule.repaymentSchedule.futurePeriods[-].daysInPeriod == loanResponseBeforeReschedule.repaymentSchedule.futurePeriods[-1].daysInPeriod

