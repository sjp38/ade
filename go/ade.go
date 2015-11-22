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

var (
	ade_dir = "def_ade_dir"
	ade_src_dir = "def_ade_src_dir"
)

func list(args []string) {
	fmt.Println("list called with... ", args)
	fmt.Printf("list go programs in dir %s\n", ade_src_dir)
	files, err := ioutil.ReadDir(ade_src_dir)
	if err != nil {
		fmt.Printf("error while read dir %s: %s\n",
			ade_src_dir, err)
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
	if len(args) < 1 {
		fmt.Printf("no argument for load...\n")
		fmt.Printf("%s\n", USAGE)
	}
	file_path := path.Join(ade_src_dir, args[0])
	src, err := ioutil.ReadFile(file_path)
	if err != nil {
		fmt.Printf("failed to read file: %s\n", file_path, err)
		return
	}

	fmt.Printf("%s", src)
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

func init() {
	_, file, _, _ := runtime.Caller(1)
	ade_dir = path.Dir(file)
	ade_src_dir = path.Join(ade_dir, "src")
	fmt.Printf("file: %s, src dir: %s", file, ade_src_dir)
}
