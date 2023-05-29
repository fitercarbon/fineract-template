Feature: Test client apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl


  @createFetchUpdateEntityClient
    Scenario: Create fetch and update Entity client

    #- Disable configuration  ---address
    *  def addressConfigName = 'Enable-Address'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(addressConfigName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(response.globalConfig.id)' }

    #- Fetch codeValue for constitutionId
    * def constitutionCodeId = 24
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(constitutionCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : constitutionCodeId, name : 'Test'});
    * def constitutionCodeValueId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print constitutionCodeValueId

    #- Create client Entity without address
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def createdClient = call read('classpath:features/portfolio/clients/clientsteps.feature@createEntityStep') { clientCreationDate : '#(submittedOnDate)', constitutionId : '#(constitutionCodeValueId)'}
    * def createdClientId = createdClient.clientId

    # Activate client
    * def activatedClient = call read('classpath:features/portfolio/clients/clientsteps.feature@activateClientStep') { clientId : '#(createdClientId)'}
    * assert createdClientId == activatedClient.res.clientId

    # Fetch created client
    * def legalFormId = 2
    * def fetchedClient = call read('classpath:features/portfolio/clients/clientsteps.feature@findbyclientid') { clientId : '#(createdClientId)'}
    * def client = fetchedClient.client
    * match createdClientId == client.id
    * match legalFormId == client.legalForm.id

    # Update fetched client
    * def accountNo = client.accountNo
    * def fullname = "Business update"
    * def updatedClient = call read('classpath:features/portfolio/clients/clientsteps.feature@updateEntityStep') { clientId : '#(createdClientId)', accountNo : '#(accountNo)', constitutionId : '#(constitutionCodeValueId)'}
    * assert createdClientId == updatedClient.res.resourceId
    * match updatedClient.res.changes contains { externalId: '#notnull'}
    * assert fullname == updatedClient.res.changes.fullname
    * assert true == updatedClient.res.changes.isRegistered


  @createFetchAndUpdatePersonClient
    Scenario: Create fetch and update Normal client

    #- Disable configuration  ---address
    *  def addressConfigName = 'Enable-Address'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(addressConfigName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(response.globalConfig.id)' }

    #- Create client without address
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


 @createClientWithSavings
    Scenario: Create client with savings account

    #- Disable configuration  ---address
    *  def addressConfigName = 'Enable-Address'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(addressConfigName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(response.globalConfig.id)' }

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

    #- Disable configuration  ---address
    *  def addressConfigName = 'Enable-Address'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(addressConfigName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(response.globalConfig.id)' }

    #- Check validation limit present in the system or not
    * def result = call read('classpath:features/portfolio/products/validationLimitsSteps.feature@list')
    * def limits =  result.validationLimits
    * def limitValue = ((karate.sizeOf(limits) > 0)) ? limits[0] : {}
    * print limits, karate.sizeOf(limits), limitValue

    #- If present proceed otherwise create limit and then proceed
    * def createdValLimit = if (karate.sizeOf(limits) < 1) karate.call('classpath:features/portfolio/products/validationLimitsSteps.feature@createValidationLimit');
    * def createdId = createdValLimit != null ? createdValLimit.code.resourceId : 0

    #- Fetch limit for for created client
    * def res = if(createdId != null && createdId >0) {karate.call('classpath:features/portfolio/products/validationLimitsSteps.feature@fetchById', { validationLimitId : createdId });}
    * print res
    * def limitValue = (res != null ? res.validationLimit : limitValue)
    * print limitValue

    #- Create client with more than set limits
    * def addValue = 100
    * def clientLevel = limitValue.clientLevel.id
    * def dailyLimit = limitValue.maximumClientSpecificDailyWithdrawLimit + addValue
    * def singleLimit = limitValue.maximumClientSpecificSingleWithdrawLimit + addValue
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def clientResult = call read('classpath:features/portfolio/clients/clientsteps.feature@createClientWithWithdrawalLimitsStep') { clientCreationDate : '#(submittedOnDate)',dailyWithdrawLimit : '#(dailyLimit)', singleWithdrawLimit : '#(singleLimit)', clientLevel : #(clientLevel)}
    * print clientResult