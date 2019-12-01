#!/bin/bash

if [ "$1" != "" ]; then
	DAY=$1
	echo "Making files for Day $DAY"
	if [ ! -e "Day_$DAY" ]; then
		echo "Copying files into Day_$DAY"
		cp -r ./Day_Template/ ./Day_$DAY
	else
		echo "Day_$DAY already exists..."
		exit 0
	fi
	cd Day_$DAY
	mv Day_Num.java Day_$DAY.java
	sed -i -e "s/Num/$DAY/g" Day_$DAY.java
	echo "Finished making files"
else
	echo "Please type in a day. e.g. ./make_day.sh 02"
fi
