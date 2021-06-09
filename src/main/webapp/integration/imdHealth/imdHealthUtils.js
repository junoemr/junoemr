// Requires jQuery

var Juno = Juno || {};
Juno.Integration = Juno.Integration || {};
Juno.Integration.iMDHealth = Juno.Integration.iMDHealth || {};

Juno.Integration.iMDHealth.openIMDHealth = function getIMDHealthSSOLink()
{
    var endPoint = "../ws/rs/integrations/iMDHealth/SSOLink";
    var self = this;

    var queryParam = jQuery.param({
        demographicNo: self.readDemographicNo(),
    });

    jQuery.ajax({
            type: "GET",
            url: endPoint + '?' + queryParam,
            success: self.onFetchLink,
            error: self.onFail,
    });
};

Juno.Integration.iMDHealth.readDemographicNo = function readDemographicNo()
{
    var regex = /demographicNo=\d+/i
    var param = regex.exec(window.location);

    if (param.size() === 1)
    {
        return param[0].split('=')[1];
    }
}

Juno.Integration.iMDHealth.onFetchLink = function onFetchLink(response)
{
    var ssoLink = response.body;
    window.open(ssoLink, "_blank");
};

Juno.Integration.iMDHealth.onFail = function onFail(error)
{
    alert("Could not connect to iMD Health.\nPlease check your integration settings and try again");
};

