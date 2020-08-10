"use strict";

// The following runs inside an iFrame, Do not use jQuery because you will
// lose reference to the document after it renders in the frame.

var Juno = window.Juno || {};
Juno.Admin = Juno.Admin || {};
Juno.Admin.Provider = Juno.Admin.Provider || {};
Juno.Admin.Provider.Profile = Juno.Admin.Provider.Profile || {};

Juno.Admin.Provider.Profile.toggleBCPElement = function toggleBCPElement(siteElement, siteBCPElement)
{
    if (siteElement.checked)
    {
        siteBCPElement.disabled = false;
        siteBCPElement.parentNode.style.display = "table-cell";
    }
    else
    {
        siteBCPElement.checked = false;
        siteBCPElement.disabled = true;
        siteBCPElement.parentNode.style.display = "none";
    }
};

Juno.Admin.Provider.Profile.bindSiteHandler = function bindSiteHandler(siteElement)
{
    var siteId = siteElement.value;
    var siteBCPElement = document.getElementById('bcp-site-' + siteId);

    // Only bind the site handler if there's a BCP element associated with it.
    // Non-BC sites will not have one.
    if (siteBCPElement)
    {
        siteElement.addEventListener('change', function()
        {
            Juno.Admin.Provider.Profile.toggleBCPElement(siteElement, siteBCPElement);
        });

        Juno.Admin.Provider.Profile.toggleBCPElement(siteElement, siteBCPElement);
    }
};

Juno.Admin.Provider.Profile.initSiteSelectHandler = function initSiteSelectHandler()
{
    var siteInputs = document.getElementsByName('sites');

    for (var element of siteInputs)
    {
        Juno.Admin.Provider.Profile.bindSiteHandler(element);
    }
};