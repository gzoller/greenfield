#!/bin/bash
while [[ true ]]
do
	echo `top -pid $1 -l2 | grep $1 | tail -n 1 | awk -F' ' '{print $3}'` > cpu.stat
	size=`cat cpu.stat | tr -d '\n' | wc -c`
	if [$size == '0' ]
	then
		rm cpu.stat
		exit
	fi
	sleep 150
done
