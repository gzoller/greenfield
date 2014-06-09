#!/bin/bash
echo `top -pid 30563 -l2 | grep 30563 | tail -n 1 | awk -F' ' '{print $3}'` >> cpu.stat && ./trimLast.sh cpu.stat > tmp.file && mv tmp.file cpu.stat
