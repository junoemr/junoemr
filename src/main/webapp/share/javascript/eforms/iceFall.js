IceFall =
{
	ICE_FALL_WS_BASE: "../ws/rs/integrations/iceFall",
	ICE_FALL_WS_AUTH: "/authenticate",
	ICE_FALL_WS_SEND_FORM: "/sendForm",

	doPost: function (endpoint, postData)
	{
		return new Promise(function (resolve, reject)
		{
			console.log(JSON.stringify(postData));
			$.post({
				url: IceFall.ICE_FALL_WS_BASE + endpoint,
				data: JSON.stringify(postData),
				contentType: "application/json"
			})
					.done(function (response)
					{
						resolve(response);
					})
					.fail(function (response)
					{
						reject(response);
					});
		});
	},

	authenticate: function ()
	{
		return IceFall.doPost(IceFall.ICE_FALL_WS_AUTH, null);
	},

	sendForm: function (formId, demoNo)
	{
		return IceFall.doPost(IceFall.ICE_FALL_WS_SEND_FORM, {fdid: formId, demographicNo: demoNo});
	},

};