# AngularJS 1.x custom rules for SonarQube

## Install

- Build project with `mvn clean install`
- Copy the generated JAR file in `SONARQUBE_HOME/extensions/plugins/`
- Restart SonarQube

## Configure

- Create a new Quality Profile which inherits from SonarWay Javascript
- Activate the custom rules for the new Quality Profile (You can find them in the repository `Custom Repository`)

## Run

- You can use [gulp-sonar](https://www.npmjs.com/package/gulp-sonar) to generate SonarQube metrics for your AngularJS Project

## Rules

- [List of implemented rules](RULES.md)
