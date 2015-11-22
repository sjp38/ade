package main

import (
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"os/exec"
	"path"
	"strings"
	"syscall"
)

const (
	USAGE = "Usage: ade <command> [argument]"
	workDir = "ade_work"
	TMP_DIR = "tmp"
	TMP_SRC = "tmp.go"
)

var (
	ade_dir = "def_ade_dir"
	ade_src_dir = "def_ade_src_dir"
	ade_tmp_dir = "def_ade_tmp_dir"
	ade_tmp_src = "def_ade_tmp_src"

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
	if len(args) < 1 {
		adel.Printf("no argument to save\n")
		return
	}
	save_path := path.Join(ade_src_dir, args[0])
	err := os.Rename(ade_tmp_src, save_path)
	if err != nil {
		adel.Printf("error while rename: %s\n", err)
		return
	}
}

func remove(args []string) {
	if len(args) < 1 {
		adel.Printf("no argument to remove\n")
		return
	}
	remove_path := path.Join(ade_src_dir, args[0])
	err := os.Remove(remove_path)
	if err != nil {
		adel.Printf("error while remove: %s\n", err)
		return
	}
}

func run(args []string) {
	shbin, err := exec.LookPath("sh")
	if err != nil {
		panic(err)
	}
	//goroot := "/home/sjpark/go"	for test on host
	goroot := "/data/local/tmp/goroot"
	//goroot := path.Join(ade_dir, "goroot")	for official version
	gobin := path.Join(goroot, "bin/go")
	tmpdir := ade_tmp_dir
	cmd := fmt.Sprintf("sh -c \"GOROOT=%s TMPDIR=%s %s run %s\"",
			goroot, tmpdir, gobin, ade_tmp_src)
	cmd_slice := []string{"sh", "-c", cmd}
	env := os.Environ()

	err = syscall.Exec(shbin, cmd_slice, env)
	if err != nil {
		adel.Printf("failed to exec %s %s\n", shbin, cmd)
		panic(err)
	}
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
	case "remove":
		remove(args)
	case "run":
		run(args)
	}
}

func init() {
	file := os.Args[0]
	ade_dir = path.Dir(file)
	ade_src_dir = path.Join(ade_dir, "src")
	ade_tmp_dir = path.Join(ade_dir, TMP_DIR)
	ade_tmp_src = path.Join(ade_tmp_dir, TMP_SRC)
	adel.Printf("file: %s, src dir: %s\ntmp dir: %s, tmp src: %s\n",
			file, ade_src_dir, ade_tmp_dir, ade_tmp_src)
}
