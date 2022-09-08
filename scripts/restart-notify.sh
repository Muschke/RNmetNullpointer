#!/bin/bash
echo "Starting up"
echo "Sending email"
MESSAGE="/home/ec2-user/tmp/startup.txt"
rm $MESSAGE
echo "To: rnmonitoring@hi10.be" >> $MESSAGE
echo "From: HI10Realnutrition@gmail.com" >> $MESSAGE
echo "Subject: Real Nutrition App (re)started" >> $MESSAGE
echo "" >> $MESSAGE
echo "You have received this email because the Real Nutrition app has been (re)started." >> $MESSAGE
echo "If this is not part of a scheduled maintainance or upgrade, please contact your admin." >> $MESSAGE
echo "" >> $MESSAGE
echo "Details:" >> $MESSAGE
echo "Hostname: `hostname`" >> $MESSAGE
echo "Time: $(date)" >> $MESSAGE

ssmtp -v "rnmonitoring@hi10.be" < $MESSAGE
