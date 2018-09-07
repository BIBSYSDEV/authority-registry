#!/bin/sh

method=POST
echo $#
if [ $# -eq 0  ]; then 
 printf "%s\n%s\n%s\n" "Argument 1: file with the request body " "Argument 2: Request method:(GET POST etc)" "Argument 3: Resource name from template.yaml of the API lambda function to be invoked" 
else 
  if [ "$#" -eq 2 ]; then
    method=$2
  fi

  cat  $1 |  sed -e "s/^/'/g" | sed -e "s/$/'/g" |sam local generate-event api -m $method | sam  local invoke $3
fi
