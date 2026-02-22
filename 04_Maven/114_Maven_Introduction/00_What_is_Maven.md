- Maven is like pip,npm, yarn
- its a project manager 

### Your project needs:

- logging library
- database connector
- testing framework
- JSON parser

Instead of downloading everything manually…
Maven does it automatically.

Maven = Dependency Manager + Build Tool for Java projects

### Problem Without Maven

Earlier Java developers had to:
    Download .jar files manually
    Put them into project folders
    Handle versions manually
    Fix dependency conflicts themselves

my-app/
 ├── src/
 │   ├── main/java/
 │   └── test/java/
 ├── pom.xml   ← MOST IMPORTANT FILE


### What Maven Actually Helps With
1. Dependency Management
    Like npm install.
2. Build Automation
    Compiles Java code: instead of compiling 1000 files
3. Testing
    Runs tests automatically:
        mvn test
4. Packaging App
    Creates runnable file:

5. Standard Project Structure
    - Every Java project looks similar → easier teamwork.