#!/bin/sh
PATH="../../../target/node/":$PATH
node "../../../target/node/node_modules/npm/bin/npm-cli.js" "$@"
