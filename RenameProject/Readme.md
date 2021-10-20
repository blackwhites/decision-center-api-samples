# Rename rule project in Decision Center

This tool allows to rename a rule project in Decision Center. 

## Compiling

This solution is build using the dependencies from [this project](https://github.com/ODMDev/odm-libs-in-maven/blob/master/README.md) and an installation of ODM.

1. Clone the project from github  
1. Configure the properties in pom.xml to match your version and configuration
1. Run maven clean deploy


## Installing

- Download the [release](https://github.com/ODMDev/decision-center-api-samples/releases/tag/2.0.0)
- Add the jar configure-dt-0.0.1-SNAPSHOT.jar in teamserver/lib folder

## Usage

- run 
java -cp "<odm_home>/teamserver/lib/*:<odm_home>/teamserver/lib/eclipse_plugins/*:" com.ibm.odm.tools.RenameProject

  java com.ibm.odm.tools.RenameProject -username username -password password -url url -dataSource dataSource -ruleProject <projectName> -to <newName>   
	rename the rule project and update dependencies in all branches.

### Arguments

-username        : an administrator account   
-password        : the user password  
-url             : decision center URL   
-dataSource      : the JNDI name of the datasource usually jdbc/ilogDataSource  
-ruleProject     : the name of the rule project to rename
-to              : the new name of the rule project
