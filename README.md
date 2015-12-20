# AngularJS custom rules for SonarQube

## Install

- Build project with mvn clean install`
- Copy the generated JAR file in SONARQUBE_HOME/extensions/plugins/``
- Restart SonarQube

## Configure

- Create a new Quality Profile which inherits from Sonar Javascript
- Activate the custom rules in the rules menu tab for the new Quality Profile

## Run

- You can use [gulp-sonar](https://www.npmjs.com/package/gulp-sonar) to generate SonarQube metrics for your Angular Project

## Rules

- [List of implemented rules](RULES.md)