// Figure out what to do with this. 
angular.module('oscarProviderViewModule').factory('ReportNavigation', [ '$rootScope', function($rootScope)
{
	return {
		location: '',

		load: function(msg)
		{
			this.location = msg;
		}
	};
}]);

angular.module('Report').controller('Report.ReportsController', [

	'$scope',
	'$state',
	'$filter',
	'$log',
	'ReportNavigation',
	'securityService',

	function(
		$scope,
		$state,
		$filter,
		$log,
		ReportNavigation,
		securityService,)
	{

		var controller = this;
		controller.me = securityService.getUser().providerNo;

		$scope.$emit('configureShowPatientList', false);

		controller.reports = [
			{
				numberLabel: "1",
				name: 'EDB List',
				templateUrl: 'src/report/report_edb_list.jsp'
			},
			{
				numberLabel: "2",
				name: 'Active Patients',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../report/reportactivepatientlist.jsp'
			},
			{
				numberLabel: "3",
				name: 'Daysheets',
				templateUrl: 'src/report/report_daysheet.jsp',
			},
			{
				numberLabel: "5",
				name: 'Bad Appointments',
				templateUrl: 'src/report/report_badAppointments.jsp'
			},
			{
				numberLabel: "6",
				name: 'Patient Chart List',
				templateUrl: 'src/report/report_patientChartList.jsp'
			},
			{
				numberLabel: "7",
				name: 'Old Patients',
				templateUrl: 'src/report/report_oldPatients.jsp'
			},
			{
				numberLabel: "8",
				name: 'No Show Appointments',
				templateUrl: 'src/report/report_noShowAppointments.jsp'
			},
			{
				numberLabel: "9",
				name: 'Consultations',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/ConsultationReport.jsp'
			},
			{
				numberLabel: "10",
				name: 'Lab Requisitions',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/LabReqReport.jsp'
			},
			{
				numberLabel: "11",
				name: 'Demographic Report Tool',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/ReportDemographicReport.jsp'
			},
			{
				numberLabel: "13",
				name: 'Preventions',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarPrevention/PreventionReporting.jsp'
			},
			{
				numberLabel: "14",
				name: 'Patient Study List',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../report/demographicstudyreport.jsp'
			},
			{
				numberLabel: "15",
				name: 'Chronic Disease Management',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/oscarMeasurements/SetupSelectCDMReport.do'
			},
			{
				numberLabel: "16",
				name: 'Waiting List',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarWaitingList/SetupDisplayWaitingList.do?waitingListId='
			},
			{
				numberLabel: "17",
				name: 'Forms',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../report/reportFormRecord.jsp'
			},
			{
				numberLabel: "18",
				name: 'SCBP demographic Report',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../report/reportBCARDemo.jsp'
			},
			{
				numberLabel: "19",
				name: 'Clinical',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../report/ClinicalReports.jsp'
			},
			{
				numberLabel: "20",
				name: 'Injections',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/InjectionReport2.jsp'
			},
			{
				numberLabel: "21",
				name: 'OSIS Report',
				group: 'Public Health',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/OSISReport.jsp'
			},
			{
				numberLabel: "22",
				name: 'One Time Consult CDS Report',
				group: 'Public Health',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/CDSOneTimeConsultReport.jsp'
			},
			{
				numberLabel: "23",
				name: 'Report By Template',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/reportByTemplate/homePage.jsp'
			},
			{
				numberLabel: "24",
				name: 'Unbilled Reports',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../billing/CA/billingReportCenter.jsp?displaymode=billreport&providerview=' + controller.me
			},
		];

		controller.init = function init()
		{
			controller.reportGroup = "";
			controller.reportGroups = controller.getReportGroups();
			controller.reportSidebar = ReportNavigation;
			controller.test = controller.reportSidebar.url;
		};

		controller.editDemographicSet = function editDemographicSet()
		{
			controller.selectReport(
			{
				name: 'Demographic Set Edit',
				templateUrl: 'src/report/report_iframe.jsp',
				iframeUrl: '../oscarReport/demographicSetEdit.jsp'
			});
		};

		controller.getReports = function getReports()
		{
			if (controller.reportGroup != null && controller.reportGroup.length > 0)
			{
				var filtered = $filter('filter')(controller.reports,
				{
					group: controller.reportGroup
				});
				return filtered;

			}
			return controller.reports;
		};

		controller.getReportGroups = function getReportGroups()
		{
			var groups = [
			{
				value: '',
				label: 'All Groups'
			}];
			var groupMap = {};

			for (var i = 0; i < controller.reports.length; i++)
			{
				if (controller.reports[i].group != null)
				{
					groupMap[controller.reports[i].group] = controller.reports[i].group;
				}
			}

			for (var key in groupMap)
			{
				groups.push(
				{
					value: key,
					label: key
				});
			}

			return groups;
		};

		controller.selectReport = function selectReport(report)
		{
			controller.currentReport = report;

			if (report.direct === true)
			{
				window.open(report.window.url, report.name, 'width=' + report.window.width + '&height=' + report.window.height);
			}
			else
			{
				ReportNavigation.load(report.templateUrl);
			}
		};


		// controller.selectReport(controller.reports[0]);

		// controller.openReportWindow = function openReportWindow(url, name)
		// {
		// 	window.open(url, name, 'height=900,width=700');
		// };
	}
]);