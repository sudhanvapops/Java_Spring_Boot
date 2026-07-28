### Typical Spring Boot Workflow


# JDK Java Image
download open jdk


# Make a Jar File
add in 
<finalName>hello-demo</finalName>
this is the name of the jar pacakge will be


# when ever youcreate a pacakge it will go to target folder
- mvn packcage

# Run jar
java -jar "relative path of that jar"



# Copy Jar to JDK Container 
cmd or in vscode itslef

docker cp from to
docker cp target/hello-demo.jar jdk:/tmp


# makes the cuurent container image
docker commit containername imagename:tag


and change the command in commit itself

all should be in lower case in imagename and containername
docker commit --change='CMD ["java","-jar","/tmp/hello-demo.jar"]' containername imagename:tag 