## Took this file from visionmedia/debug.

# Get Mkefile directory name: http://stackoverflow.com/a/5982798/376773
THIS_MAKEFILE_PATH:=$(word $(words $(MAKEFILE_LIST)),$(MAKEFILE_LIST))
THIS_DIR:=$(shell cd $(dir $(THIS_MAKEFILE_PATH));pwd)

# BIN directory
BIN := $(THIS_DIR)/node_modules/.bin

# applications
NODE ?= $(shell which node)
NPM ?= $(NODE) $(shell which npm)
BROWSERIFY ?= $(NODE) $(BIN)/browserify

all: dist/loglevel-debug.js

install: node_modules

clean:
	@rm -rf dist

dist:
	@mkdir -p $@

dist/loglevel-debug.js: node_modules index.js dist
	@$(BROWSERIFY) \
		--standalone loglevelDebug \
		. > $@

distclean: clean
	@rm -rf node_modules

node_modules: package.json
	@NODE_ENV= $(NPM) install
	@touch node_modules

.PHONY: all install clean distclean
