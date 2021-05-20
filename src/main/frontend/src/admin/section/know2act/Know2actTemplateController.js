angular.module("Admin.Section.Know2act").controller('Admin.Section.Know2act.k2aTemplateController', [
	"reportByTemplateService",
	"k2aService",
	function (reportByTemplateService,
	          k2aService)
	{
		var controller = this;

		controller.message = "";
		controller.k2aActive = false;
		controller.k2aReports = null;
		controller.K2A_URL = "";

		controller.dtOptions = {};
		// controller.dtOptions = DTOptionsBuilder.newOptions().withOption('order', [1, 'asc']);

		controller.checkStatus = function checkStatus() {
			k2aService.isK2AInit().then(
				function success(data) {
					controller.k2aActive = data;
				},
				function failure(error) {
					console.error(error);
					alert("Failed to verify K2A Status");
				}
			);
		};
		controller.checkStatus();

		controller.getAllK2AReports = function getAllK2AReports(){
			reportByTemplateService.getAllK2AReports().then(function(data){
				controller.k2aReports = data;
				console.log("k2a reports", controller.k2aReports);
			});
		};
		controller.getAllK2AReports();

		controller.saveK2AReport = function saveK2AReport(id){
			reportByTemplateService.getK2AReportById(id).then(function(data){
				controller.message = data;
				console.log(controller.message);
				refreshParent();
			});
		};

		controller.openK2AUrl = function openK2AUrl() {
			reportByTemplateService.getK2AUrl().then(
				function success(data) {
					window.open(data + "/#/ws/rs/posts/browse/Report");
				},
				function failure(error) {
					console.log(error);
				});
		};

		controller.refreshParent = function refreshParent() {
			window.opener.document.location.href = "../../../../oscarReport/reportByTemplate/homePage.jsp";
		};
		window.onunload = controller.refreshParent();
	}
]);