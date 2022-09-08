#!/bin/bash

#A log file is stored daily.
#To save storage space only 1 log file per month will be kept and stored in a different folder
#In the logs folder there will be 30 daily logs

SRC_DIR="/home/ec2-user/Backend/logs"
DEST_DIR="/home/ec2-user/Backend/logsMonthlyBackup"

#get day
function getDay {
local date=${1}

local day=${date#*-}
echo ${day#*-}
}

#get month
function getMonth {
local date=${1}
local month=${date#*-}
month=${month%-*}
echo $month
}

#get Year
function getYear {
local date=${1}
echo  ${date%%-*}
}


#check if year folder exists else create
function createYearDir {
local dir=$DEST_DIR/${1}
mkdir -p $dir
echo $dir
}


#get date from files
function getDateFromFile {
local str=${1}
str=${str:46:10}
echo $str
}

#copy all files to new destination where day is first of the month
function moveFirstOfMonthToDestDir {
for FILE in $SRC_DIR/* ;
do
	echo "$FILE"
	date=$(getDateFromFile ${FILE});
	echo "$date"
	day=$(getDay $date)
	echo "$day"
	if [[ "$day" == "01" ]];
	then
		year=$(getYear $date)
		createYearDir $year
		cp -p ${FILE} $DEST_DIR/$year
	fi
done
}

function deleteOldFiles {
find $SRC_DIR -type f -mtime +30 -delete
}

moveFirstOfMonthToDestDir
deleteOldFiles
