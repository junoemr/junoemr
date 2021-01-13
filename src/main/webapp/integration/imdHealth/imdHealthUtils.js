// Requires jQuery

var Juno = Juno || {};
Juno.Integration = Juno.Integration || {};
Juno.Integration.iMDHealth = Juno.Integration.iMDHealth || {};

Juno.Integration.iMDHealth.openIMDHealth = function getIMDHealthSSOLink()
{
    var endPoint = "../ws/rs/integrations/iMDHealth/SSOLink";
    var self = this;

    jQuery.ajax({
            type: "GET",
            url: endPoint,
            success: self.onFetchLink,
            error: self.onFail,
    });
};

Juno.Integration.iMDHealth.onFetchLink = function onFetchLink(response)
{
    var ssoLink = response.body;
    window.open(ssoLink, "_blank");
};

Juno.Integration.iMDHealth.onFail = function onFail(error)
{
    alert("Could not connect to iMDHealth.\nPlease check your integration settings and try again");
};

