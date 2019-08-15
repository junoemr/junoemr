(function ($) {
    $.fn.juno_trackIsChanged = function() {
        $(this).data("_juno_changed", false);
        $(":input", this).each(_addChangeListener);
        return this;
    };

    $.fn.juno_isChanged = function() {
        return this.data("_juno_changed");
    };

    function _addChangeListener(index, element)
    {
        $(this).change(function () {
        $(this.form).data("_juno_changed", true);
        })
    }
} (jQuery));