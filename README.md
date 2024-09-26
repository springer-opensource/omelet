## WARNING: this repository is not currently maintained!    

**There may be known security issues or just general bit-rot. We hope the code may remain useful to some but we cannot offer any support at this time.**


	**********************************************
	**********************************************
	                      .__          __
	  ____   _____   ____ |  |   _____/  |_
	/  _  \ /     \_/ __ \|  | _/ __ \   __\
	( <_>  )  Y Y  \  ___/|  |_\  ___/|  |
	\____ /|__|_|  /\___  >____/\___  >__|
	        \/     \/          \/
	**********************************************
	           LET'S COOK SOME TESTS!
	**********************************************
 
======
[![Build Status](https://travis-ci.org/springer-opensource/omelet.svg?branch=master)](https://travis-ci.org/springer-opensource/omelet)

What is Omelet
--------------
Very simple yet powerful automation library for **Selenium WebDriver** which uses one of the most popular test runner for UI Automation- **TestNG**

Omelet Vagrant Box
----------------
if you would like to work on fresh machine, then there is vagrant box built for the development of automation script
https://github.com/springernature/omelet-dev-box

Salient Features
----------------
* Multi Parallel testing
* Support all Cloud solution like BrowserStack, SauceLabs, Testingbot
* Data driver testing
* Step level report generation 
* Auto screen Shot of failed test steps
* Command Line support for CI integration
* ExpectedConditionsExtended for PageObjects Design pattern
* Auto Re-Run failed test cases
 and many more ... 

Build
------
1. mvn clean install
2. Jar will be created in target folder with name :**omelet-1.0.xxx.jar**
    


[Quick Start](http://springer-opensource.github.io/omelet/Quick-Start/)
----------


Latest version can be checked on maven central over [here](http://maven-repository.com/search?q=omelet)

ReportNG dependency
----------------
        <dependency>
			<groupId>com.google.inject</groupId>
			<artifactId>guice</artifactId>
			<version>3.0</version>
		</dependency>
		<dependency>
			<groupId>org.uncommons</groupId>
			<artifactId>reportng</artifactId>
			<version>1.1.2</version>
			<type>jar</type>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.testng</groupId>
			<artifactId>testng</artifactId>
			<version>5.7</version>
			<type>jar</type>
			<classifier>jdk15</classifier>
			<scope>compile</scope>
		</dependency>
Reports
-----------

Omelet uses reportNG for reporting and there has to be guice dependency for reportNG to work 
You may face issue with dependencies related to ReportNG, do read this [blog](http://solidsoft.wordpress.com/2011/01/23/better-looking-html-test-reports-for-testng-with-reportng-maven-guide/)
### Reports Path 

* If we do mvn test(or clean install ) then reports will be generated in target/surefire-reports/html/index.html

* if we run via TestNg then report can be found in test-output/html/index.html

Data
----------
* Simple Properties file
* Xml 
* Google Sheets 

Discussions
------------
Google Grooups: https://groups.google.com/forum/#!forum/omelet

Todo
-------
* Though everything works fine :) but more unit test cases
* Off-course more documentation 
* Improve java docs
* Naming convention 
* And some nitty-gritty stuff
