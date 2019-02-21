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
 * Adds a click event handler to all specified link elements when the save button is clicked. The handler disables the
 * link from firing.  The links will fire again normally when the page is refreshed automatically after the data is saved/
 * @param $saveInput jQuery save input
 * @param $linkArray jQuery links to disable
 */
Oscar.FormHelpers.safeSave = function disableLinksOnSave($saveInput, $linkArray)
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

/**
 * Prompt a confirmation dialog if the page is dirty and the window is closed.
 *
 * NOTE:  Most modern browsers will recognize the confirm prompt, but will override it with their own built-in prompt
 * due to safety/user experience issues.  Therefore, the message displayed will probably not be the one coded below.
 * Regardless, the functionality is equivalent.
 *
 * Overrides tested:
 * Firefox 61.0, Chrome 69.0 - Prompt on dirty state only
 * Safari 9.1.3 - Prompt on both clean and dirty states
 */
Oscar.FormHelpers.safeWindow = function alertDirtyBeforeWindowClose()
{
    console.log("blah blah");
    window.onbeforeunload = function() {
        // .juno_isChanged will not necessarily work here.  We can edit a form field, then click the window close
        // without clicking on another element.  Without the blur event, the jQuery change handler will not fire.
        // The browser itself is capable of detecting the dirty state properly in 2/3 of cases (see method doc),
        // so we will just let it do the work for us.

        return confirm('Are you sure you want to navigate away from this page without saving your changes?');
    }
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

