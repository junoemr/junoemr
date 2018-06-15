// Webpack plugin to generate the templates.jsp file for juno.  It is a plugin because it needs
// to know the output directory and needs to be run whenever webpack runs.

const path = require('path');
const globby = require('globby');
const fs = require('fs');

const outputFilename = "templates.jsp";

var options = {};

function HelloWorldPlugin(userOptions) {
	// Setup the plugin instance with options...
	options = userOptions;
}

HelloWorldPlugin.prototype.apply = function(compiler) {
/*	compiler.plugin('done', function() { */

	compiler.hooks.afterEmit.tapAsync('create-templates', function(compiler, callback)
	{
		globby(options.from, {}).then(function(paths)
		{
			var outStringArray  = [];
			for(var i = 0; i < paths.length; i++)
			{
				var filename = paths[i];

				var templateString = '<script type="text/ng-template" id="' +
					filename +
					'">\n\t<jsp:include page="' +
					filename +
					'"/>\n</script>';

				outStringArray.push(templateString);
			}

			var outputPath = path.resolve(compiler.options.output.path + '/' + outputFilename);
			fs.writeFile(outputPath, outStringArray.join("\n"), function(err)
			{
				if(err)
				{
					return console.log(err);
				}

				console.log('');
				console.log('== create_templates =================================================');
				console.log('Created ' + outputPath);
			});
		});

		callback();
	});

};

module.exports = HelloWorldPlugin;