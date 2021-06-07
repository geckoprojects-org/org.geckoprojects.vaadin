# initial setup

## Github - create Project
- Create a empty [GitHub repository](https://github.com/organizations/geckoprojects-org/repositories/new)
  - Name: `org.geckoprojects.vaadin`
  - DO NOT!!! use any DEFAULTS like README or license
  
## Sonarcloud - add Project
- The add the project, you need to be ADMIN in sonarcloud.io!!!
- do the [Sonar Configuration](https://sonarcloud.io/organizations/geckoprojects-org/projects)
  - press `+`_(upper right)_
  - Analyze new project
  - check `org.geckoprojects.vaadin` and `SetUp`
  - Select `with a GitHub Action`
  - Copy the `SONAR_TOKEN` to notepad
  - NOT!!!! follow the description .about `github/workflows/build.yml`. _this still [exists](../blob/main/.github/workflows/sonar.yml) in this template._
  - GitHub -> Project-Settings -> Secrets -> [New Repository Secrets](Link: https://github.com/geckoprojects-org/org.geckoprojects.vaadin/settings/secrets/actions/new)
    - add `SONAR_TOKEN`

## Gradle init

on linux Linux - make execubale 
```
chmod +x gradlew
./gradlew --version
```

Info: 
when runnng `./gradlew tasks` on this empty workspace you will get
```
* What went wrong:
Task 'testOSGi' not found in root project 'org.geckoprojects.vaadin'.
```
This is okay!


## Git init and push
- execute this in your local folder

```
git init
git add .
git commit -m "initial commit"
git branch -M main
git remote add origin git@github.com:geckoprojects-org/org.geckoprojects.vaadin.git
git push -u origin main
git checkout -b snapshot
git push --set-upstream origin snapshot
git checkout -b "initial"
```

## GitHub Branch-Protection
- Project -> Settings  -> Branches [add rule](https://github.com/geckoprojects-org/org.geckoprojects.vaadin/settings/branch_protection_rules/new)
  - Branch name pattern: `[ms][an][ia][np]*` *(ugly but works, protects 'main*' and 'snap*' by charachter )*
  - Require pull request reviews before merging
    - 1 reviewer
    - Dismiss stale pull request approvals when new commits are pushed
    - Require status checks to pass before merging
      - Require branches to be up to date before merging
    - Require linear history
    - Include administrators
    - NOT!!! Allow force pushes
    - NOT!!! Allow deletion

## Jenkins
- wait up to 15 minutes for the auto-import
- [Jenkins Mainpage](https://devel.data-in-motion.biz/jenkins/)
- [Jenkins GitHub-Group](https://devel.data-in-motion.biz/jenkins/job/GH/) should show `org.geckoprojects.vaadin`
- [Jenkins org.geckoprojects.vaadin branches](https://devel.data-in-motion.biz/jenkins/job/GH/job/org.geckoprojects.vaadin/)

