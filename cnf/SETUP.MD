# Initial Setup

## Basics

The Maven group-id is defaulted to **org.gecko.vaadin** in *cnf/build.bnd* using the bnd **-groupid** instruction.

## Github - Create Project
- Create a empty [GitHub repository](https://github.com/organizations/geckoprojects-org/repositories/new)
  - Name: `org.gecko.vaadin`
  - DO NOT!!! use any DEFAULTS like README or license
  
## Sonarcloud
  - Add Project
  - do the [Sonar Configuration](https://sonarcloud.io/organizations/geckoprojects-org/projects)
  - press `+`_(upper right)_
  - Analyze new project
  - check `org.gecko.vaadin` and `SetUp`
  - Select `with a GitHub Action`
  - Copy the `SONAR_TOKEN` to notepad
  - NOT!!!! follow the description .about `github/workflows/build.yml`. _this still [exists](../blob/main/.github/workflows/sonar.yml) in this template._
  - GitHub -> Project-Settings -> Secrets -> [New Repository Secrets](Link: https://github.com/geckoprojects-org/org.gecko.vaadin/settings/secrets/actions/new)
    - add `SONAR_TOKEN`

## Gradle init

On Linux - make *gradlew* executable:
```
chmod +x gradlew
./gradlew --version
```

## Git init and push
- execute this in your local folder

```
git init
git add .
git commit -m "initial commit"
git branch -M main
git remote add origin git@github.com:geckoprojects-org/org.gecko.vaadin.git
git push -u origin main
git checkout -b snapshot
git push --set-upstream origin snapshot
git checkout -b "initial"
```

## GitHub Branch-Protection
- Project -> Settings  -> Branches [add rule](https://github.com/geckoprojects-org/org.gecko.vaadin/settings/branch_protection_rules/new)
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
- [Jenkins GitHub-Group](https://devel.data-in-motion.biz/jenkins/job/GH/) should show `org.gecko.vaadin`
- [Jenkins org.gecko.vaadin branches](https://devel.data-in-motion.biz/jenkins/job/GH/job/org.gecko.vaadin/)

