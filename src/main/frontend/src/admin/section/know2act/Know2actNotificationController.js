angular.module("Admin.Section.Know2act").controller('Admin.Section.Know2act.k2aNotificationController', [
	"k2aService",
	function (k2aService)
	{
		var controller = this;
		controller.page = {};
		controller.page.notifications = [];

		controller.readMore = function (notif) {
			k2aService.getMoreNotification(notif).then(
				function success(data) {
					var win = window.open(data, 'moreInfo', 'width=700,height=700');
					win.focus();
				},
				function failure(error) {
					console.error(error);
				});
		};
		controller.archive = function (notif) {
			k2aService.ackNotification(notif).then(
				function success(data) {
					controller.getNotifications();
				},
				function failure(error) {
					console.error(error);
					alert("Failed to acknowledge notification");
				});
		};
		controller.getNotifications = function () {
			k2aService.getNotifications().then(
				function success(data) {
					controller.page.notifications = data;
				},
				function failure(error) {
					console.error(error);
					alert("Failed to load notifications");
				});
		};
		controller.getNotifications();
	}
]);