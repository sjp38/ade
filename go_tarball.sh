#!/bin/bash

mkdir sandbox
pushd sandbox

git clone https://go.googlesource.com/go gorepo
pushd gorepo
git checkout go1.6
popd
cp -R gorepo goroot

pushd gorepo/src
GOARCH=arm ./make.bash
popd

pushd goroot
rm -fr .git
popd

mkdir goroot/bin
cp gorepo/bin/linux_arm/go goroot/bin/
cp gorepo/bin/linux_arm/gofmt goroot/bin/

mkdir -p goroot/pkg/linux_arm
cp -R gorepo/pkg/linux_arm/* goroot/pkg/linux_arm/
mkdir -p goroot/pkg/tool/linux_arm
cp -R gorepo/pkg/tool/linux_arm/* goroot/pkg/tool/linux_arm/

cp gorepo/src/runtime/zversion.go goroot/src/runtime/
cp gorepo/src/runtime/zdefaultcc.go goroot/src/runtime/
cp gorepo/src/cmd/cgo/zdefaultcc.go goroot/src/cmd/cgo/
cp gorepo/src/cmd/internal/obj/zbootstrap.go goroot/src/cmd/internal/obj/
cp gorepo/src/runtime/internal/sys/zversion.go goroot/src/runtime/internal/sys/

tar cvf goroot.tar ./goroot
