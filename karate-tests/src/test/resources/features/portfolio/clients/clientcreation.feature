Feature: Test client apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

    @Ignore
    @createFetchUpdateEntityClient
    Scenario: Create fetch and update Entity client
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@createEntityStep') { clientCreationDate : '#(submittedOnDate)'}
    * def createdClientId = result.clientId

    # Activate client
    * def activatedClient = call read('classpath:features/portfolio/clients/clientsteps.feature@activateClientStep') { clientId : '#(createdClientId)'}
    * assert createdClientId == activatedClient.res.clientId

    # Fetch created client
    * def legalFormId = 2
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@findbyclientid') { clientId : '#(createdClientId)'}
    * def client = result.client
    * match createdClientId == client.id
    * match legalFormId == client.legalForm.id

    # Update fetched client
    * def accountNo = client.accountNo
    * def fullname = "Business update"
    * def updatedClient = call read('classpath:features/portfolio/clients/clientsteps.feature@updateEntityStep') { clientId : '#(createdClientId)', accountNo : '#(accountNo)'}
    * assert createdClientId == updatedClient.res.resourceId
    * match updatedClient.res.changes contains { externalId: '#notnull'}
    * assert fullname == updatedClient.res.changes.fullname

    @Ignore
    @createFetchAndUpdatePersonClient
    Scenario: Create fetch and update Normal client
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)'}
    * def createdClientId = result.clientId

    # Fetch created client
    * def legalFormId = 1
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@findbyclientid') { clientId : '#(createdClientId)'}
    * def client = result.client
    * match createdClientId == client.id
    * match legalFormId == client.legalForm.id

    # Update fetched client
    * def updatedClient = call read('classpath:features/portfolio/clients/clientsteps.feature@update') { clientId : '#(createdClientId)'}
    * assert createdClientId == updatedClient.res.resourceId


    #createClientWithSavings
    Scenario: Create client with savings account

    # Fetch saving product
    * def savingsProduct = call read('classpath:features/portfolio/products/savingsproduct.feature@fetchdefaultproduct')
    * def savingsProductId = savingsProduct.savingsProductId

    # Then create client
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@createClientWithSavingsStep') { savingsProductId : '#(savingsProductId)'}
    * def savingsId = result.client.savingsId

    # Fetch savings account for created client
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert savingsProductId == savingsResponse.savingsAccount.savingsProductId


    @testDailyWithdrawalLimitAndSingleWithdrawalLimitCanNotExceedGlobalLimit
    Scenario: Test DailyWithdrawalLimit And SingleWithdrawalLimit Can Not Exceed GlobalLimit
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/products/validationLimitsSteps.feature@list')
    * def limits =  result.validationLimits
    * def limitValue = ((karate.sizeOf(limits) > 0)) ? limits[0] : {}
    * print limits, karate.sizeOf(limits), limitValue

    * def createdValLimit = if (karate.sizeOf(limits) < 1) karate.call('classpath:features/portfolio/products/validationLimitsSteps.feature@createValidationLimit');
    * def createdId = createdValLimit != null ? createdValLimit.code.resourceId : 0
    * def res = if(createdId != null && createdId >0) {karate.call('classpath:features/portfolio/products/validationLimitsSteps.feature@fetchById', { validationLimitId : createdId });}
    * print res
    * def limitValue = (res != null ? res.validationLimit : limitValue)
    * print limitValue

    * def addValue = 100
    * def clientLevel = limitValue.clientLevel.id
    * def dailyLimit = limitValue.maximumClientSpecificDailyWithdrawLimit + addValue
    * def singleLimit = limitValue.maximumClientSpecificSingleWithdrawLimit + addValue
    * def clientResult = call read('classpath:features/portfolio/clients/clientsteps.feature@createClientWithWithdrawalLimitsStep') { clientCreationDate : '#(submittedOnDate)',dailyWithdrawLimit : '#(dailyLimit)', singleWithdrawLimit : '#(singleLimit)', clientLevel : #(clientLevel)}
    * print clientResult