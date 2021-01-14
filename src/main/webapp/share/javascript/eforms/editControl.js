/*
    editControl - a WYSIWYG edit control using iFrames and designMode
    
    Modified by Selwyn Wan (March 25, 2015)
    Version 1.3a
    	- Altered the browser detect code to set a single global variable for the detection of IE to be called once at load time.
   			This allows for much more complicated detection code to be executed one time during load away from the places where the
   			IEness of the browser is used.
    	- Fixed the browser detect code that misidentified FireFox as IE post FF 17.0.11 ESR
    	- Fixed OnLoad handler for loading of templates as the handler was being set after the load happened
    	- Fixed various errors in code syntax that was causing compilation errors
    	- Altered the bracketing style to improve readability of the code
    	- Altered the usercss function to now operate with any browser that supports the feature
    	- Altered the viewsource function to now operate with any browser that supports the feature
    
    Copyright (C) 2009-2010 Peter Hutten-Czapski
    Version 1.3 now about 500 lines of code
        NEW in 0.2 button styles, links, select box
        NEW in 0.3 help, date, rule, select all, and clean functions
        NEW in 0.4 code completely rewritten, more functions including images and
            now more modular and can support IE, including spelling for IE under 300 lines
        NEW in 0.5 template loading with support for placeholder form letter fields
        NEW in 0.8 minor code cleanup and bugfixes
        NEW in 1.1 first commit to cvs
        NEW in 1.2 bugfix for button style mouse behavior and 5 more buttons/functions
        NEW in 1.3 support for IE template loading, cut, copy, paste buttons/functions
    * Requirements: DesignMode and other Dom 2 methods
    * Mozilla 1.3+ IE 5.5+ Netscape 6+ Opera 9+ Konqueror 3.5.7+ Safari 1.3+ Chrome
    * designed for and tested on Firefox 2/3.  Tested on Opera 10, Chrome 4 and IE 6/7

    This is a simplistic emulation of Xinha and TinyMCE javascript texteditors

    Released under the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 2.1 of the License,
    or (at your option) any later version.

    * * *
    *
    * USAGE: Put the following snippit in your webpage where you want the control to appear
    *        Put editControl.js, blank.html and image files (buttons) in the same directory as the webpage
    *
    * * *
        <script language="javascript" type="text/javascript" src="editControl.js"></script>
		<script language="javascript">

		//some of the optional configuration variables with their defaults go here
		// cfg_width = '700';					//editor control width in pixels
		// cfg_height = '400';					//editor control height in pixels
		// cfg_layout = '[select-block]|[bold][italic]|[unordered][ordered][rule]|[undo][redo]|[indent][outdent][select-all][clean]|[clock][help]<br />[edit-area]';
						// [select-block] an option list for paragraph and header styles
						// [select-face] an option list for selecting font face
						// [select-size] an option list for selecting font size
						// [select-template] an option list for selecting base content and style
						// | a cosmetic seperator bar
						// [bold] a button that toggles bold of the selected text
						// [italic] a button that toggles emphasis text
					// [underlined] a button that toggles underlined text
						// [strike] a button that toggles strike trough text
						// [subscript] a button that toggles subscript text
						// [superscript] a button that toggles superscript text
						// [text-colour] a button that applies text colour
						// [hilight] a button that applies text high lighting colour
						// [left] a button that left justifies text
						// [center] a button that center justifies text
						// [full] a button that fully justifies text
						// [right] a button that right justifies text
						// [unordered]a button that creates a bulleted list
						// [ordered] a button that creates an ordered list
						// [rule]a button that creates a horizontal rule
						// [undo]a button that undoes the last action(s)
						// [redo]a button that redoes the last action(s)
						// [heading1] inserts a heading IF select-block is not available
						// [indent]a button that indents the text
						// [outdent]a button that outdents the text
						// [select-all]a button that selects all text
						// [clean]a button that removes font formatting from all selected text
						// [table]a button that inserts a table
						// [link]a button that inserts a URL of a link
						// [image]a button that inserts an image
						// [date] a button that adds the current date to the form
						// [clock] a button that adds the current time to the form
						// [help] a button that loads a help window
						// [spell] a button that invokes a spell checker for IE
						// [cut] a button that cuts the selected text for IE
						// [copy] a button that copies to windows clipboard for IE
						// [paste] a button that pastes from the windows clipboard for IE
						// <br /> an embedded html element; you can add them freely in the layout
						// [edit-area] the location of the iFrame that contains the editor

		insertEditControl(); // Initialise the edit control

		// To set the HTML contents of this edit control use JavaScript to call: seteditControlContents(IdString,html)
		//  - e.g. for putting "bar" into a control called "foo", call seteditControlContents("foo","<p>bar</p>");

		// To retrieve the HTML contents of this edit control call: editControlContents(string)
		//   - e.g. for a control called "foo", call editControlContents("foo");
		</script>
*/
//GLOBALS
var cfg_layout = '[select-block]|[bold][italic]|[unordered][ordered][rule]|[undo][redo]|[indent][outdent][select-all][clean]|[clock][spell][help]<br />[edit-area]';
var cfg_formatblock = '<option value="">&mdash; format &mdash;</option>  <option value="<p>">Paragraph</option>  <option value="<h1>">Heading 1</option>  <option value="<h2>">Heading 2 <H2></option>  <option value="<h3>">Heading 3 <H3></option>  <option value="<h4>">Heading 4 <H4></option>  <option value="<h5>">Heading 5 <H5></option>  <option value="<h6>">Heading 6 <H6></option>  </select>';
var cfg_formatface = '<option value="">&mdash; font face &mdash;</option>  <option value="Arial,Helvetica,sans-serif">Arial</option> <option value="Courier">Courier</option> <option value="Times New Roman">Times</option> </select>';
var cfg_formatfontsize = '<option value="">&mdash; font size &mdash;</option>  <option value="1">1</option>  <option value="2">2</option> <option value="3">3</option> <option value="4">4</option> <option value="5">5</option> <option value="6">6</option> <option value="7">7</option> </select>';
var cfg_formattemplate = '<option value="">&mdash; template &mdash;</option>  <option value="blank">blank</option>  </select>';
var cfg_isrc = '';  				// path to icons degrades to text buttons if icons not found
var cfg_filesrc = '';				// path to blank.html and editor_help.html
var cfg_template = 'blank.html';	// style and content template of the editor's iframe itself.
var cfg_width = '700';				// editor control width in pixels
var cfg_height = '400';				// editor control height in pixels
var cfg_editorname ="anEditControl";		// handle for the editor control itself
var cfg_bstyle = 'width:24px;height:24px;border: solid 2px #ccccff; background-color: #ccccff;'; 	//the CSS of the button elements
var cfg_boutstyle = 'solid 2px #ccccff'; 	//the CSS of the button elements om mouse out
var cfg_sstyle = 'vertical-align: top; height:24px;'; //the CSS of the option select box.  Selects will take font and background but not border.
var cfg_sepstyle = 'width:6px;height:24px;border: solid 2px #ccccff; background-color: #ccccff;';	//the CSS of the seperator icon
	
var isIE = false;

function browserDetect()
{
	if (navigator.userAgent.indexOf('MSIE') != -1 || (navigator.userAgent.indexOf('Trident') != -1 && navigator.userAgent.indexOf('rv') != 01))
		isIE = true;
}

function insertEditControl() 
{
	browserDetect();
	
	// The main initialising function which writes the edit control as per passed variables
	// ...OR... if it fails, degrades nicely by supplying a text area with the same ID (cfg_editorname)

	// FIRST BUILD BUTTONS WITH USEFUL ATTRIUBUTES
	// Mozilla requires the title attribute for tool tips, and that works in IE as well
	// The ID matches a execCommand argument and carries the action associated with the button
	// when the action needs a value cmdValue="promptUser" will prompt the user with the promptText attribute
	// the class="editControlButton" identifies the elements that will have button function
	//   -these will change appearance on mouse events and will trigger on mouse click a default action

	var boldButton = '<input type="image" src="' + cfg_isrc + 'b.png" value="Bold" alt="Bold" title="Bold"  name="' + cfg_editorname + '" class="editControlButton" id="bold" style="'+ cfg_bstyle+ '">';
	var italicButton = '<input type="image" src="' + cfg_isrc + 'i.png" value="Italic" alt="Italic" title="Italic" name="' + cfg_editorname + '" class="editControlButton" id="italic" style="'+ cfg_bstyle+ '">';
	var underlinedButton = '<input type="image" src="' + cfg_isrc + 'u.png" value="Underlined" alt="Underlined" title="Underline" name="' + cfg_editorname + '" class="editControlButton" id="underline" style="'+ cfg_bstyle+ '">';
	var strikethroughButton = '<input type="image" src="' + cfg_isrc + 'strike_trough.png" value="Strike Through" alt="Strike Through" title="Strike Through" name="' + cfg_editorname + '" class="editControlButton" id="strikethrough" style="'+ cfg_bstyle+ '">';
	var superscriptButton = '<input type="image" src="' + cfg_isrc + 'superscript.png" value="Superscript" alt="Superscript" title="Superscript" name="' + cfg_editorname + '" class="editControlButton" id="superscript" style="'+ cfg_bstyle+ '">';
	var subscriptButton = '<input type="image" src="' + cfg_isrc + 'subscript.png" value="Subscript" alt="Subscript" title="Subscript" name="' + cfg_editorname + '" class="editControlButton" id="subscript" style="'+ cfg_bstyle+ '">';
	var leftButton = '<input type="image" src="' + cfg_isrc + 'format-justify-left.png" value="Left" alt="Left" title="Left"  name="' + cfg_editorname + '" class="editControlButton" id="justifyleft" style="'+ cfg_bstyle+ '">';
	var centerButton = '<input type="image" src="' + cfg_isrc + 'format-justify-center.png" value="Center" alt="Center" title="Center"  name="' + cfg_editorname + '" class="editControlButton" id="justifycenter" style="'+ cfg_bstyle+ '">';
	var fullButton = '<input type="image" src="' + cfg_isrc + 'format-justify-full.png" value="Full" alt="Full" title="Full"  name="' + cfg_editorname + '" class="editControlButton" id="justifyfull" style="'+ cfg_bstyle+ '">';
	var rightButton = '<input type="image" src="' + cfg_isrc + 'format-justify-right.png" value="Right" alt="Right" title="Right"  name="' + cfg_editorname + '" class="editControlButton" id="justifyright" style="'+ cfg_bstyle+ '">';
	var selectBlock = '<select name="' + cfg_editorname + '" id="formatblock" onchange="Select(this.id);" style="' + cfg_sstyle +'">'+ cfg_formatblock ;
	var selectFace = '<select name="' + cfg_editorname + '" id="fontname" onchange="Select(this.id);" style="' + cfg_sstyle +'">'+ cfg_formatface ;
	var selectSize = '<select name="' + cfg_editorname + '" id="fontsize" onchange="Select(this.id);" style="' + cfg_sstyle +'">'+ cfg_formatfontsize ;
	var selectTemplate = '<select name="' + cfg_editorname + '" id="template" onchange="loadTemplate(this.id);" style="' + cfg_sstyle +'">'+ cfg_formattemplate ;
	var undoButton = '<input type="image" src="' + cfg_isrc + 'undo.png" value="Undo" alt="Undo" title="Undo" name="' + cfg_editorname + '" id="undo" class="editControlButton" style="'+ cfg_bstyle+ '">';
	var redoButton = '<input type="image" src="' + cfg_isrc + 'redo.png" value="Redo" alt="Redo" title="Redo" name="' + cfg_editorname + '" id="redo" class="editControlButton" style="'+ cfg_bstyle+ '">';
	var selectAllButton = '<input type="image" src="' + cfg_isrc + 'all.png" value="Select all" alt="Select all" title="Select All" name="' + cfg_editorname + '" id="selectall" class="editControlButton" style="'+ cfg_bstyle+ '">';
	var insertImageButton = '<input type="image" src="' + cfg_isrc + 'img.png" value="Insert image" alt="Insert image" title="Insert Image" name="' + cfg_editorname + '" id="insertimage" class="editControlButton" cmdValue="promptUser" promptText="URL of image?" style="'+ cfg_bstyle+ '">';
	var insertLinkButton = '<input type="image" src="' + cfg_isrc + 'a.png" value="Insert link" alt="Insert link" title="Insert Link" name="' + cfg_editorname + '" id="createlink" class="editControlButton" cmdValue="promptUser" promptText="URL of link?[http://www.srpc.ca]" style="'+ cfg_bstyle+ '">';
	var textcolourButton = '<input type="image" src="' + cfg_isrc + 'text_color.png" value="Text Colour" alt="Text Colour" title="Text Colour" name="' + cfg_editorname + '" id="forecolor" class="editControlButton" cmdValue="promptUser" promptText="Text Colour?[red]" style="'+ cfg_bstyle+ '">';
	var hilightcolourButton = '<input type="image" src="' + cfg_isrc + 'select_background_color.png" value="Hilight" alt="Hilight" title="Hilight" name="' + cfg_editorname + '" id="hilitecolor" class="editControlButton" cmdValue="promptUser" promptText="Hilight Colour?[yellow]" style="'+ cfg_bstyle+ '">';
	var unorderedlistButton = '<input type="image" src="' + cfg_isrc + 'ul.png" value="Unordered" alt="Unordered List" title="Insert Bullet List" name="' + cfg_editorname + '" class="editControlButton" id="insertunorderedlist" style="'+ cfg_bstyle+ '">';
	var orderedlistButton = '<input type="image" src="' + cfg_isrc + 'ol.png" value="Ordered" alt="Numbered List" title="Insert Numbered List" name="' + cfg_editorname + '" class="editControlButton" id="insertorderedlist" style="'+ cfg_bstyle+ '">';
	var ruleButton = '<input type="image" src="' + cfg_isrc + 'hr.png" value="Rule" alt="Horizontal Rule" title="Insert Horizontal Rule" name="' + cfg_editorname + '" class="editControlButton" id="inserthorizontalrule" style="'+ cfg_bstyle+ '">';
	var cleanButton = '<input type="image" src="' + cfg_isrc + 'clean.png" value="Unformat" alt="Remove Formatting" title="Remove Formatting" name="' + cfg_editorname + '" class="editControlButton" id="removeformat" style="'+ cfg_bstyle+ '">';
	var indentButton = '<input type="image" src="' + cfg_isrc + 'indent.png" value="Indent" alt="Indent" title="Indent" name="' + cfg_editorname + '" class="editControlButton" id="indent" style="'+ cfg_bstyle+ '">';
	var outdentButton = '<input type="image" src="' + cfg_isrc + 'outdent.png" value="Outdent" alt="Indent Less" title="Indent Less" name="' + cfg_editorname + '" class="editControlButton" id="outdent" style="'+ cfg_bstyle+ '">';
	var insertHeading1Button = '<input type="image" src="' + cfg_isrc + 'h1.png" value="Heading" alt="Headings1" title="Heading 1" name="' + cfg_editorname + '" id="formatblock" class="editControlButton" cmdValue="<H1>" style="'+ cfg_bstyle+ '">';
	var tableButton = '<input type="image" src="' + cfg_isrc + 'table.png" value="Table" alt="Table" title="Insert Table" name="' + cfg_editorname + '" class="editControlButton" id="table" cmdValue="table" style="'+ cfg_bstyle+ '">';
	var helpButton = '<input type="image" src="' + cfg_isrc + 'help.png" value="Help" alt="Help" title="Editor Help" name="' + cfg_editorname + '" class="editControlButton" id="help" cmdValue="help" style="'+ cfg_bstyle+ '">';
	var clockButton = '<input type="image" src="' + cfg_isrc + 'clock.png" value="Clock" alt="Insert Time" title="Time" name="' + cfg_editorname + '" class="editControlButton" id="insertHTML" cmdValue="clock" style="'+ cfg_bstyle+ '">';
	var dateButton = '<input type="image" src="' + cfg_isrc + 'office-calendar.png" value="Date" alt="Date" title="Date d/m/y" name="' + cfg_editorname + '" class="editControlButton" id="insertHTML" cmdValue="date" style="'+ cfg_bstyle+ '">';
	var ieSpellButton='<input type="image" src="' + cfg_isrc + 'iespell.png" value="ieSpell" alt="Spell" title="Check Spelling" name="' + cfg_editorname + '" class="editControlButton" id="spell" cmdValue="spell" style="'+ cfg_bstyle+ '">';
	var cutButton = '<input type="image" src="' + cfg_isrc + 'edit-cut.png" value="Cut" alt="Cut" title="Cut" name="' + cfg_editorname + '" class="editControlButton" id="cut" style="'+ cfg_bstyle+ '">';
	var copyButton = '<input type="image" src="' + cfg_isrc + 'edit-copy.png" value="Copy" alt="Copy" title="Copy" name="' + cfg_editorname + '" class="editControlButton" id="copy" style="'+ cfg_bstyle+ '">';
	var pasteButton = '<input type="image" src="' + cfg_isrc + 'edit-paste.png" value="Paste" alt="Paste" title="Paste" name="' + cfg_editorname + '" class="editControlButton" id="paste" style="'+ cfg_bstyle+ '">';

	var separator = '<input type="image" src="' + cfg_isrc + 'separator.png" value="|" name="' + cfg_editorname + '" style="'+ cfg_sepstyle+ '">';
	var editControl = '<iframe src="'+cfg_filesrc+cfg_template+'" id="' + cfg_editorname + '" \n style="width:' + cfg_width + 'px; height:' + cfg_height + 'px; border-style:inset; border-width:thin;" frameborder="0px"></iframe>';

	// SECOND GET THE LAYOUT STRING PASSED AND REPLACE IT WITH THE BUTTONS AS REQUESTED
	var editControlHTML = cfg_layout;
	
	if (editControlHTML == "[all]")
		editControlHTML = "<div id=control>[select-block][select-face][select-size][select-template]|[bold][italic][underlined][unordered][ordered][rule]|[undo][redo]|[cut][copy][paste]|[left][center][full][right]|[indent][outdent][select-all][clean]|[image][link]|[clock][date][spell][help]</div>[edit-area]";
	
	if ((editControlHTML.indexOf("select-block") > -1) && (editControlHTML.indexOf("heading1") > -1))
		editControlHTML = editControlHTML.replace("[heading1]", ""); //only one id=formatblock tolerated
		
	editControlHTML = editControlHTML.replace("[bold]", boldButton);
	editControlHTML = editControlHTML.replace("[italic]", italicButton);
	editControlHTML = editControlHTML.replace("[underlined]", underlinedButton);
	editControlHTML = editControlHTML.replace("[strike]", strikethroughButton);
	editControlHTML = editControlHTML.replace("[superscript]", superscriptButton);
	editControlHTML = editControlHTML.replace("[subscript]", subscriptButton);
	editControlHTML = editControlHTML.replace("[left]", leftButton);
	editControlHTML = editControlHTML.replace("[center]", centerButton);
	editControlHTML = editControlHTML.replace("[full]", fullButton);
	editControlHTML = editControlHTML.replace("[right]", rightButton);
	editControlHTML = editControlHTML.replace("[select-face]", selectFace);
	editControlHTML = editControlHTML.replace("[select-size]", selectSize);
	editControlHTML = editControlHTML.replace("[undo]", undoButton);
	editControlHTML = editControlHTML.replace("[redo]", redoButton);
	editControlHTML = editControlHTML.replace("[select-all]", selectAllButton);
	editControlHTML = editControlHTML.replace("[text-colour]", textcolourButton);
	editControlHTML = editControlHTML.replace("[hilight]", hilightcolourButton);
	editControlHTML = editControlHTML.replace("[image]", insertImageButton);
	editControlHTML = editControlHTML.replace("[link]", insertLinkButton);
	editControlHTML = editControlHTML.replace("[unordered]", unorderedlistButton);
	editControlHTML = editControlHTML.replace("[ordered]", orderedlistButton);
	editControlHTML = editControlHTML.replace("[rule]", ruleButton);
	editControlHTML = editControlHTML.replace("[clean]", cleanButton);
	editControlHTML = editControlHTML.replace("[indent]", indentButton);
	editControlHTML = editControlHTML.replace("[outdent]", outdentButton);
	editControlHTML = editControlHTML.replace("[select-block]", selectBlock);
	editControlHTML = editControlHTML.replace("[heading1]", insertHeading1Button);
	editControlHTML = editControlHTML.replace("[table]", tableButton);
	editControlHTML = editControlHTML.replace("[help]", helpButton);
   	editControlHTML = editControlHTML.replace("[clock]", clockButton);
   	editControlHTML = editControlHTML.replace("[date]", dateButton);
	editControlHTML = editControlHTML.replace("[|]", separator);
	editControlHTML = editControlHTML.replace("[edit-area]", editControl);
	editControlHTML = editControlHTML.replace("[select-template]", selectTemplate);

	var agent=navigator.userAgent.toLowerCase();
	if (isIE && (agent.indexOf("opera") == -1)) 
	{
		//Browser is Microsoft Internet Explorer : Can load browser specific code
		editControlHTML = editControlHTML.replace("[spell]", ieSpellButton);
		editControlHTML = editControlHTML.replace("[cut]", cutButton);
		editControlHTML = editControlHTML.replace("[copy]", copyButton);
		editControlHTML = editControlHTML.replace("[paste]", pasteButton);
	} 
	else 
	{
		//spell is superfluous for FF and cut/copy/paste are protected
		editControlHTML = editControlHTML.replace("[spell]", "");
		editControlHTML = editControlHTML.replace("[cut]", "");
		editControlHTML = editControlHTML.replace("[copy]", "");
		editControlHTML = editControlHTML.replace("[paste]", "");
	}
	// THIRD WRITE THE EDIT CONTROL TO THE WEB PAGE
	if (document.designMode) 
	{
		document.write(editControlHTML);
		InitToolbarButtons(cfg_editorname);
	} 
	else 
	{
		// create a normal <textarea> if document.designMode does not exist
		//alert("Design mode is not supported by your browser \n- reverting to classic mode");
		document.write('<textarea id="' + cfg_editorname + '" style="width:' + cfg_width + '; height:' + editControlHeight + 'px; ' + editControlStyle + '"></textarea>');
	}
}

function editControlContents(editorname) 
{
	var retStr = "";
	// this function retrieves the HTML contents of the edit control "editorname"
	if (document.designMode) 
	{
		// Explorer reformats HTML during document.write() removing quotes on element ID names
		// so we need to address Explorer elements as window[elementID]
		if (isIE) //(window[editorname]) 
		    retStr = window[editorname].document.body.innerHTML;
		else		
			retStr = document.getElementById(editorname).contentWindow.document.body.innerHTML;
	} 
	else 
		retStr = document.getElementById(editorname).value; // play nice and at least return the value from the <textarea> if document.designMode does not exist
		
	return retStr;
}

function seteditControlContents(editorname, value){
	// this function sets the HTML contents of the edit control "editorname" to "value"
    if (document.designMode) 
    {
		if (isIE) //(window[editorname]) 
		    window[editorname].document.body.innerHTML = value; //if browser supports M$ conventions
		else 
		    document.getElementById(editorname).contentWindow.document.body.innerHTML = value;
	} 
	else 
		document.getElementById(cfg_editorname).value = value; // play nice and at least set the value to the <textarea> if document.designMode does not exist
}


function InitToolbarButtons(cfg_editorname) {
	// make the buttons pretty and functional
	// code modeled after the Mozilla Rich Text Editor Demo http://www.mozilla.org/editor/midasdemo/
	var kids = document.getElementsByTagName('input');

	for (var i=0; i < kids.length; i++) 
	{
		if (kids[i].className == "editControlButton" && kids[i].name == cfg_editorname) 
		{
			kids[i].onmouseover = tbuttonMouseOver;
			kids[i].onmouseout = tbuttonMouseOut;
			kids[i].onmouseup = tbuttonMouseUp;
			kids[i].onmousedown = tbuttonMouseDown;
			kids[i].onclick = tbuttonOnClick;
		}
	}
}

function tbuttonMouseDown(e) 
{
	// when the mouse button is held down use CSS to
	// inset the border by 2 pixels (darken over and the left border)
  	this.style.border="inset 2px";
	// prevent default event (i.e. don't remove focus from text area)
	var evt = e ? e : window.evcfg_editornameent;
	
	if (evt.returnValue) 
		evt.returnValue = false;
	else if (evt.preventDefault) 
		evt.preventDefault();
	else 
		return false;
}

function tbuttonMouseOver() 
{
	// when the mouse pointer is over the button
	// outset the border by 2 pixels (darken under and the right border)
	this.style.border="outset 2px";
}

function tbuttonMouseOut() 
{
	// events for mouseOut on buttons #FFFFFF = white
  	this.style.border=cfg_boutstyle;
}

function tbuttonMouseUp() 
{
	// events for mouseUp on buttons
  	this.style.border="outset 2px";
}

function Select(selectname)
{
  	var cursel = document.getElementById(selectname).selectedIndex;
  	if (cursel != 0) 
  	{ // First one is a label
    	var selected = document.getElementById(selectname).options[cursel].value;
		if (isIE) // if (window[cfg_editorname]) 
    		window[cfg_editorname].document.execCommand(selectname, false, selected); //if browser supports M$ conventions
		else 
			document.getElementById(cfg_editorname).contentWindow.document.execCommand(selectname, false, selected); 
		
    	document.getElementById(selectname).selectedIndex = 0;
  		document.getElementById(cfg_editorname).contentWindow.focus();
  	}
}

function loadTemplate(selectname)
{
	//change the iframe src to that selected in the template select box
	//TODO-legacy fix the eventlistener! in the meantime just set the dirty flag
	setDirtyFlag();
  	var cursel = document.getElementById(selectname).selectedIndex;
  	
  	if (cursel != 0) 
  	{ // First one is a label
		var obj = document.getElementById(cfg_editorname);
		
		obj.onload = function() 
		{ 
			parseTemplate(); 
		}
  	
    	var selected = document.getElementById(selectname).options[cursel].value;
		//document.getElementById(cfg_editorname).src = cfg_filesrc + selected + '.html' ; //FF != IE
		window.frames[0].location = cfg_filesrc + selected + '.html' ; //FF & IE ***ASSUMES 1 iframe!
			
		if (selected !='blank'){document.getElementById('subject').value = selected;}
    		document.getElementById('template').selectedIndex = 0;
    		
		//need to ensure that the new src is loaded before we parse it FF only IE doesn't do nada
		
		//for IE put some delay to ensure that the new src is loaded before we parse it
    	if (isIE) // if (window[cfg_editorname]) 
    		setTimeout('parseTemplate()',1000); //if M$ like browser
    }
}

function parseTemplate()
{
	//replace template placeholders with database pulls
	var temp = new Array();
	contents=editControlContents(cfg_editorname);
	temp = contents.split('##'); //parse for template place holders identified by ##value##
	contents='';
	var x;
	
	for (x in temp) 
	{
		if ((x % 2))
		{ //odd numbered values contain placeholders
			if((document.getElementById(temp[x])) && (document.getElementById(temp[x]).value.length>0))
				temp[x]=document.getElementById(temp[x]).value; //known field placeholder with a value so use it
			else 
			{
				//get the placeholder value from the user
				var prompttext=new Array();
				prompttext=temp[x].split('=');
				temp[x]= prompt(prompttext[0], prompttext[1]);
			}
		}
		contents += temp[x];
	}
	seteditControlContents(cfg_editorname,contents);
}

function spellCheck() 
{
	//check spelling with ieSpell if available
 	try 
 	{
  		var axo=new ActiveXObject("ieSpell.ieSpellExtension");
  		axo.CheckAllLinkedDocuments(document);
 	} 
 	catch(e) 
 	{
  		if(e.number==-2146827859) 
  		{
  			if (confirm("ieSpell is not installed on your computer. \n Click [OK] to go to download page."))
    			window.open("http://www.iespell.com/download.php","DownLoad");
  		} 
  		else 
   			alert("Error Loading ieSpell: Exception " + e.number);
 	}
}


function parseText(obs) 
{
	//clean up OSCAR formated case management notes leaving the last entry
	var myRe= /^(.|\n|\r)*-{5}/g;
	if (obs.match(myRe)) 
		var obs=obs.replace(myRe,"");
	
	return obs;
}

function doHtml(value)  
{
	//insert HTML of value
	if (isIE) // if (window[cfg_editorname])
	{  //if you can't support insertHtml do something else
		var tmp = window[cfg_editorname].document.body.innerHTML;
		tmp = tmp+value;  // for IE this means append the text
		window[cfg_editorname].document.body.innerHTML = tmp;
	} 
	else 
		document.getElementById(cfg_editorname).contentWindow.document.execCommand("insertHtml", false, value);
	
	return;
}

function block(blockElements) 
{
	for(i=0; i<blockElements.length; i++) 
	{
		htm = '<div>'+blockElements[i]+'<\div>';
		doHtml(htm);
	}
}

function doTime() 
{
	// need to supply the time!
	var digital = new Date();
	var hours = digital.getHours();
	var minutes = digital.getMinutes();
	var seconds = digital.getSeconds();
	var amOrPm = "AM";
	if (hours > 11) 
		amOrPm = "PM";
	if (hours > 12) 
	 	hours = hours - 12;
	if (hours == 0) 
		hours = 12;						//0 hour
	if (minutes <= 9) 
		minutes = "0" + minutes;		//pad with 0
	if (seconds <= 9) 
		seconds = "0" + seconds;		//pad with 0
		
	var time = " " + hours + ":" + minutes + ":" + seconds + " " + amOrPm +" ";
	
	return time;
}

function doDate() 
{
	// need to supply the date!
	var digital = new Date();
	var days = digital.getDate();
	var months = digital.getMonth()*1 +1;
	var years = digital.getFullYear();
	var date =" " + days + "/" + months + "/" + years + " ";
	return date;
}

function doTable() 
{
	var rowstext = prompt("enter rows");
	var colstext = prompt("enter cols");
	var rows = parseInt(rowstext);
	var cols = parseInt(colstext);
	var table;
	if ((rows > 0) && (cols > 0)) 
	{
		table = '<table style="text-align: left; width: 100%;" border="1" cellpadding="2" cellspacing="2"><tbody>'
		for (var i=0; i < rows; i++) 
		{
			table +='<tr>';
			for (var j=0; j < cols; j++) 
				table +='<td>&nbsp;</td>';
			table +='</tr>';
		}
		table += '</tbody></table>';
    }
	return table;
}

function tbuttonOnClick() 
{
	// do the deed unless you need to prompt the user for information
	var value = this.getAttribute('cmdValue') || null;
	
	switch (value) 
	{
  		case "spell" : spellCheck(); break;
  		case "clock" : doHtml(doTime()); break;
  		case "date" : doHtml(doDate()); break;
  		case "table" : doHtml(doTable()); break;
  		case "help" : window.open(cfg_filesrc+"editor_help.html","mywindow","resizable=1,width=300,height=500"); break;
  		case "promptUser" : value = prompt(this.getAttribute('promptText'));
  		default: 
  			if (isIE) // if (window[this.name]) 
  				window[this.name].document.execCommand(this.id, false, value); 
  			else 
  				document.getElementById(this.name).contentWindow.document.execCommand(this.id, false, value); 
			break;
	}
}
function viewsource(inViewSource) 
{
	// load the html into a variable, blank the body, import as text, disable gui
	var html;
//	if (isIE) // if (window[cfg_editorname])
//	{
//		html = window[cfg_editorname].document.body.innerHTML ; //if browser supports M$ conventions
//		alert(html) ; //load into an alert as importnode not supported by IE
//		return;
//	}
	if (inViewSource) 
	{
		html = document.createTextNode(document.getElementById('edit').contentWindow.document.body.innerHTML);
		document.getElementById(cfg_editorname).contentWindow.document.body.innerHTML = "";
		html = document.getElementById(cfg_editorname).contentWindow.document.importNode(html,false);
		document.getElementById(cfg_editorname).contentWindow.document.body.appendChild(html);
		document.getElementById("control1").style.visibility="hidden";
		document.getElementById("control2").style.visibility="hidden";
		document.getElementById("control3").style.visibility="hidden";
	} 
	else 
	{
		html = document.getElementById(cfg_editorname).contentWindow.document.body.ownerDocument.createRange();
		html.selectNodeContents(document.getElementById(cfg_editorname).contentWindow.document.body);
		document.getElementById(cfg_editorname).contentWindow.document.body.innerHTML = html.toString();
		document.getElementById("control1").style.visibility="visible";
		document.getElementById("control2").style.visibility="visible";
		document.getElementById("control3").style.visibility="visible";
	}
	
	return;
}

function usecss(inUseCSS) 
{
	var theEditor = document.getElementById('edit').contentWindow.document;
	
    try 
    {
        theEditor.execCommand("styleWithCSS", 0, inUseCSS);
    } 
    catch (e) 
    {
        try 
        {
           theEditor.execCommand("useCSS", 0, inUseCSS);
        } 
        catch (e) 
        {
            try 
            {
            	// Mozilla
                theEditor.execCommand('styleWithCSS', false, inUseCSS);
            }
            catch (e) 
            {
            }
        }
    }
}
