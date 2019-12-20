IceFall =
{
	ICE_FALL_WS_BASE: "../ws/rs/integrations/iceFall",
	ICE_FALL_WS_AUTH: "/authenticate",
	ICE_FALL_WS_SEND_FORM: "/sendForm",

	doPost: function (endpoint, postData)
	{
		return new Promise(function (resolve, reject)
		{
			$.post({
				url: IceFall.ICE_FALL_WS_BASE + endpoint,
				data: JSON.stringify(postData),
				contentType: "application/json"
			})
					.done(function (response)
					{
						if (response.error)
						{
							console.log(response);
							reject(response.error.message);
						}
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

	sendForm: function (fdid, fid, demoNo, dosage, expiryDate, type, thcLimit, diagnosis)
	{
		let submitObj = IceFall.formToJSON(document.forms[0]);
		submitObj.fdid = fdid;
		submitObj.fid = fid;
		submitObj.demographicNo = demoNo;
		submitObj.dosage = dosage;
		submitObj.expiryDate = expiryDate;
		submitObj.type = type;
		submitObj.thcLimit = thcLimit;
		submitObj.diagnosis = diagnosis;
		return IceFall.doPost(IceFall.ICE_FALL_WS_SEND_FORM, submitObj);
	},

	formToJSON: function (formElements)
	{
		return [].reduce.call(formElements, (data, element) =>
		{
			data[element.name] = element.value;
			return data;
		}, {});
	},

};