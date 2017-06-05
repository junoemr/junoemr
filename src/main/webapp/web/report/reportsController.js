// Figure out what to do with this. 
oscarApp.factory('ReportNavigation', function($rootScope)
{
	return {
		location: '',

		load: function(msg)
		{
			this.location = msg;
		}
	};
});

angular.module('Report').controller('Report.ReportsController', [

	'$scope',
	'$state',
	'$filter',
	'$log',
	'ReportNavigation',

	function(
		$scope,
		$state,
		$filter,
		$log,
		ReportNavigation)
	{

		var controller = this;

		controller.reportSidebar = ReportNavigation;

		$scope.$emit('configureShowPatientList', false);

		controller.reports = [
			{
				name: 'Daysheets',
				templateUrl: 'report/report_daysheet.jsp'
			},
			{
				name: 'Active Patients',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../report/reportactivepatientlist.jsp'
			},
			{
				name: 'Old Patients',
				templateUrl: 'report/report_oldPatients.jsp'
			},
			{
				name: 'Patient Chart List',
				templateUrl: 'report/report_patientChartList.jsp'
			},
			{
				name: 'EDB List',
				templateUrl: 'report/report_edb_list.jsp'
			},
			{
				name: 'Bad Appointments',
				templateUrl: 'report/report_badAppointments.jsp'
			},
			{
				name: 'No Show Appointments',
				templateUrl: 'report/report_noShowAppointments.jsp'
			},
			{
				name: 'Consultations',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/ConsultationReport.jsp'
			},
			{
				name: 'Lab Requisitions',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/LabReqReport.jsp'
			},
			{
				name: 'Preventions',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarPrevention/PreventionReporting.jsp'
			},
			{
				name: 'Injections',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/InjectionReport2.jsp'
			},
			{
				name: 'Demographic Report Tool',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/ReportDemographicReport.jsp'
			},
			{
				name: 'Patient Study List',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../report/demographicstudyreport.jsp'
			},
			{
				name: 'Chronic Disease Management',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/oscarMeasurements/SetupSelectCDMReport.do'
			},
			{
				name: 'Waiting List',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarWaitingList/SetupDisplayWaitingList.do?waitingListId='
			},
			{
				name: 'Forms',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../report/reportFormRecord.jsp'
			},
			{
				name: 'Clinical',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../report/ClinicalReports.jsp'
			},
			{
				name: 'SCBP demographic Report',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../report/reportBCARDemo.jsp'
			},
			{
				name: 'Report By Template',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/reportByTemplate/homePage.jsp'
			},
			{
				name: 'General Forms',
				group: 'CAISI',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../PMmodule/ClientManager.do?method=getGeneralFormsReport'
			},
			{
				name: 'Registration Intake',
				group: 'CAISI',
				templateUrl: 'report/report_registration_intake.jsp'
			},
			{
				name: 'Follow-up Intake',
				group: 'CAISI',
				templateUrl: 'report/report_followup_intake.jsp'
			},
			{
				name: 'Activity Report',
				group: 'CAISI',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../PMmodule/Reports/ProgramActivityReport.do'
			},
			{
				name: 'UCF Report',
				group: 'CAISI',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../SurveyManager.do?method=reportForm'
			},
			{
				name: 'SH Mental Health Report',
				group: 'CAISI',
				templateUrl: 'report/report_sh_mental_health.jsp'
			},
			{
				name: 'OSIS Report',
				group: 'Public Health',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/OSISReport.jsp'
			},
			{
				name: 'One Time Consult CDS Report',
				group: 'Public Health',
				templateUrl: 'report/report_iframe.jsp',
				iframeUrl: '../oscarReport/CDSOneTimeConsultReport.jsp'
			},

		];


		controller.editDemographicSet = function editDemographicSet()
		{
			controller.selectReport(
			{
				name: 'Demographic Set Edit',
				templateUrl: 'report/report_iframe.jsp',
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


		controller.selectReport(controller.reports[0]);

		controller.openReportWindow = function openReportWindow(url, name)
		{
			window.open(url, name, 'height=900,width=700');
		};
	}
]);