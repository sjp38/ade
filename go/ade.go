package main

import (
	"fmt"
	"os"
)

const (
	USAGE = "Usage: ade <command> [argument]"
	workDir = "ade_work"
)

func list(args []string) {
	fmt.Println("list called with... ", args)
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
