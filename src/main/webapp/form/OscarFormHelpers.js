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
Oscar.FormHelpers.alertDirtyBeforeLink = function alertDirtyBeforeLink($link, $form)
{
    $link.on('click', function(event) {
        if ($form.juno_isChanged())
        {
            event.preventDefault();
            $.when(Oscar.FormHelpers.createConfirmDirtyState()).done(function(confirmed)
            {
                if (confirmed)
                {
                    window.location.href = event.target.href;
                }
            });
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
Oscar.FormHelpers.alertDirtyBeforeClose = function alertDirtyBeforeClose($closeInput, $form)
{
    $closeInput.on('click', function(event) {
        if ($form.juno_isChanged())
        {
            $.when(Oscar.FormHelpers.createConfirmDirtyState()).done(function(confirmed)
            {
                if (confirmed)
                {
                    window.close();
                }
            });
        }
        else
        {
            window.close();
        }

        // Return false just in case the close button is a submit input.  We want to block it in all cases.
        return false;
    });
};

/**
 * Disable all link elements when called
 * @param $linkArray jQuery links to disable
 */
Oscar.FormHelpers.disableLinks = function disableLinks($linkArray)
{
    $.each($linkArray, function() {
        var $link = $(this);
        $link.off("click");
        $link.on("click", function() { return false; })
    });
};

/**
 * Create a dialog box that acts like a regular confirmation box.  This creates and binds to its own div, so there
 * are no structural dependencies on the form.
 *
 * @returns Promise, resolves when dialog option is selected.
 */
Oscar.FormHelpers.createConfirmDirtyState = function createConfirmDirtyState()
{
    var defer = $.Deferred();
    var confirmed = false;

    $('<div>')
        .html(Oscar.FormHelpers.DirtyStateConfirmMessage)
        .dialog({
                resizeable: false,
                draggable: false,
                modal: true,
                closeOnEscape: true,
                create: function (event) {
                    $(event.target).parent().css({ 'position': 'fixed'});
                },
                width: '30%',
                title: Oscar.FormHelpers.DirtyStateConfirmTitle,
                buttons: {
                    'Yes': function() {
                        confirmed = true;
                        $(this).dialog('close');
                    },
                    'No': function() {
                        $(this).dialog('close');
                    }
                },
                close: function() {
                    defer.resolve(confirmed);
                    $(this).dialog('destroy').remove();
                    }
        })
        .parent().position({
            my: 'center',
            at: 'center top+10%',
            of: 'body'
        });

    return defer.promise();
};

/**
 * Any inputs used as checkboxes do not submit their "value" if they are unchecked
 * To ensure that an unchecked checkbox is properly saved for future usage of the form,
 * create a hidden input with a blank value for any unchecked checkbox input on the page.
 *
 * Call this as part of any onSubmit procedures as a last-ditch effort to get inputs to submit properly.
 */

Oscar.FormHelpers.forceSubmitUncheckedCheckboxes = function forceSubmitUncheckedCheckboxes()
{
    var checkboxes = $("input:checkbox");
    if (checkboxes !== null)
    {
        for (var i = 0; i < checkboxes.length; i++)
        {
            if (!checkboxes[i].checked)
            {
                $('<input>').attr({
                    type: 'hidden',
                    id: checkboxes[i].name + '' + i,
                    name: checkboxes[i].name,
                    value: ''
                }).appendTo('form');
            }
        }
    }
};



Oscar.FormHelpers.DirtyStateConfirmMessage = 'Are you sure you want to navigate away from this page without saving your changes?';
Oscar.FormHelpers.DirtyStateConfirmTitle = 'Unsaved Changes';

