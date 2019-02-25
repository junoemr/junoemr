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
Oscar.FormHelpers.safeLink = function alertDirtyBeforeLink($link, $form)
{
    $link.on('click', function(event) {
        if ($form.juno_isChanged())
        {
            event.preventDefault();
            $.when(Oscar.FormHelpers.createConfirm()).done(function(confirmed)
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
Oscar.FormHelpers.safeClose = function alertDirtyBeforeClose($closeInput, $form)
{
    $closeInput.on('click', function(event) {
        if ($form.juno_isChanged())
        {
            $.when(Oscar.FormHelpers.createConfirm()).done(function(confirmed)
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
Oscar.FormHelpers.createConfirm = function dirtyStateConfirm()
{
    var defer = $.Deferred();
    var confirmed = false;

    $('<div>Are you sure you want to navigate away from this page without saving your changes?</div>').dialog(
        {
            resizeable: false,
            draggable: false,
            modal: true,
            closeOnEscape: true,
            create: function (event) {
                $(event.target).parent().css({ 'position': 'fixed'});
            },
            width: '30%',
            title: 'Unsaved Changes',
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

        }).parent().position(
        {
            my: 'center',
            at: 'center top+10%',
            of: 'body'
        });

    return defer.promise();
};

