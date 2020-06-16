var myhealthaccess = myhealthaccess || {};

myhealthaccess = {
	ERROR_NO_INTEGRATION: "error_no_integration",

	// ask MHA if the demographic is confirmed with this clinic
	checkDemographicConfirmed: function checkDemographicConfirmed(contextPath, demographicNo, site)
	{
		return new Promise((resolve, reject) =>
		{
			myhealthaccess.getIntegration(contextPath, site).then((integration) =>
			{
				integration = JSON.parse(integration).body;
				if (integration)
				{
					jQuery.ajax(
							{
								url: contextPath + "/ws/rs/myhealthaccess/integration/" + integration.id +
										"/demographic/" + demographicNo + "/confirmed",
								type: "GET",
								success: (result) =>
								{
									resolve(result);
								},
								error: (error) =>
								{
									reject(error);
								}
							});
				}
				else
				{
					reject(myhealthaccess.ERROR_NO_INTEGRATION)
				}
			}).catch((error) =>
			{
				reject(error);
			});
		});
	},


	// get the mha integration for the specified site
	getIntegration: function getIntegration(contextPath, site)
	{
		return new Promise((resolve, reject) =>
		{
			var siteParam = "";
			if (site)
			{
				var siteParam = "?site=" + site;
			}
			jQuery.ajax(
					{
						url: contextPath + "/ws/rs/myhealthaccess/integrations/" + siteParam,
						type: "GET",
						success: (result) =>
						{
							resolve(result);
						},
						error: (error) =>
						{
							reject(error);
						}
					});
		});
	},

	// get the mha integration for the specified site
	getAppointment: function getAppointment(contextPath, site, apptNo)
	{
		return new Promise((resolve, reject) =>
		{
			myhealthaccess.getIntegration(contextPath, site).then((integration) =>
			{
				integration = JSON.parse(integration).body;
				if (integration)
				{
					var queryParams = "?appointmentNo=" + apptNo;
					jQuery.ajax(
							{
								url: contextPath + "/ws/rs/myhealthaccess/integration/" + integration.id + "/appointments/" + queryParams,
								type: "GET",
								success: (result) =>
								{
									resolve(result);
								},
								error: (error) =>
								{
									reject(error);
								}
							});
				}
				else
				{
					reject(myhealthaccess.ERROR_NO_INTEGRATION)
				}
			});
		});
	},

	// send a telehealth appointment notification to the the patient of the appointment
	sendTelehealthAppointmentNotification: function (contextPath, site, mhaAppointment)
	{
		return myhealthaccess.getIntegrationWrapper(contextPath, site, (resolve, reject, integration) =>
		{
			jQuery.ajax(
				{
					url: contextPath + "/ws/rs/myhealthaccess/integration/" + integration.id + "/appointment/" + mhaAppointment.id + "/send_telehealth_appt_notification",
					type: "POST",
					success: (result) =>
					{
						resolve(result);
					},
					error: (error) =>
					{
						reject(error);
					}
				});
		});
	},

	getIntegrationWrapper: function getIntegrationWrapper(contextPath, site, callback)
	{
		return new Promise((resolve, reject) =>
		{
			myhealthaccess.getIntegration(contextPath, site).then((integration) =>
			{
				integration = JSON.parse(integration).body;
				if (integration)
				{
					callback(resolve, reject, integration)
				}
				else
				{
					reject(myhealthaccess.ERROR_NO_INTEGRATION)
				}
			});
		});
	},
};