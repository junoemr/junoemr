import '../../js/jquery-1.7.1.min.js';

var CARE_CONNECT_PPN_URL = "https://bc.careconnect.ca";
var CARE_CONNECT_EXTERNAL_URL = "https://health.careconnect.ca";

if (!window.Juno)
{
    window.Juno = {};
}
if (!Juno.CareConnect)
{
    Juno.CareConnect = {};
}
Juno.CareConnect.Util = {};
/**
 * Given information about the current environment, use the correct URL for ConnectCare.
 * Depending on whether the user is on the PPN, we should be using a different URL.
 *
 * CareConnect has disabled ICMP packets to their servers, and we have no real
 * info on how to actually detect as to whether we're on the PPN.
 * Assumption is that if the user is on PPN, then a simple GET request to the
 * server will return... something instead of timing out.
 */
Juno.CareConnect.Util.determineCareConnectURL = function determineCareConnectURL(callback)
{
    var checkPPN = true;
    Juno.CareConnect.Util.checkPPNOverride(function response(result)
    {
        checkPPN = result;
        // If we find a setting somewhere somehow indicating we don't care about the PPN, give the external URL
        if (!checkPPN)
        {
            callback(CARE_CONNECT_EXTERNAL_URL);
        }
        else
        {
            jQuery.ajax({
                url: CARE_CONNECT_PPN_URL,
                success: function success()
                {
                    callback(CARE_CONNECT_PPN_URL);
                },
                timeout: 5000, // milliseconds
                complete: function onTimeout()
                {
                    callback(CARE_CONNECT_EXTERNAL_URL);
                }
            });
        }
    });

}

/**
 * Helper function to wrap hitting the internal REST API so we can check for a given user preference.
 * In this case, we care about whether the user has decided to skip the check for whether they are on BC's PPN
 * and always use the external domain.
 */
Juno.CareConnect.Util.checkPPNOverride = function checkPPNOverride(callback)
{
    var endpoint = "../../ws/rs/providerSettings/all"
    jQuery.ajax({
        url: endpoint,
        success: function success(settings)
        {
            callback(settings.content[0].enableCareConnectPPNCheck);
        },
        error: function error()
        {
            // We don't know what happened so just assume we're checking
            callback(true);
        }
    })
}