# Makefile
# Author: Nathan Tebbs
# Teacher & TAs: Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
# Assignment: Prog4
# Course: CSc 460
# Due: December 8th, 2025
# NOTE: This Makefile is intended to be run whilst connected to lectura.
#
# USAGE:
#
#   make run USER=[uname] PASS=[psswd]
#   make design

SRC_DIR := src
BIN_DIR := bin

JAVAC := javac
JAVA  := java
LATEX := pdflatex

MAIN := PetCafeApplication
SOURCES := $(wildcard $(SRC_DIR)/*.java)

CLASSPATH := $(BIN_DIR):/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar

USER ?=
PASS ?=

# ================
# LaTeX settings
# ================
LATEX_OUT_DIR := latex
TEX_SRC := design.tex
PDF_OUT := design.pdf
AUX_DIR := docs/latex

TEXFLAGS := -interaction=nonstopmode -halt-on-error \
            -output-directory=$(LATEX_OUT_DIR)

# ============================
# Default target
# ============================
all: build

# ============================
# Build Java
# ============================
build:
	mkdir -p $(BIN_DIR)
	$(JAVAC) -cp $(CLASSPATH) -d $(BIN_DIR) $(SOURCES)
	@echo "Build complete."

# ============================
# Run Java program
# ============================
run: build
	@if [ -z "$(USER)" ] || [ -z "$(PASS)" ]; then \
		echo "Usage: make run USER=your_username PASS=your_password"; \
		exit 1; \
	fi
	$(JAVA) -cp $(CLASSPATH) $(MAIN) $(USER) $(PASS)

# ============================
# Build design.pdf using LaTeX
# ============================
design: $(PDF_OUT)

$(PDF_OUT): docs/$(TEX_SRC) | $(AUX_DIR)
	@pushd docs > /dev/null ; \
	$(LATEX) $(TEXFLAGS) $(TEX_SRC) ; \
	$(LATEX) $(TEXFLAGS) $(TEX_SRC) ; \
	popd > /dev/null
	cp $(AUX_DIR)/design.pdf $(PDF_OUT)
	@echo "design.pdf built successfully."

$(AUX_DIR):
	mkdir -p $(AUX_DIR)

# ============================
# Clean
# ============================
clean:
	rm -rf $(BIN_DIR)
	@echo "Java build artifacts removed."

clean-docs:
	rm -rf $(AUX_DIR)
	@echo "LaTeX auxiliary files removed."

clean-all: clean clean-docs
	rm -f $(PDF_OUT)
	@echo "All build artifacts removed."

rebuild: clean build

.PHONY: all build run design clean clean-docs clean-all rebuild
