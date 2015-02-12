#!/bin/sh


#Usage:
#./send_to_jira.sh = builds oscar_12_2_2013-11-11.war
#./send_to_jira.sh -i 1 = builds oscar_12_2_2013-11-11.1.war
#./send_to_jira.sh -i 2 = builds oscar_12_2_2013-11-11.2.war
#./send_to_jira.sh -v oscar_12_3 = builds oscar_12_3_2013-11-11.war
#./send_to_jira.sh -i 1 -v oscar_12_3 = builds oscar_12_3_2013-11-11.1.war

# A POSIX variable
OPTIND=1 # Reset in case getopts has been used previously in the shell.

# Initialize our own variables

ITERATION=
DATE=`date +%Y-%m-%d` 
OSCAR_VERSION=oscar_12_2
DELIMITER=_

while getopts "h?i:v:" opt; do
	case "$opt" in
		h|\?)
			echo "Usage: send_to_jira.sh -i <iteration/optional> -v <version/default: oscar_12_2>"
			exit 0
			;;
		i) ITERATION=".$OPTARG"
			;;
		v) OSCAR_VERSION=$OPTARG
			;;
	esac
done


SNAPSHOT="oscar-SNAPSHOT.war"

if [ $OSCAR_VERSION = "oscar_12_3" ]; then
	SNAPSHOT="oscar-12.1.1-SNAPSHOT.war"
fi

scp -P 20202 target/$SNAPSHOT jira.oh.ca:/var/storage/oscar_versions/$OSCAR_VERSION$DELIMITER$DATE$ITERATION.war
