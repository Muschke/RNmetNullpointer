#!/bin/bash

#let's define where the log file is
LOG=/home/ec2-user/Backend/logs/realnutrition.log

#create an empty array, we'll populate it briefly and use in calculate_duration()
LINES=()
#bash return is funky, we'll be using these global variables instead
RESULT=0
MIN=0
MAX=0
LAST=0

#parse log file into array of strings
while IFS='' read -r LINE || [ -n "$LINE" ]; do
	LINES+=("$LINE")
done < $LOG

#this function expects two strings as input parameters
#it will find all lines of which the first string is a substring of
#and for each match will find the first line of which the second string is a substring of
#the date and time portion of these string pairs will be parsed
#the average/min/max of the difference in seconds is calculated and finally assigned to RESULT/MIN/MAX
calculate_duration() {
	DURATIONS=()
	FIRST=true
	for i in "${!LINES[@]}"
	do
        	if [[ ${LINES[i]} == *"$1"* ]]; then
                	for j in "${!LINES[@]}"
                	do
                        	if [[ ${LINES[(i+j)]} == *"$2"* ]]; then
                                	START_DATE=$(date -d "${LINES[i]:0:23}" +%s)
                                	END_DATE=$(date -d "${LINES[i+j]:0:23}" +%s)
                                	DURATION=$(($END_DATE - $START_DATE))
                                	DURATIONS+=("$DURATION")
					if [[ "$FIRST" = true ]]; then
						MIN="$DURATION"
						MAX="$DURATION"
						FIRST=false
					else
						if [[ "$DURATION" -lt "$MIN" ]]; then
							MIN="$DURATION"
						elif [[ "$DURATION" -gt "$MAX" ]]; then
							MAX="$DURATION"
						fi
					fi
                                	break
                        	fi
                	done
        	fi
	done

	SUM=0
	for i in "${DURATIONS[@]}"
	do
        	SUM=$(($SUM + $i))
	done

	RESULT=$(($SUM / ${#DURATIONS[@]}))
}

#this function expects one string as an input parameter
#it will find the last line of which the string is a substring of
#the date and time portion of this string will be parsed and compared to the current time
#and finally assigned to LAST
last_occurence() {
	LAST_MATCH=$(grep "$1" $LOG | tail -1)
	DATE_MATCH=$(date -d "${LAST_MATCH:0:23}" +%s)
	DATE_CURRENT=$(date +%s)
	LAST=$(($DATE_CURRENT - $DATE_MATCH))
}

#basic statistics
echo "Start Analytics"
echo "---------------"
webhooks_received=$(grep -c "webHook received" $LOG)
echo "WebHooks:"
echo "- Received: $webhooks_received"
webhooks_handled=$(grep -c "webHook handled" $LOG)
echo "- Handled: $webhooks_handled"
last_occurence "webHook received"
echo "- Last seen: $LAST seconds ago"
echo ""
calculate_duration "RetryProcedure started" "RetryProcedure finished"
echo "- Average: $RESULT seconds"
echo "- Minimum: $MIN seconds"
echo "- Maximum: $MAX seconds"
echo ""
echo ""
retry_procedures_skipped=$(grep -c "Previous retryProcedure ran without errors" $LOG)
echo "Retry Procedures:"
echo "- Skipped: $retry_procedures_skipped"
retry_procedures_started=$(grep -c "RetryProcedure started" $LOG)
echo "- Started: $retry_procedures_started"
retry_procedures_finished=$(grep -c "RetryProcedure finished" $LOG)
echo "- Finished: $retry_procedures_finished"
last_occurence "RetryProcedure started"
echo "- Last seen: $LAST seconds ago"
echo ""
calculate_duration "webHook received" "webHook handeled"
echo "- Average: $RESULT seconds"
echo "- Minimum: $MIN seconds"
echo "- Maximum: $MAX seconds"
echo ""
echo ""
access_tokens_requested=$(grep -c "Stored Access Token is expired" $LOG)
echo "Access Tokens:"
echo "- Requested: $access_tokens_requested"
last_occurence "Stored Access Token is expired"
echo "- Last seen: $LAST seconds ago"
echo ""
echo ""
RESPONSE=$(cd /tmp && sudo -u postgres -H -- psql -d stock_updater -c "SELECT COUNT(id)  FROM errors" | head -3 | tail -1 | xargs)
echo "Database:"
echo "- Rows in errors: $RESPONSE"
echo ""
echo ""
echo "End Analytics"
echo "-------------"
