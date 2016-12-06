// change oceanHost value below for development testing
//window.oceanHost = "https://localhost:8443";
window.oceanHost = "https://ocean.cognisantmd.com";
jQuery("#cppBoxes").append("<div id='ocean_div' style='width: 100%; display: none; font-size: 11px;'>Sorry, the Ocean toolbar is currently unavailable.</div>");
jQuery.ajax({
		url: window.oceanHost + "/robots.txt",
		cache: true,
		dataType: "text",
		success: function() {
				jQuery.ajax({
						url: window.oceanHost + "/oscar_resources/OscarToolbar.js",
						cache: true,
						dataType: "script"
				});
		},
		error: function(jqXHR, textStatus, error) {
				console.log("Ocean toolbar error: " + textStatus + ", " + error);
				jQuery("#ocean_div").show().css("padding", "5px").
						css("text-align", "center");
		}
});
