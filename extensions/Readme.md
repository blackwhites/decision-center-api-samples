# Business Console Extensions

## Extending

1. Add your custom controller in a package com.ibm.rules.decisioncenter.web.myController  
1. Add resources for this controller in a folder resources/extensions   
1. Add you entry point in the preferences.properties in resources/com/ibm/rules/decisioncenter/web  
1. Rebuild

This solution is build using the dependencies from [this project](https://github.com/ODMDev/odm-libs-in-maven/blob/master/README.md) and an installation of ODM.

## Installing

- Download the [release](/releases/tag/1.0.0)
- Add the jar BCExtensions into DecisionCenter.war in WEB-INF/lib
- Redeploy the application

## Using

### ClipboardActions

Add the ability to select rules from the Decision Artifact middle view and copy them to any project or branch selecting folder(s) in the Decision Artifact tree view. 

* Copy to clipboard ![](src/resources/extensions/images/ruletoclipboard.png)  

Check rules in the middle view to be copied.     
The clipboard is reset by using Copy To Clipboard with nothing selected.

* Paste to Package ![](src/resources/extensions/images/clipboardtopack.png)

Select folders and project in the tree view where to copy the rules stored in the clipboard.
