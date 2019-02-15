"use strict";

var Oscar = Oscar || {};

if (!Oscar.FormHelpers) {
    Oscar.FormHelpers = {};
}

/**
 * Prompt a confirmation dialog if a link is pressed and the form is in a dirty state.
 *
 * @param $link jQuery link element
 * @param $form jQuery form element
 * @return false if form is dirty and the confirmation returns false
 */
Oscar.FormHelpers.safeLink = function promptSaveBeforeLink($link, $form)
{
    $link.on("click", function(event) {
        if ($form.juno_isChanged() && !confirm('Are you sure you want to navigate away from this page without saving your changes?'))
        {
            event.preventDefault();
        }
    })
};

/**
 * Prompt a confirmation dialog if the close button is pressed and the form is in a dirty state.
 *
 * @param $closeInput jQuery link element
 * @param $form jQuery form element
 * @return false
 */
Oscar.FormHelpers.safeClose = function promptSaveBeforeClose($closeInput, $form)
{
    $closeInput.on("click", function() {
        if ($form.juno_isChanged() && confirm('Are you sure you want to navigate away from this page without saving your changes?'))
        {
            window.close();
        }

        return false;
    });
};

/**
 * Adds a click event handler to all specified link elements when the save button is clicked. The handler disables the
 * link from firing.  The links will fire again normally when the page is refreshed automatically after the data is saved/
 * @param $saveInput jQuery save input
 * @param $linkArray jQuery links to disable
 */
Oscar.FormHelpers.safeSave = function DisableLinksOnSave($saveInput, $linkArray)
{
    $saveInput.on("click",function() {
        $.each($linkArray, function() {
            var $link = $(this);
            $link.on("click", function (event)
            {
                event.preventDefault();
            })
        })
    })
};
