# Rename rule project in Decision Center

This tool allows to rename a rule project in Decision Center. 

## Building

This solution is build using the dependencies from [this project](https://github.com/ODMDev/odm-libs-in-maven/blob/master/README.md) and an installation of ODM.

1. Clone the project from github  
1. Configure the properties in pom.xml to match your version and configuration
1. Run maven clean deploy


## Installing

- Add the jar [RenameProject-0.0.1-SNAPSHOT.jar](https://github.com/ODMDev/decision-center-api-samples/raw/master/RenameProject/RenameProject-0.0.1-SNAPSHOT.jar) in teamserver/lib folder

## Usage

On Linux   
```java -cp "<odm_home>/teamserver/lib/*:<odm_home>/teamserver/lib/eclipse_plugins/*:" com.ibm.odm.tools.RenameProject -username <username> -password <password> -url <url> -dataSource <dataSource> -ruleProject <projectName> -to <newName>```

On Windows   
```java -cp "<odm_home>\teamserver\lib\*;<odm_home>\teamserver\lib\eclipse_plugins\*;" com.ibm.odm.tools.RenameProject -username <username> -password <password> -url <url> -dataSource <dataSource> -ruleProject <projectName> -to <newName>```

rename the rule project *projectName* into *newName* and update dependencies to this project in all branches.

### Parameters

**-username**        : an administrator account   
**-password**        : the user password  
**-url**             : decision center URL  (```http://server:port/decisioncenter```)    
**-dataSource**      : the JNDI name of the datasource usually jdbc/ilogDataSource  
**-ruleProject**     : the name of the rule project to rename   
**-to**              : the new name of the rule project   
