package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/hello", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintln(w, "This is the epreuves microservice")
	})

	http.ListenAndServe(":8080", nil)
}
