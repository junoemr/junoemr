var Juno = Juno || {};
Juno.BillingHelper = Juno.BillingHelper || {};
Juno.BillingHelper.BC = Juno.BillingHelper.BC || {};


// Requires jQuery

Juno.BillingHelper.BC._applyBCP = function applyBCP() {
    if ($providerSelect.val() === "000000") {
        Juno.BillingHelper.BC._updateFacilityNumber($facNumInput, "");
        return;
    }
    else {
        Juno.BillingHelper.BC._applyProviderBCP($providerSelect, $facNumInput)
    }
};

Juno.BillingHelper.BC._applyProviderBCP = function applyProviderBCP($providerSelect, $facNumInput)
{
    var providerEndpoint =  Juno.BillingHelper.BC._localJunoInstance
        + "/ws/rs/providerService/provider/" + $providerSelect.val() + "/billing";

    jQuery.get(providerEndpoint)
        .done(function (provider)
        {
            if (provider && provider.bcBCPEligible)
            {
                var clinicEndpoint = Juno.BillingHelper.BC._localJunoInstance + "/ws/rs/clinic/";

                jQuery.get(clinicEndpoint)
                    .done(function(clinic)
                    {
                        if (clinic && clinic.bcFacilityNumber)
                        {
                            Juno.BillingHelper.BC._updateFacilityNumber($facNumInput, clinic.bcFacilityNumber);
                        }
                    })
                    .fail(Juno.BillingHelper.BC._alertError);
            }
            else
            {
                Juno.BillingHelper.BC._updateFacilityNumber($facNumInput, "");
            }
        })
        .fail(Juno.BillingHelper.BC._alertError);
};

Juno.BillingHelper.BC._localJunoInstance = "";

Juno.BillingHelper.BC._updateFacilityNumber = function updateFacilityNumber($facNumInput, facNo)
{
    if (facNo)
    {
        $facNumInput.val(facNo);
    }
    else
    {
        $facNumInput.val("");
    }
};

Juno.BillingHelper.BC._alertError = function alertError(error)
{
    alert("Cannot determine BCP eligibility");
    console.error(error);
};


/**
 * Bind the select provider element to the facility number element.  When a new provider
 * is selected, automatically fill in the facility number.
 */
Juno.BillingHelper.BC.initAutoApplyBCP = function initAutoApplyBCP(context, $providerSelect, $facNoInput)
{
    Juno.BillingHelper.BC._localJunoInstance = context;

    $providerSelect.change(function()
    {
        Juno.BillingHelper.BC._applyBCP($providerSelect, $facNoInput)
    });
    Juno.BillingHelper.BC._applyBCP($providerSelect, $facNoInput);
};
