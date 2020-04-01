# Crazy Enchantments
Source Code for Crazy Enchantments

Build Status: [![Build Status](https://jenkins.badbones69.com/job/Crazy-Enchantments/badge/icon)](https://jenkins.badbones69.com/job/Crazy-Enchantments/)


## Jenkins: 
 https://jenkins.badbones69.com/job/Crazy-Enchantments/

## Compiling:
Due to supporting premium plugins a simple `mvn install` will not work. Since there are plugins that I can not share because of them costing money you will need to do one of these options.

1. Get a hold of the jars and add them to your repo.
2. Remove the code that is used for these premium plugins.

## Removing hooks:
Each hook is in its own class and is only hooked into from the [Support](https://github.com/badbones69/Crazy-Enchantments/blob/v1.8/plugin/src/main/java/me/badbones69/crazyenchantments/multisupport/Support.java) class.
To remove the hooks delete the class and then remove the code used in the Support class.
