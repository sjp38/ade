package main

import (
	"fmt"
	"io/ioutil"
	"log"
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
	adel = log.New(os.Stderr, "", log.Ltime | log.Lshortfile)
)

func list(args []string) {
	adel.Printf("list called with... %s\n", args)
	adel.Printf("list go programs in dir %s\n", ade_src_dir)
	files, err := ioutil.ReadDir(ade_src_dir)
	if err != nil {
		adel.Printf("error while read dir %s: %s\n",
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
	adel.Printf("load called with... %s\n", args)
	if len(args) < 1 {
		adel.Printf("no argument for load...\n")
		adel.Printf("%s\n", USAGE)
	}
	file_path := path.Join(ade_src_dir, args[0])
	src, err := ioutil.ReadFile(file_path)
	if err != nil {
		adel.Printf("failed to read file: %s\n", file_path, err)
		return
	}

	fmt.Printf("%s", src)
}

func save(args []string) {
	adel.Printf("save called with... %s\n", args)
}

func main() {
	adel.Printf("ade is a development environment\n")
	args := os.Args
	if len(args) < 2 {
		adel.Println("no argument. do nothing")
		adel.Println("USAGE: ", USAGE)
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
	adel.Printf("file: %s, src dir: %s", file, ade_src_dir)
}
