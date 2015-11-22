#!/bin/bash

GOARCH=arm go build ../go/ade.go
mv ade ./app/src/main/assets/ade.arm
