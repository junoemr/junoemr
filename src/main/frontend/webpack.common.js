const path = require('path');
const webpack = require('webpack');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const CreateTemplates = require('./create_templates');

const basePath = __dirname;
const targetPath = '../webapp';
const targetFolder = 'web';


module.exports = {

	// Set type of source map to use.  See https://webpack.js.org/configuration/devtool/
	devtool: 'source-map',

	module: {
		rules: [
			{
				// Typescript loader
				test: /\.tsx?$/,
				use: 'ts-loader'
			},
			{
				// URL loader.  Falls back to file loader for large files.
				test: /\.(woff(2)?|ttf|eot|svg|gif|png)(\?v=\d+\.\d+\.\d+)?$/,
				loader: 'url-loader',
				options: {
					limit: 10000,
				}
			},
			{
				// html loader
				test: /\.html?$/,
				use: 'html-loader'
			},
		]
	},
	plugins: [
		// Clears out the release folder before filling it back up
		new CleanWebpackPlugin([targetFolder], {
			root: basePath + '/' + targetPath
		}),

		// Fixes warning in moment-with-locales.min.js
        //   Module not found: Error: Can't resolve './locale' in ...
		new webpack.IgnorePlugin(/\.\/locale$/),

		// Provide access to various libraries from legacy code.
		new webpack.ProvidePlugin({
			$: "jquery",
			jQuery: "jquery",
			"window.jQuery": "jquery",
			moment: "moment",
			datepicker: "bootstrap-datepicker",
			pym: "pym.js"
		}),

		// Create the HTML to include webpack-created files
		new HtmlWebpackPlugin({
			template: 'raw-loader!./index.jsp',
			filename: 'index.jsp'
		}),
		new HtmlWebpackPlugin({
			template: 'raw-loader!./faxConfiguration.jsp',
			filename: 'faxConfiguration.jsp'
		}),
		new HtmlWebpackPlugin({
			template: 'raw-loader!./faxSendReceive.jsp',
			filename: 'faxSendReceive.jsp'
		}),
		new HtmlWebpackPlugin({
			template: 'raw-loader!./Know2actConfiguration.jsp',
			filename: 'Know2actConfiguration.jsp'
		}),
		new HtmlWebpackPlugin({
			template: 'raw-loader!./Know2actTemplate.jsp',
			filename: 'Know2actTemplate.jsp'
		}),
		new HtmlWebpackPlugin({
			template: 'raw-loader!./Know2actNotifications.jsp',
			filename: 'Know2actNotifications.jsp'
		}),

		// Copy files that aren't modified by webpack
		new CopyWebpackPlugin([

			// Copy templates
			{from: 'src/**/*.jsp'}
		],{}),

		new CreateTemplates(
			{from: 'src/**/*.jsp'}
		)
	],
	optimization: {

		// Split the file into chunks as webpack sees fit
		runtimeChunk: true,
		splitChunks: {
			chunks: 'all'
		}
	},
	resolve: {
		extensions: ['.tsx', '.ts', '.js']
	},
	output: {
		path: path.resolve(basePath + '/' + targetPath, targetFolder),

		// Name files with the chunk hash, that way if a chunk changes, it won't force reload of
		// the other chunks.
		filename: '[name].[chunkhash].js',
		chunkFilename: '[name].[chunkhash].js'
	},
	entry: {
		bundle: [
			'../webapp/share/javascript/Oscar.js',
			'../webapp/share/javascript/HealthCardParser.js',
			'./src/common/displayMessages.js',
			'./src/common/util/util.js',
			'./src/common/util/serviceHelper.js',
			'./src/consults/common.js',
			'./app.js',
/*			'./node_modules/cp-calendar/module.js',
			'./node_modules/cp-calendar/directive.js',
			'./node_modules/cp-calendar/controller.js',
			'./node_modules/cp-calendar/event_controller.js',
			'./node_modules/cp-calendar/util.js',*/
			// TODO: move this back to node_modules after development
			/*
			'./cp-calendar/module.js',
			'./cp-calendar/directive.js',
			'./cp-calendar/controller.js',
			'./cp-calendar/event_controller.js',
			'./cp-calendar/util.js',
			*/
			'./src/common/module.js',

			'./src/common/util/module.js',
			'./src/common/util/angular-util.js',
			'./src/common/util/junoHttp.js',
			'./src/common/util/searchListHelper.js',

			'./src/common/services/module.js',
			'./src/common/services/appService.js',
			'./src/common/services/autoCompleteService.js',
			'./src/common/services/billingService.js',
			'./src/common/services/consultService.js',
			'./src/common/services/demographicService.js',
			'./src/common/services/demographicsService.js',
			'./src/common/services/diseaseRegistryService.js',
			'./src/common/services/errorsService.js',
			'./src/common/services/eFormService.js',
			'./src/common/services/faxAccountService.js',
			'./src/common/services/faxInboundService.js',
			'./src/common/services/faxOutboundService.js',
			'./src/common/services/fieldHelperService.js',
			'./src/common/services/focusService.js',
			'./src/common/services/formService.js',
			'./src/common/services/globalStateService.js',
			'./src/common/services/inboxService.js',
			'./src/common/services/k2aService.js',
			'./src/common/services/messageService.js',
			'./src/common/services/noteService.js',
			'./src/common/services/patientDetailStatusService.js',
			'./src/common/services/personaService.js',
			'./src/common/services/programService.js',
			'./src/common/services/providerService.js',
			'./src/common/services/providersService.js',
			'./src/common/services/reportingService.js',
			'./src/common/services/resultsService.js',
			'./src/common/services/securityService.js',
			'./src/common/services/staticDataService.js',
			'./src/common/services/summaryService.js',
			'./src/common/services/systemPreferenceService.js',
			'./src/common/services/ticklerService.js',
			'./src/common/services/reportByTemplateService.js',
			'./src/common/services/uxService.js',
			'./src/common/services/specialistsService.js',
			'./src/common/services/referralDoctorsService.js',

			'./src/common/filters/module.js',
			'./src/common/filters/age.js',
			'./src/common/filters/cut.js',
			'./src/common/filters/offset.js',
			'./src/common/filters/startFrom.js',

			'./src/common/directives/module.js',
			'./src/common/directives/junoConfirmClick.js',
			'./src/common/directives/typeaheadHelper.js',
			'./src/common/directives/patientSearchTypeahead.js',
			'./src/common/directives/patientTypeahead.js',
			'./src/common/directives/datepickerPopup.js',
			'./src/common/directives/jqueryUIResizable.js',
			'./src/common/directives/jqueryUIDraggable.js',
			'./src/common/directives/angular-ui-calendar.js',
			'./src/common/directives/selectOptionTooltip.js',

			'./src/common/directives/appointment/juno_appointment_status_select.js',

			'./src/common/directives/clinicaid/ca_field_alphadate.js',
			'./src/common/directives/clinicaid/ca_field_alpha.js',
			'./src/common/directives/clinicaid/ca_field_autocomplete.js',
			'./src/common/directives/clinicaid/ca_field_boolean.js',
			'./src/common/directives/clinicaid/ca_field_button_group.js',
			'./src/common/directives/clinicaid/ca_field_color.js',
			'./src/common/directives/clinicaid/ca_field_currency_filter.js',
			'./src/common/directives/clinicaid/ca_field_currency.js',
			'./src/common/directives/clinicaid/ca_field_date3.js',
			'./src/common/directives/clinicaid/ca_field_date.js',
			'./src/common/directives/clinicaid/ca_field_number.js',
			'./src/common/directives/clinicaid/ca_field_select.js',
			'./src/common/directives/clinicaid/ca_field_text.js',
			'./src/common/directives/clinicaid/ca_field_time.js',
			'./src/common/directives/clinicaid/ca_field_toggle.js',
			'./src/common/directives/clinicaid/ca_focus_field.js',
			'./src/common/directives/clinicaid/ca_info_messages.js',
			'./src/common/directives/clinicaid/ca_key_bind_scope.js',
			'./src/common/directives/clinicaid/ca_pagination.js',
			'./src/common/directives/clinicaid/ca_quill.js',
			'./src/common/directives/clinicaid/ca_sticky_search_results_footer.js',
			'./src/common/directives/clinicaid/ca_zero_pad.js',
			'./src/common/directives/clinicaid/compile_html.js',
			'./src/common/directives/clinicaid/file_model.js',
			'./src/common/directives/clinicaid/resize_handler.js',
			'./src/common/directives/clinicaid/resize.js',
			'./src/common/directives/clinicaid/scroll_handler.js',
			'./src/common/directives/clinicaid/scroll.js',
			'./src/common/directives/clinicaid/zero_pad.js',

			'./src/common/components/module.js',
			'./src/common/components/modalComponent.js',

			'./src/layout/module.js',
			'./src/layout/bodyController.js',
			'./src/layout/primaryNavComponent.js',
			'./src/layout/leftAsideComponent.js',

			'./src/patient/module.js',
			'./src/patient/newPatientController.js',
			'./src/patient/demographicCardComponent.js',

			'./src/dashboard/module.js',
			'./src/dashboard/dashboardController.js',
			'./src/dashboard/ticklerConfigureController.js',

			'./src/record/module.js',
			'./src/record/recordController.js',
			'./src/record/summary/module.js',
			'./src/record/summary/summaryController.js',
			'./src/record/summary/recordPrintController.js',
			'./src/record/summary/groupNotesController.js',
			'./src/record/summary/saveWarningController.js',
			'./src/record/forms/module.js',
			'./src/record/forms/formsController.js',
			'./src/record/details/module.js',
			'./src/record/details/detailsController.js',
			'./src/record/details/swipecardController.js',
			'./src/record/phr/module.js',
			'./src/record/phr/phrController.js',

			'./src/record/tracker/module.js',
			'./src/record/tracker/trackerController.js',

			'./src/tickler/module.js',
			'./src/tickler/ticklerListController.js',
			'./src/tickler/ticklerViewController.js',
			'./src/tickler/ticklerAddController.js',
			'./src/tickler/ticklerNoteController.js',
			'./src/tickler/ticklerCommentController.js',

			'./src/schedule/module.js',
			'./src/schedule/scheduleController.js',
			'./src/schedule/eventController.js',
			'./src/schedule/scheduleService.js',

			'./src/admin/module.js',
			'./src/admin/adminController.js',
			'./src/admin/integration/module.js',
			'./src/admin/integration/fax/module.js',
			'./src/admin/integration/fax/faxConfigurationController.js',
			'./src/admin/integration/fax/faxConfigurationEditController.js',
			'./src/admin/integration/fax/faxSendReceiveController.js',
			'./src/admin/integration/know2act/module.js',
			'./src/admin/integration/know2act/Know2actConfigController.js',
			'./src/admin/integration/know2act/Know2actNotificationController.js',
			'./src/admin/integration/know2act/Know2actTemplateController.js',

			'./src/billing/billingController.js',

			'./src/consults/module.js',
			'./src/consults/consultRequestAttachmentController.js',
			'./src/consults/consultResponseAttachmentController.js',
			'./src/consults/consultRequestListController.js',
			'./src/consults/consultRequestController.js',
			'./src/consults/consultResponseListController.js',
			'./src/consults/consultResponseController.js',

			'./src/inbox/module.js',
			'./src/inbox/inboxController.js',

			'./src/patient/search/module.js',
			'./src/patient/search/patientSearchController.js',
			'./src/patient/search/remotePatientResultsController.js',

			'./src/report/module.js',
			'./src/report/reportsController.js',
			'./src/report/reportBadAppointmentSheetController.js',
			'./src/report/reportDaySheetController.js',
			'./src/report/reportEdbListController.js',
			'./src/report/reportNoShowAppointmentSheetController.js',
			'./src/report/reportOldPatientsController.js',
			'./src/report/reportPatientChartListController.js',

			'./src/document/module.js',
			'./src/document/documentsController.js',

			'./src/settings/module.js',
			'./src/settings/settingsController.js',
			'./src/settings/changePasswordController.js',
			'./src/settings/quickLinkController.js',
			'./src/settings/summaryItemSettingsController.js',

			'./src/help/module.js',
			'./src/help/supportController.js',
			'./src/help/helpController.js',
			'./src/index.ts',
		]
	}
};
