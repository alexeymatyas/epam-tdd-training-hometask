To run:
1. mvn install
2. java -jar target/fxcalc-0.0.1-SNAPSHOT.jar usd100.0+cny5.0=cny



# Application description
- Application allows to calculate value of expressions given in form `<ccy1><amt1><OPERARION+-*/><ccy2><amt2> = <rslt ccy>` (e.g. usd100.0+cny5.0=cfh). 
- Application can parse expressions from Strings.
- Application does automatic currency conversion through public FX rates service Fixerio (http://fixer.io/)
- Application is implemented as a maven project
- **Application doesn't have any unit-tests implemented**

# Preparation
1. Fork repository under your own Github account
1. Import maven project into your IDE
1. Make sure project is built successfully by running `mvn clean install`

# Task
1. Think of unit- and intergration tests that would be valuable. List them down.
1. Implement at least 3 unit-tests out of what you listed. 3 is mandatory, but the more you implement - the better. Just try avoiding redundant tests as we discussed.
  1. Use Junit framework
  1. Use some of Hamcrest matchers at least once in your tests
  1. Use Mockito for mocking/stubbing at least once. Hint: mock you FixerioServiceProxy.
  1. Use Mockito to verify behavior. Hint: in some cases currency conversion is not needed, so that no need to call Fixerio. You can verify that.
1. Try running your tests both with IDE and Maven capabilities. Pay attention to how results are presented in both cases.
1. Optional. Implement integration test. Make sure it's executed when running `mvn install`
1. Commit all your changes to your fork. Create pull request from your fork to my original repository. In comment please provide your English name.
