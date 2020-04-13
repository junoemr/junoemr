"use strict";

// dependencies: JQuery

var Juno = window.Juno || {};
Juno.Admin = Juno.Admin || {};
Juno.Admin.Sites = Juno.Admin.Sites || {};
Juno.Admin.Sites.SiteDetails = Juno.Admin.Sites.SiteDetails || {};

/**
 * Callback function when a province is chosen.
 * @param prov {String} Two letter Postal Abbreviation or ISO 3166-2:CA province code
 */
Juno.Admin.Sites.SiteDetails.onChooseProvince = function onChooseProvince(prov)
{
    var allProvincialElements = document.getElementsByClassName("province-specific");

    for (var element of allProvincialElements)
    {
        element.style.display = "none";
    }

    var provClass = "province-specific " + prov.toLowerCase();
    var provElements = document.getElementsByClassName(provClass);

    for (var element of provElements)
    {
        element.style.display = "table-row";
    }
};

/**
 * Set the background color of the color field to whatever value was typed in the input
 * @param $colorField jQuery element corresponding to the color field
 */
Juno.Admin.Sites.SiteDetails.onChooseColor = function onChooseColor($colorField)
{
    if ($colorField && $colorField.val())
    {
        console.log("changing");
        $colorField.css("backgroundColor", $colorField.val());
    }
};

/**
 * Set initial state and bind callbacks.
 */
Juno.Admin.Sites.SiteDetails.onReady = function onReady()
{
    var provinceSelect = document.getElementById('province-select');

    Juno.Admin.Sites.SiteDetails.onChooseProvince(provinceSelect.value);

    provinceSelect.addEventListener('change', function ()
    {
        Juno.Admin.Sites.SiteDetails.onChooseProvince(provinceSelect.value);
    });

    var saveButton = document.getElementById('save-button');
};

/**
 * Bind handlers when document is ready
 */
$(document).ready(Juno.Admin.Sites.SiteDetails.onReady);
