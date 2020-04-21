# Crazy Enchantments
Source Code for Crazy Enchantments

## Build Status:
[![Build Status](https://jenkins.badbones69.com/job/Crazy-Enchantments/badge/icon)](https://jenkins.badbones69.com/job/Crazy-Enchantments/)

## Compiling:
Due to supporting premium plugins a simple `mvn install` will not work. Since there are plugins that I can not share because of them costing money you will need to do one of these options.

1. Get a hold of the jars and add them to your repo.
2. Remove the code that is used for these premium plugins.

## Removing hooks:
Each hook is in its own class and is only hooked into from the [Support](https://github.com/badbones69/Crazy-Enchantments/blob/v1.8/plugin/src/main/java/me/badbones69/crazyenchantments/multisupport/Support.java) class.
To remove the hooks delete the class and then remove the code used in the Support class.

## Latest Version:
[![Latest Version](https://img.shields.io/badge/Latest%20Version-1.8--Dev--Build--v8-blue)](https://github.com/badbones69/Crazy-Enchantments/releases/latest)

## Jenkins: 
 https://jenkins.badbones69.com/job/Crazy-Enchantments/

## Nexus:
https://nexus.badbones69.com/#browse/browse:maven-releases:me%2Fbadbones69%2Fcrazyenchantments-plugin

## Maven:
```xml
<repository>
    <id>nexus</id>
    <url>https://nexus.badbones69.com/repository/maven-releases/</url>
</repository>

<dependency>
    <groupId>me.badbones69</groupId>
    <artifactId>crazyenchantments-plugin</artifactId>
    <version>{Latest Version}</version>
</dependency>
```
