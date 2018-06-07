#!/bin/sh
PATH="../../../../target/node/":$PATH
node "node_modules/gulp/bin/gulp.js" "$@"
