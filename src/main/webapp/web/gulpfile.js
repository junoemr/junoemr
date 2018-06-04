var sass = require('gulp-sass')
//var cssmin = require('gulp-clean-css');
//var rename = require('gulp-rename');
var concatFilenames = require('gulp-concat-filenames');
var gulp = require('gulp');
var ts = require('gulp-typescript');


var paths = {
    scss: 'scss/juno.scss',
    jsp: ['./src/**/*.jsp'],
	ts: ['./src/**/*.ts'],
    src: './src/',
	dest: './dist/'
};

// Compile scss
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

// Formatter for generating templates.
function ngTemplateFormatter(filename)
{
	return '<script type="text/ng-template" id="' +
		filename +
		'">\n\t<jsp:include page="../' +
		filename +
		'"/>\n</script>';
}

// Finds all jsp template files and generates a file that will run them all and output the html.
// This is done so that all of the template code can be loaded when the app is loaded.
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

// Compile typescript.  The .js files are created in the same place as the .ts files.
gulp.task('typescript', function()
{
	return gulp.src(paths.ts)
		.pipe(ts({
			noImplicitAny: true,
			target: 'es5',
			lib: ['es5'],
			//declaration: true // This will generate *.d.ts files
		}))
		.pipe(gulp.dest(paths.src));
});

gulp.task('default', ['sass', 'templates', 'typescript'], function() {});
