angular.module('Admin').controller('Admin.AdminController', [
	'$scope',
	'$http',
	'$location',
	function ($scope, $http, $location)
	{
		let controller = this;
		controller.onSideNaveClick = function(group, item)
		{

		};

		function generateTransition(newState)
		{
			return function()
			{
				$location.url("/admin/"+newState);
			}
		}

		// define side nav items
		controller.navList = [
			{
				name: "User Management",
				items: [
					{
						name: "Add a Provider Record",
						callback: generateTransition("frame?frameUrl=/admin/provideraddarecordhtm.jsp")
					},
					{
						name: "Search/Edit/Delete Provider Records",
						callback: generateTransition("fizbang")
					},
					{
						name: "Add a Login Record",
						callback: null
					},
					{
						name: "Search/Edit/Delete Security Records",
						callback: null
					},
					{
						name: "Assign Role to Provider",
						callback: null
					},
					{
						name: "Unlock Account",
						callback: null
					},
				]
			},
			{
				name: "Billing",
				items: [
					{
						name: "Manage Billing Form",
						callback: null
					},
					{
						name: "Manage Private Bill",
						callback: null
					},
					{
						name: "Manage Service/Diagnostic Code Associations",
						callback: null
					},
					{
						name: "Manage Procedure/Fee Code Associations",
						callback: null
					},
					{
						name: "Manage Referral Doctors",
						callback: null
					},
					{
						name: "BC MSP Quick Billing",
						callback: null
					},
					{
						name: "Simulate Submission File2",
						callback: null
					},
					{
						name: "Generate Teleplan File2",
						callback: null
					},
					{
						name: "Manage Teleplan",
						callback: null
					},
					{
						name: "MSP Reconcilliation Reports",
						callback: null
					},
					{
						name: "Accounting Reports",
						callback: null
					},
					{
						name: "Edit Invoices",
						callback: null
					},
					{
						name: "Settle Over/Under Paid Claims",
						callback: null
					},
				]
			},
			{
				name: "Labs/Inbox",
				items: [
					{
						name: "HL7 Lab Upload",
						callback: null
					},
					{
						name: "Lab Forwarding Rules",
						callback: null
					},
					{
						name: "Add New Queue",
						callback: null
					},
				]
			},
			{
				name: "Forms/eForms",
				items: [
					{
						name: "Select Forms",
						callback: null,
					},
					{
						name: "Import Form Data",
						callback: null,
					},
					{
						name: "Manage eForms",
						callback: null,
					},
					{
						name: "Upload an Image",
						callback: null,
					},
					{
						name: "eForm Groups",
						callback: null,
					},
					{
						name: "Rich Text Letter",
						callback: null,
					},
					{
						name: "Patient-independent eForms",
						callback: null,
					},

				]
			},


		];
	}
]);
