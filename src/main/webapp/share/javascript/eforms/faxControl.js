if (typeof $ == "undefined")
{
    alert("The faxControl library requires jQuery. Please ensure that it is loaded first");
}

var faxControl = {
    _elements: {
        faxControlPlaceholder: "<br/>Fax Recipients:<br/><div id='faxForm'>Loading fax options..</div>",
        faxControlFaxButton: "<span>&nbsp;</span><input value='Fax' name='FaxButton' id='fax_button' class='faxButton' disabled type='button' onclick='faxControl.submitFax(false)'>",
        faxControlFaxSaveButton: "<span>&nbsp;</span><input value='Submit & Fax' name='FaxSaveButton' class='faxButton' id='faxSave_button' disabled type='button' onclick='faxControl.submitFax(true)'>",
        faxControlMemoryInput: "<input value='false' name='fax' id='fax' type='hidden' />",
        faxControlSaveHolder: "<input id='saveHolder' type='hidden' name='skipSave' value='\" + !save + \"' >",
        faxControlContainer: "<div id='faxControl'></div>"
    },

    _faxControlContainer: null,

    initialize: function ()
    {
        var containerPrepared = this._prepareFaxControlContainer();

        if (containerPrepared)
        {
            var demoNo = this._parseDemographicNumber("demographic_no", window.location.href) ||
                this._parseDemographicNumber("efmdemographic_no", $("form").first().attr('action'));

            $.ajax({
                url: "../eform/efmformfax_form.jsp",
                data: "demographicNo=" + demoNo
            }).then(function(data)
            {
                if (data && data.trim())
                {
                    faxControl._createFaxUI(data);
                    faxControl._announceDone();
                }
                else
                {
                    alert("Error loading fax control, please contact an administrator.");
                }
            });
        }
    },

    _prepareFaxControlContainer: function injectFaxControlForm()
    {
        this._faxControlContainer = $("#faxControl");

        if (!this._faxControlContainer.length)
        {
            var alternateSite = $(".DoNotPrint");

            if (alternateSite.length)
            {
                this._faxControlContainer = $(faxControl._elements.faxControlContainer);
                alternateSite.append(this._faxControlContainer);
            }
            else
            {
                alert("Missing placeholder please ensure a div with the id faxControl or a div with class DoNotPrint exists on the page.");
                return false;
            }
        }

        this._faxControlContainer.html(faxControl._elements.faxControlPlaceholder);
        return true;
    },

    _createFaxUI: function (data)
    {

        var faxContainer = faxControl._faxControlContainer;

        faxContainer.html(data);

        var submitButton = $("input[name='SubmitButton']");

        if (submitButton.length)
        {
            $(faxControl._elements.faxControlFaxButton).insertAfter(submitButton);
            $(faxControl._elements.faxControlFaxSaveButton).insertAfter(submitButton);
            $(faxControl._elements.faxControlMemoryInput).insertAfter(submitButton);
        }
        else
        {
            var doNotPrintArea = $(".DoNotPrint");
            var form = $("form").first();

            var alternateSite = doNotPrintArea || form;

            if (!alternateSite)
            {
                alert("Unable to find form or save button please check this is a proper eform.");
                return;
            }

            alternateSite.append($(faxControl._elements.faxControlFaxButton));
            alternateSite.append($(faxControl._elements.faxControlFaxSaveButton));
            alternateSite.append($(faxControl._elements.faxControlMemoryInput));

            var faxEnabled = ($("#faxControl_faxEnabled").val() === "true");
            if (!faxEnabled)
            {
                faxContainer.find(":input").prop('disabled', true);
                faxContainer.find(":button").prop('disabled', true);
            }
        }
    },

    _announceDone: function announceDone()
    {
        $(document).trigger("faxControlLoaded");
    },

    submitFax: function submitFax(save)
    {
        document.getElementById('fax').value = true;
        var form = $("form").first();

        var saveHolder = $("#saveHolder");

        if (!saveHolder.length)
        {
            form.append($(faxControlSaveHolder));
        }

        saveHolder = $("#saveHolder");
        saveHolder.val(!save);

        // unfortunately this variable is declared by the eform itself
        // so we can't completely encapsulate this module
        if (typeof(window.needToConfirm) !== "undefined")
        {
            window.needToConfirm = false;
        }

        if (!$("#Letter").length)
        {
            form.submit();
        }
        else
        {
            form = $("form[name='RichTextLetter']");
            document.getElementById('Letter').value = editControlContents('edit');
            form.submit();
        }

        document.getElementById('fax').value = false;
    },

    _parseDemographicNumber: function parseDemographicNumber(name, url)
    {
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
        var results = regex.exec(url);

        if (!results)
        {
            return "";
        }

        return results[1];
    }
};

$(document).ready(function()
{
	faxControl.initialize();
});