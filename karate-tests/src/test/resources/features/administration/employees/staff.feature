Feature: staff creation api tests
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * configure ssl = true


  @list
  Scenario: Retrieve all staff
    Given path 'staff'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def staff = response

  @ignore
  @findone
  Scenario: Retrieve one by id
    Given path 'staff',staffId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def staff = response

  @ignore
  @edit
  Scenario: Edit staff
    Given path 'staff',staffId
    And params { status : 'all' }
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def firstname = faker.name().firstName()
    * def lastname = faker.name().lastName()
    * def mobileNo = faker.phoneNumber()
    * def joiningDate = df.format(calendar.getTime())
    * def administration = read('classpath:templates/administration.json')
    And request administration.staff
    When method PUT
    Then status 200
  @Ignore
  @create
  Scenario: Create staff
    Given path 'staff'
    And params { status : 'all' }
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def firstname = faker.name().firstName()
    * def lastname = faker.name().lastName()
    * def mobileNo = faker.phoneNumber()
    * def joiningDate = df.format(calendar.getTime())
    * def administration = read('classpath:templates/administration.json')
    And request administration.staff
    When method POST
    Then status 200
    Then def staffId = response.resourceId
    Then def staffResponse = call read('classpath:features/administration/employees/staff.feature@findone') { staffId : '#(staffId)'}
    Then call read('classpath:features/administration/employees/staff.feature@edit') { staffId : '#(staffId)'}