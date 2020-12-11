var Juno = Juno || {};
Juno.BillingHelper = Juno.BillingHelper || {};
Juno.BillingHelper.ON = Juno.BillingHelper.ON || {};

Juno.BillingHelper.ON._localJunoInstance = null;

/**
 * executes the provider callback with the provider billing data returned from the server
 * @param context - juno context path
 * @param providerNo - the provider who's billing data is to be retrieved
 * @param callback - the callback to call with the billing data as an argument
 * @private
 */
Juno.BillingHelper.ON._providerBillingWrapper = function (context, providerNo, callback)
{
    if (providerNo)
    {
        providerNo = providerNo.match(/^\d+/)[0];
        var providerEndpoint = context + "/ws/rs/providerService/provider/" + providerNo + "/billing";

        // cut down w/ no error handler because of old jquery on ON billing page.
        jQuery.get(providerEndpoint, null, function (result)
        {
            result = JSON.parse(result);
            callback(result.body);
        })
    }
}

Juno.BillingHelper.ON._updateVisitLocationCode = function(providerNo, $visitLocationSelect)
{
    Juno.BillingHelper.ON._providerBillingWrapper(Juno.BillingHelper.ON._localJunoInstance, providerNo, function (providerBilling)
    {
        if (providerBilling.onMasterNumber)
        {
            for (var $option of jQuery.makeArray($visitLocationSelect.children("option")))
            {
                $option = jQuery($option);
                var match = $option.val().match(/^\d+/);
                if (match && match[0] === providerBilling.onMasterNumber)
                {
                    $visitLocationSelect.val($option.val());
                }
            }
        }
    });
}

Juno.BillingHelper.ON._updateServiceLocationCode = function(providerNo, $serviceLocationSelect)
{
    Juno.BillingHelper.ON._providerBillingWrapper(Juno.BillingHelper.ON._localJunoInstance, providerNo, function (providerBilling)
    {
        if (providerBilling.onServiceLocation)
        {
            $serviceLocationSelect.val(providerBilling.onServiceLocation);
        }
    });
}


/**
 * create an on change hook for provider select. This hook will adjust the visit location
 * based on provider setting
 * @param context - juno context path
 * @param $providerSelect - the provider select to watch
 * @param $visitLocationSelect - the visit location select to adjust
 */
Juno.BillingHelper.ON.initVisitLocationCodeHook = function(context, $providerSelect, $visitLocationSelect)
{
    Juno.BillingHelper.ON._localJunoInstance = context;

    Juno.BillingHelper.ON._updateVisitLocationCode($providerSelect.val(), $visitLocationSelect);
    $providerSelect.change(function()
    {
        Juno.BillingHelper.ON._updateVisitLocationCode($providerSelect.val(), $visitLocationSelect);
    });
}

/**
 * create an on change hook for provider select. This hook will adjust the service location
 * based on provider setting
 * @param context - juno context path
 * @param $providerSelect - the provider select to watch
 * @param $serviceLocationSelect - the service location select to adjust
 */
Juno.BillingHelper.ON.initServiceLocationCodeHook = function(context, $providerSelect, $serviceLocationSelect)
{
    Juno.BillingHelper.ON._localJunoInstance = context;

    Juno.BillingHelper.ON._updateServiceLocationCode($providerSelect.val(), $serviceLocationSelect);
    $providerSelect.change(function()
    {
        Juno.BillingHelper.ON._updateServiceLocationCode($providerSelect.val(), $serviceLocationSelect);
    });
}
