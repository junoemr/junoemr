var Juno = Juno || {};
Juno.BillingHelper = Juno.BillingHelper || {};
Juno.BillingHelper.BC = Juno.BillingHelper.BC || {};

// Requires jQuery

Juno.BillingHelper.BC._noProviderSelected = "000000";
Juno.BillingHelper.BC._noSiteSelected = "-1";
Juno.BillingHelper.BC._localJunoInstance = "";

Juno.BillingHelper.BC._isProviderSelected = function isProviderSelected(providerNo)
{
    return (providerNo && providerNo !== Juno.BillingHelper.BC._noProviderSelected);
};

Juno.BillingHelper.BC._isSiteSelected = function isSiteSelected(siteNo)
{
    return (siteNo && siteNo !== Juno.BillingHelper.BC._noSiteSelected);
};

/**
 * Set the select menu to the value if it exists, otherwise don't change it
 *
 * @param $select jQuery select menu to set
 * @param targetVal value to set (if found)
 * @private
 */
Juno.BillingHelper.BC._setSelectToValue = function setSelectToValue($select, targetVal)
{
    for (var $option of $select.children("option").toArray())
    {
        $option = jQuery($option);
        var match = $option.val().match(/^\w/);

        if (match && match[0] === targetVal)
        {
            $select.val($option.val());
        }
    }
}

/**
 * Determine which site is selected, safe for use with single site instances.
 */
Juno.BillingHelper.BC._selectedSite = function selectedSite($siteSelect)
{
    if ($siteSelect && Juno.BillingHelper.BC._isSiteSelected($siteSelect.val()))
    {
        return $siteSelect.val();
    }

    return null;
}

Juno.BillingHelper.BC._alertError = function alertError(error)
{
    alert("Cannot determine BCP eligibility");
    console.error(error);
};

/**
 * executes the provider callback with the provider billing data returned from the server
 * @param context - juno context path
 * @param providerNo - the provider who's billing data is to be retrieved
 * @param callback - the callback to call with the billing data as an argument
 * @private
 */
Juno.BillingHelper._providerBillingWrapper = function (context, providerNo, callback)
{
    var providerEndpoint = context + "/ws/rs/providerService/provider/" + providerNo + "/billing";

    if (Juno.BillingHelper.BC._isProviderSelected(providerNo))
    {
        jQuery.get(providerEndpoint).done(function (result)
        {
            callback(result.body);
        }).fail(function(err)
        {
            console.error("Failed to update rural retention code based on provider setting with error: " + err);
        });
    }
}

/**
 * executes the provider callback with the provider billing data returned from the server
 * @param context - juno context path
 * @param providerNo - the provider who's billing data is to be retrieved
 * @param callback - the callback to call with the billing data as an argument
 * @private
 */
Juno.BillingHelper._siteBillingWrapper = function (siteNo, callback)
{
    var siteEndpoint = Juno.BillingHelper.BC._localJunoInstance
        + '/ws/rs/sites/' + siteId;

    if (Juno.BillingHelper.BC._isProviderSelected(providerNo))
    {
        jQuery.get(siteEndpoint)
            .done(function (result)
            {
                callback(result.body);
            })
            .fail(function(error)
            {
                console.error(error)
            });
    }
}

/**
 * Get system properties from the server, executing the callback on success
 * @param preference - system preference to look up
 * @param defaultVal - default value to use if preference can't be found
 * @param callback - the callback to call with the billing data as an argument
 * @private
 */
Juno.BillingHelper._systemPreferenceWrapper= function (preference, defaultVal, callback)
{
    var clinicEndpoint = Juno.BillingHelper.BC._localJunoInstance + "/ws/rs/systemPreference/" + preference;

    jQuery.get(clinicEndpoint, {default: defaultVal})
        .done(function(response)
        {
            callback(response.body);
        })
        .fail(function(error)
        {
            console.error(error)
        });
}

Juno.BillingHelper.BC._applyBCP = function applyBCP($providerSelect, $facNumInput) {
    if (Juno.BillingHelper.BC._isProviderSelected($providerSelect.val()))
    {
        Juno.BillingHelper.BC._applyProviderBCP($providerSelect, $facNumInput);
    }
    else
    {
        Juno.BillingHelper.BC._updateFacilityNumber($facNumInput, "");
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
    $siteSelect.children("option").each(function ()
    {
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
 * Set the SLC value based on the Service Location code key.  This is the single letter associated with the
 * SLC, and does not include the description of the code.
 */
Juno.BillingHelper.BC._tryAutoApplySite = function tryAutoApplySite($siteSelect)
{
    var $enabledOpts = $siteSelect.children("option").siblings(":not([disabled])");

    // Length === 2 accounts for the placeholder itself being an option
    if ($enabledOpts.length === 2)
    {
        var $defaultSite = $enabledOpts.filter(":not([value='-1'])");

        $defaultSite.prop("selected", true);
    }
    else
    {
        var $placeHolder = $enabledOpts.filter("[value='-1']");
        $placeHolder.prop("selected", true);
    }

    // Both outcomes have changed the site select, so trigger the event
    $siteSelect.trigger("change");
};

Juno.BillingHelper.BC._applyProviderSites = function applyProviderSites($providerSelect, $siteSelect, $facNumInput)
{
    if (Juno.BillingHelper.BC._isProviderSelected($providerSelect.val()))
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
    else
    {
        Juno.BillingHelper.BC._updateFacilityNumber($facNumInput, "");
        Juno.BillingHelper.BC._filterSiteSelectOptions($siteSelect);
        Juno.BillingHelper.BC._tryAutoApplySite($siteSelect);
    }
};

Juno.BillingHelper.BC._applyBCPMultiSite = function applyBCPMultiSite($providerSelect, $siteSelect, $facNoInput)
{
    var siteId = $siteSelect.val();

    if (Juno.BillingHelper.BC._isSiteSelected(siteId))
    {
        var providerNo = $providerSelect.val();
        if (providerNo && providerNo !== Juno.BillingHelper.BC._noProviderSelected) {
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
                    else
                    {
                        Juno.BillingHelper.BC._updateFacilityNumber($facNoInput, "");
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
 * updates rural retention code base on provider billing setting.
 * @param providerNo - the provider who's billing setting will be used.
 * @param $ruralRetentionCodeSelect - the rural retention code select to update.
 * @private
 */
Juno.BillingHelper.BC._updateRuralRetentionCode = function(providerNo, $ruralRetentionCodeSelect)
{
    Juno.BillingHelper._providerBillingWrapper(Juno.BillingHelper.BC._localJunoInstance, providerNo, function (providerBilling)
    {
        if (providerBilling.bcRuralRetentionCode && providerBilling.bcRuralRetentionName)
        {
            let ruralRetentionSelectVal = providerBilling.bcRuralRetentionCode + "|" +
                providerBilling.bcRuralRetentionName.replace(/^\(\d+\)\s+/, "");
            $ruralRetentionCodeSelect.val(ruralRetentionSelectVal);
        }
    });
}

/**
 * Updates the service location code based on the following priority order:
 * 1) Provider
 * 2) Site (if multisite)
 * 3) Clinic
 *
 * @param providerNo - the provider who's billing setting will be used.
 * @param $serviceLocationSelect - the service location select to update
 * @private
 */
Juno.BillingHelper.BC._updateServiceLocationCode = function(providerNo, siteNo, $serviceLocationSelect)
{
    Juno.BillingHelper._providerBillingWrapper(Juno.BillingHelper.BC._localJunoInstance, providerNo, function (providerBilling)
    {
        if (providerBilling && providerBilling.bcServiceLocationCode)
        {
            Juno.BillingHelper.BC._setSelectToValue($serviceLocationSelect, providerBilling.bcServiceLocationCode);
            return;
        }

        if (siteNo)
        {
            Juno.BillingHelper._siteBillingWrapper(siteNo, function (site)
            {
                if (site && site.bcServiceLocationCode)
                {
                    Juno.BillingHelper.BC._setSelectToValue($serviceLocationSelect, site.bcServiceLocationCode);
                }
                else
                {
                    Juno.BillingHelper._systemPreferenceWrapper("service_location_code", "", (function (clinicSLC)
                    {
                        if (clinicSLC)
                        {
                            Juno.BillingHelper.BC._setSelectToValue($serviceLocationSelect, clinicSLC);
                        }
                    }));
                }
            })
            return;
        }
        
        Juno.BillingHelper._systemPreferenceWrapper("service_location_code", "", (function (clinicSLC)
        {
            if (clinicSLC)
            {
                // Clinic SLC is stored as "key|value", we only need the key
                Juno.BillingHelper.BC._setSelectToValue($serviceLocationSelect, clinicSLC.split("|")[0]);
            }
        }));
    });
}


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

/**
 * create on change hook for provider select. This hook will adjust the rural retention code value,
 * to the default specified in the providers profile
 * @param context - juno context path
 * @param $providerSelect - the provider select to watch
 * @param $ruralRetentionCodeSelect - the rural retention code select to adjust
 */
Juno.BillingHelper.BC.initRuralRetentionCodeHook = function(context, $providerSelect, $ruralRetentionCodeSelect)
{
    Juno.BillingHelper.BC._localJunoInstance = context;

    Juno.BillingHelper.BC._updateRuralRetentionCode($providerSelect.val(), $ruralRetentionCodeSelect);
    $providerSelect.change(function()
    {
        Juno.BillingHelper.BC._updateRuralRetentionCode($providerSelect.val(), $ruralRetentionCodeSelect);
    });
}

/**
 * Create change hook for the provider and site selects.  Changing the value in either of these inputs will recalculate
 * the service location code to apply.
 *
 * @param context - juno context path
 * @param $providerSelect - the provider select to watch
 * @param $siteSelect - the site select to watch
 * @param $serviceLocationSelect - the service location select to adjust
 */
Juno.BillingHelper.BC.initServiceLocationCodeHook = function(context, $providerSelect, $siteSelect, $serviceLocationSelect)
{
    Juno.BillingHelper.BC._localJunoInstance = context;

    $providerSelect.change(function()
    {
        Juno.BillingHelper.BC._updateServiceLocationCode($providerSelect.val(),
            Juno.BillingHelper.BC._selectedSite($siteSelect),
            $serviceLocationSelect);
    });

    if ($siteSelect && $siteSelect.length > 0)
    {
        $siteSelect.change(function()
        {
            Juno.BillingHelper.BC._updateServiceLocationCode($providerSelect.val(),
                Juno.BillingHelper.BC._selectedSite($siteSelect),
                $serviceLocationSelect);
        });
    }
}
