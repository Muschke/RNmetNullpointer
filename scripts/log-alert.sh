#!/bin/bash

# Monitor the HI10 RealNutrition backend
#
# add the following to crontab:
# 58 23 * * * /home/ec2-user/Backend/log-alert.sh
#

last_occurence() {
        LAST_MATCH=$(grep "$1" $LOG | tail -1)
        DATE_MATCH=$(date -d "${LAST_MATCH:0:23}" +%s)
        DATE_CURRENT=$(date +%s)
        LAST=$(($DATE_CURRENT - $DATE_MATCH))
}

LOG=/home/ec2-user/Backend/logs/realnutrition.log
LAST=0
LEAST=0
ISSUES=false
TOLERANCE=3600

#use \| between search items you want results for, eg: "WARN\|ERROR"
SEARCH_FOR="ERROR"

last_occurence "RetryProcedure started"
STARTED=$LAST
last_occurence "Previous retryProcedure ran without errors"
SKIPPED=$LAST

if [[ "$STARTED" -lt "$SKIPPED" ]]; then
	LEAST=$STARTED
else
	LEAST=$SKIPPED
fi

if [[ "$TOLERANCE" -lt "$LEAST" ]]; then
	ISSUES=true
fi

#prev_count=0
COUNT=$(grep "`date --date='today' '+%Y-%m-%d'`"  $LOG | grep "$SEARCH_FOR" | wc -l)

if [[ "0" -lt "$COUNT"  ]]; then
	ISSUES=true
fi

echo "started: $STARTED"
echo "skipped: $SKIPPED"
echo "least: $LEAST"
echo "count: $COUNT"
echo "issues: $ISSUES"

if [[ "$ISSUES" = true ]]; then

	# This is a temp file, which is created to store the email message.
	MESSAGE="/home/ec2-user/tmp/logs.txt"

        rm $MESSAGE

	TO="rnmonitoring@hi10.be"

        echo "To: rnmonitoring@hi10.be" >> $MESSAGE
	echo "From: HI10Realnutrition@gmail.com" >> $MESSAGE 
	echo "Subject: ERRORS in realnutrition" >> $MESSAGE 
        echo "" >> $MESSAGE

	echo "ATTENTION: Errors are found in $LOG . Please Check with Linux admin." >> $MESSAGE

	echo  "Hostname: `hostname`" >> $MESSAGE

	echo -e "\n" >> $MESSAGE

	echo "+------------------------------------------------------------------------------------+" >> $MESSAGE

	echo "Error messages in the log file as below" >> $MESSAGE

	echo "+------------------------------------------------------------------------------------+" >> $MESSAGE

	grep "`date --date='today' '+%Y-%m-%d'`" $LOG | grep "$SEARCH_FOR" >>  $MESSAGE

	ssmtp -v "$TO" < $MESSAGE

fi
