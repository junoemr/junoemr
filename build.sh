JOB_NAME="Juno"
BUILD_NUMBER="`date +\%s`"
export JOB_NAME BUILD_NUMBER
CATALINA_HOME=/usr/java/apache-tomcat
rm -rf target
mvn clean process-resources -DskipITs -DskipTests -Dmaven.test.skip=true verify $1
