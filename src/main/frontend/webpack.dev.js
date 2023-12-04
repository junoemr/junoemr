const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
	mode: 'development',
	devtool: 'inline-source-map',
	module: {
		rules: [
			{
				// CSS loader
				test: /\.css$/,
				use: [
					'style-loader',
					'css-loader?sourceMap'
				]
			},
			{
				// CSS loader
				test: /\.scss$/,
				use: [
					'style-loader',
					'css-loader?sourceMap',
					'sass-loader?sourceMap'
				]
			}
		]
	}
});