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


#tests that would be valuable

CalculationCommand

- parseFromString
When string is usd1&cny2=usd, throw CommandFormatException.
When string is usd1.&cny2.=usd, throw CommandFormatException.
When string is usd1.0&cny2.0=usd, return a CalculationCommand.
When string is usd1.0&cny2.0=usd, return a CalculationCommand.
When string is usd10000000000000000000.0&cny2.0=usd, return a CalculationCommand.
When string is <leftCurrency>1+<rightCurrency>2=<resultCurrency>, return a CalculationCommand with leftOperand.currencyCode <leftCurrency> and leftOperand.currencyCode <rightCurrency> and resultCurrencyCode Optional.of(<resultCurrency>).
When string is usd1+cny2=, return a CalculationCommand with resultCurrencyCode Optional.empty().
When string is usd<leftValue>+cny<rightValue>=usd, return a CalculationCommand with leftOperand.value <leftValue> and rightOperand.value <rightValue>.
When string is usd1<Operation>cny2=usd, return a CalculationCommand with operation <Operation>.

CalculationCommandExecutionServiceImpl

- getConvertedValue
When value = {value:1.0, currencyCode:gbp} and targetCurrencyCode = "cny" and fxRatesService = oneGBPToTenCNYMock, return 10.0
When value = {value:10000000000000000001.0, currencyCode:gbp} and targetCurrencyCode = "cny" and fxRatesService = oneGBPToTenCNYMock, return 100000000000000000010
When value = {value:0.10000000000000000001.0, currencyCode:gbp} and targetCurrencyCode = "cny" and fxRatesService = oneGBPToTenCNYMock, return 0.100000000000000000010
When value = {value:1.0, currencyCode:gbp} and targetCurrencyCode = "gbp" and fxRatesService = null, return 1.0
When value = {value:1.0, currencyCode:zzz} and targetCurrencyCode = "cny" and fxRatesService = oneGBPToTenCNYMock, throw UnknownCurrencyException.
When value = {value:1.0, currencyCode:null} and targetCurrencyCode = "cny" and fxRatesService = oneGBPToTenCNYMock, return 1.0
When value = {value:Optional.empty(), currencyCode:gbp} and targetCurrencyCode = "cny" and fxRatesService = oneGBPToTenCNYMock, throw a java.lang.NullPointerException exception.

- calculate
In: A CalculationCommand with a leftOperand, an operation, a rightOperand and a resultCurrencyCode.
Return: A Value with the resultCurrencyCode.









