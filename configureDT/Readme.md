# Configure decision tables properties in Decision Center

This tool allows to set properties on all the Decision tables in a given decision service. Changes will be applied on all branches that can be modified. 

## Compiling

This solution is build using the dependencies from [this project](https://github.com/ODMDev/odm-libs-in-maven/blob/master/README.md) and an installation of ODM.

1. Clone the project from github  
1. Configure the properties in pom.xml to match your version and configuration
1. Run maven clean deploy


## Installing

- Add the jar [configure-dt-0.0.3.jar](https://github.com/ODMDev/decision-center-api-samples/raw/master/configureDT/configure-dt-0.0.3.jar) in teamserver/lib folder

## Usage

- run 
java -cp "<odm_home>/teamserver/lib/*:<odm_home>/teamserver/lib/eclipse_plugins/*:" com.ibm.odm.tools.ConfigureDT

  java com.ibm.odm.tools.ConfigureDT -username username -password password -url url -dataSource dataSource -decisionService <dsName> [-branch <branch name>] [-overlapCheck true|false] [-gapCheck true|false] [-manualOrdering true|false] [-autoResize true|false] [-unhideColumns true]  
	Update all decision tables properties according to option selected in the decision service selected. Update all branches by default, specify <branch name> to update in that branch.
	If an option is not selected the property is not modified. At least one option must be selected  

### Arguments

-username        : an administrator account   
-password        : the user password  
-url             : decision center URL   
-dataSource      : the JNDI name of the datasource usually jdbc/ilogDataSource  
-decisionService : the decision service name  
-branch          : the branch to configure (optional)
-overlapCheck    : set false to disable overlap check on all DT and true to enable it. If this option is not defined the property is not modified  
-gapCheck        : set false to disable gap check on all DT and true to enable it. If this option is not defined the property is not modified  
-manualOrdering  : set true to use manual ordering on all DT and false for Automatic. If this option is not defined the property is not modified  
-autoResize      : set false to disable auto resize on all DT and true to enable it. If this option is not defined the property is not modified  
-unhideColumns   : make all columns of decision tables visible (Can only be reverted in Rule Designer)

