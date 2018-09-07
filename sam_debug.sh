#!/bin/sh

method=POST
echo $#
if [ $# -eq 0  ]; then 
 printf "%s\n%s\n%s\n" "Argument 1: file with the request body " "Argument 2: Request method:(GET POST etc)" \
 "Argument 3: Resource name from template.yaml of the API lambda function to be invoked" \
 "Argument 4 (Optional):Debug port"  
else 
  if [ "$#" -eq 2 ]; then
    method=$2
  fi
    cat  $1 |  sed -e "s/^/'/g" | sed -e "s/$/'/g" | xargs sam local generate-event api -m $method -b

  if [ $# -eq 4 ]; then
    cat  $1 |  sed -e "s/^/'/g" | sed -e "s/$/'/g" |xargs  sam local generate-event api -m $method -b| sam  local invoke --debug-port $4 $3
  else
    cat  $1 |  sed -e "s/^/'/g" | sed -e "s/$/'/g" |xargs sam local generate-event api -m $method -b | sam  local invoke  $3
  fi
fi
