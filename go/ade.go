package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"path"
	"runtime"
	"strings"
)

const (
	USAGE = "Usage: ade <command> [argument]"
	workDir = "ade_work"
)

func list(args []string) {
	fmt.Println("list called with... ", args)
	_, file, _, _ := runtime.Caller(1)
	fmt.Println("file is: ", file)
	ade_dir := path.Dir(file)
	ade_files_dir := path.Join(ade_dir, "src")
	fmt.Printf("list go programs in dir %s\n", ade_files_dir)
	files, err := ioutil.ReadDir(ade_files_dir)
	if err != nil {
		fmt.Printf("error while read dir %s: %s\n",
			ade_files_dir, err)
	}
	for _, f := range files {
		if f.IsDir() {
			continue
		}
		if strings.HasSuffix(f.Name(), ".go") {
			fmt.Println(f.Name())
		}
	}
}

func load(args []string) {
	fmt.Println("load called with... ", args)
}

func save(args []string) {
	fmt.Println("save called with...\n", args)
}

func main() {
	fmt.Println("ade is a development environment")
	args := os.Args
	if len(args) < 2 {
		fmt.Println("no argument. do nothing")
		fmt.Println("USAGE: ", USAGE)
		return
	}
	cmd := args[1]
	args = args[2:]
	switch cmd {
	case "list":
		list(args)
	case "load":
		load(args)
	case "save":
		save(args)
	}
}
