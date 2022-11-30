#!/bin/bash

CONFIG_FILE=./setup.config

if [[ -f "$CONFIG_FILE" ]]; then
  source "$CONFIG_FILE"
fi

if [[ -z "$SESSION" ]]; then
  echo "SESSION must be defined in order to be authenticated"
  exit 1
fi

if ! [[ "$1" =~ ^[1-9][0-9]?$ ]]
then
  echo "Supply a valid day (1-25) as the first argument!"
  exit 1
fi

if [[ -z "${YEAR}" ]]; then
  YEAR=$(date +"%Y")
fi

if [[ -z "$INPUT_FILE_PATH" ]]; then
  INPUT_FILE_PATH=.
fi

DAY=$1
PADDED_DAY=$(printf %02d $DAY)
INPUT_FILE="$INPUT_FILE_PATH/day$PADDED_DAY.txt"

curl "https://adventofcode.com/$YEAR/day/$DAY/input" \
      --fail-with-body \
      --silent \
      --show-error \
      --cookie "session=$SESSION" \
      -o "$INPUT_FILE"