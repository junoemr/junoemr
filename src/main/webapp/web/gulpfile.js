var cssmin = require('gulp-clean-css'),
    sass = require('gulp-sass'),
    rename = require('gulp-rename'),
    gulp = require('gulp');

var paths = {
    scss: 'scss/juno.scss',
    dest: './dist/'
};

gulp.task('sass', function () {

	// compiles juno.scss to juno.css
	gulp.src(paths.scss)
		.pipe(sass({
			sourceComments: 'map',
		  sourceMap: 'sass',
		  outputStyle: 'nested' }).on('error', sass.logError))
		.pipe(gulp.dest(paths.dest));

	// minifies juno.css to juno.min.css
	gulp.src(paths.dest + 'juno.css')
		.pipe(cssmin({noRebase: true}))
		.pipe(rename({suffix: '.min'}))
		.pipe(gulp.dest(paths.dest));
});

gulp.task('default', [ 'sass' ], function () {});