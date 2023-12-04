const webpack = require('webpack');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

module.exports = merge(common, {
	mode: 'production',
	devtool: 'source-map',
	optimization: {
		minimizer: [

			// Minify the js
			new UglifyJSPlugin({
				parallel: true,
				sourceMap: true
			}),

			new OptimizeCSSAssetsPlugin({})
		]
	},
	plugins: [
		// Write css to it's own file
		new MiniCssExtractPlugin({
			filename: "[name].[chunkhash].css"
		}),

		// Define the environment so plugins can optimize themselves
		new webpack.DefinePlugin({
			'process.env.NODE_ENV': JSON.stringify('production')
		})
	],
	module: {
		rules: [
			{
				// CSS loader
				test: /\.css$/,
				use: [
					MiniCssExtractPlugin.loader,
					'css-loader'
				]
			},
			{
				// CSS loader
				test: /\.scss$/,
				use: [
					MiniCssExtractPlugin.loader,
					'css-loader',
					'sass-loader'
				]
			}
		]
	}
});
