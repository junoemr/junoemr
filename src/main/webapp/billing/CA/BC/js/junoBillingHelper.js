var Juno = Juno || {};
Juno.BillingHelper = Juno.BillingHelper || {};
Juno.BillingHelper.BC = Juno.BillingHelper.BC || {};

// Requires jQuery

Juno.BillingHelper.BC._noProviderSelected = "000000";
Juno.BillingHelper.BC._noSiteSelected = "-1";
Juno.BillingHelper.BC._localJunoInstance = "";

Juno.BillingHelper.BC._alertError = function alertError(error)
{
    alert("Cannot determine BCP eligibility");
    console.error(error);
};

Juno.BillingHelper.BC._applyBCP = function applyBCP($providerSelect, $facNumInput) {
    if ($facNumInput.val() === Juno.BillingHelper.BC._noProviderSelected)
    {
        Juno.BillingHelper.BC._updateFacilityNumber($facNumInput, "");
        return;
    }
    else
    {
        Juno.BillingHelper.BC._applyProviderBCP($providerSelect, $facNumInput)
    }
};

Juno.BillingHelper.BC._applyProviderBCP = function applyProviderBCP($providerSelect, $facNumInput)
{
    var providerEndpoint =  Juno.BillingHelper.BC._localJunoInstance
        + "/ws/rs/providerService/provider/" + $providerSelect.val() + "/billing";

    jQuery.get(providerEndpoint)
        .done(function (providerResponse)
        {
            var provider = providerResponse.body;

            if (provider && provider.bcBCPEligible)
            {
                var clinicEndpoint = Juno.BillingHelper.BC._localJunoInstance + "/ws/rs/clinic/";

                jQuery.get(clinicEndpoint)
                    .done(function(clinicResponse)
                    {
                        var clinic = clinicResponse.body;

                        if (clinic)
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

Juno.BillingHelper.BC._filterSiteSelectOptions = function filterSiteSelectOptions($siteSelect, ignoreList)
{
    $siteSelect.children("option").each(function () {
        var option = jQuery(this);

        option.removeProp('selected');

        if (option.val() === Juno.BillingHelper.BC._noSiteSelected)
        {
            option.removeProp('disabled');
        }
        else if (ignoreList && ignoreList.find(function(element) {return element === option.val()}))
        {
            option.removeProp('disabled');
        }
        else
        {
            option.prop("disabled", true);
        }
    });
};

/**
 * If there's only one valid site available, select it.
 * Otherwise select the placeholder
 */
Juno.BillingHelper.BC._tryAutoApplySite = function tryAutoApplySite($siteSelect)
{
    var $enabledOpts = $siteSelect.children("option").siblings(":not([disabled])");

    // Length === 2 accounts for the placeholder itself being an option
    if ($enabledOpts.length === 2)
    {
        console.log("auto apply site");
        var $defaultSite = $enabledOpts.filter(":not([value='-1'])");

        $defaultSite.prop("selected", true);
    }
    else
    {
        console.log("auto apply placeholder");
        var $placeHolder = $enabledOpts.filter("[value='-1']");
        $placeHolder.prop("selected", true);
    }

    console.log("triggering site select change event");
    // Both outcomes have changed the site select, so trigger the event
    $siteSelect.trigger("change");
};

Juno.BillingHelper.BC._applyProviderSites = function applyProviderSites($providerSelect, $siteSelect, $facNumInput)
{
    if ($facNumInput.val() === Juno.BillingHelper.BC._noProviderSelected)
    {
        Juno.BillingHelper.BC._updateFacilityNumber($facNumInput, "");
        Juno.BillingHelper.BC._filterSiteSelectOptions($siteSelect);
        Juno.BillingHelper.BC._tryAutoApplySite($siteSelect);
    }
    else
    {
        var siteEndpoint =  Juno.BillingHelper.BC._localJunoInstance
            + "/ws/rs/sites/provider/" + $providerSelect.val();
        jQuery.get(siteEndpoint)
            .done(function(sitesResponse)
            {
                var siteIds = sitesResponse.body.map(function(site)
                {
                    return site.siteId.toString(10);
                });

                Juno.BillingHelper.BC._filterSiteSelectOptions($siteSelect, siteIds);
                Juno.BillingHelper.BC._tryAutoApplySite($siteSelect);
            })
            .fail(Juno.BillingHelper.BC._alertError);
    }

};

Juno.BillingHelper.BC._applyBCPMultiSite = function applyBCPMultiSite($providerSelect, $siteSelect, $facNoInput)
{
    console.log("site changed");
    var siteId = $siteSelect.val();

    if (siteId && siteId !== Juno.BillingHelper.BC._noSiteSelected) {

        var providerNo = $providerSelect.val();
        if (providerNo && providerNo !== Juno.BillingHelper.BC._noProviderSelected) {
            console.log("provider and site are selected, going to check up on the the site now");
            var siteBillingEndpoint = Juno.BillingHelper.BC._localJunoInstance
                + '/ws/rs/sites/' + siteId + '/provider/' + $providerSelect.val() + '/billing';

            jQuery.get(siteBillingEndpoint)
                .done(function (billingResponse) {
                    var providerBilling = billingResponse.body;

                    if (providerBilling && providerBilling.bcBCPEligible) {
                        var siteEndpoint = Juno.BillingHelper.BC._localJunoInstance
                            + '/ws/rs/sites/' + siteId;

                        jQuery.get(siteEndpoint)
                            .done(function (siteResponse) {
                                var site = siteResponse.body;

                                if (site) {
                                    Juno.BillingHelper.BC._updateFacilityNumber($facNoInput, site.bcFacilityNumber);
                                }

                            })
                            .fail(Juno.BillingHelper.BC._alertError)
                    }
                })
                .fail(Juno.BillingHelper.BC._alertError)
        }
    }
    else
    {
        Juno.BillingHelper.BC._updateFacilityNumber($facNoInput, "");
    }
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

/**
 * Bind the provider select, site selecte, and facility number elements together.
 * When a provider is selected, the site select options are enabled or disabled accordingly.
 * When both a provider and a site are selected, the facility number is automatically filled in.
 */
Juno.BillingHelper.BC.initAutoApplyBCPMultiSite = function initAutoApplyBCPMultiSite(context, $providerSelect, $facNoInput, $siteSelect)
{
    Juno.BillingHelper.BC._localJunoInstance = context;

    $providerSelect.change(function()
    {
        Juno.BillingHelper.BC._applyProviderSites($providerSelect, $siteSelect, $facNoInput);
    });

    $siteSelect.change(function()
    {
        Juno.BillingHelper.BC._applyBCPMultiSite($providerSelect, $siteSelect, $facNoInput);
    });

    Juno.BillingHelper.BC._applyBCPMultiSite($providerSelect, $siteSelect, $facNoInput);
};

