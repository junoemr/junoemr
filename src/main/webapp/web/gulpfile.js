var sass = require('gulp-sass'),
		cssmin = require('gulp-clean-css'),
    rename = require('gulp-rename'),
    concatFilenames = require('gulp-concat-filenames'),
    gulp = require('gulp');


var paths = {
    scss: 'scss/juno.scss',
    jsp: ['./**/*.jsp', '!./index.jsp', '!./dist/**'],
    dest: './dist/'
};

gulp.task('sass', function()
{
	// compiles juno.scss to juno.css
	gulp.src(paths.scss)
		.pipe(sass({
			sourceComments: 'map',
			sourceMap: 'sass',
			outputStyle: 'nested'
		}).on('error', sass.logError))
		.pipe(gulp.dest(paths.dest));
	// .pipe(cssmin({noRebase: true}))
	// .pipe(rename({suffix: '.min'}))
	// .pipe(gulp.dest(paths.dest));
});

function ngTemplateFormatter(filename)
{
	return '<script type="text/ng-template" id="' +
		filename +
		'">\n\t<jsp:include page="../' +
		filename +
		'"/>\n</script>';
}

gulp.task('templates', function()
{
	gulp.src(paths.jsp)
  	.pipe(concatFilenames(
  		'templates.jsp', {
  			root: '.',
  			template: ngTemplateFormatter
  		}))
  	.pipe(gulp.dest(paths.dest));
});

gulp.task('default', ['sass', 'templates'], function() {});
