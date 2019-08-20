// Title: Tigra Color Picker
// URL: http://www.softcomplex.com/products/tigra_color_picker/
// Version: 1.1
// Date: 06/26/2003 (mm/dd/yyyy)
// Note: Permission given to use this script in ANY kind of applications if
//    header lines are left unchanged.
// Note: Script consists of two files: picker.js and picker.html

var TCP = new TColorPicker();

function TCPopup(field, palette) {
	this.field = field;
	this.initPalette = !palette || palette > 4 ? 0 : palette;
	var w = 194, h = 400,
	move = screen ? 
		',left=' + ((screen.width - w) >> 1) + ',top=' + ((screen.height - h) >> 1) : '', 
	o_colWindow = window.open('../admin/picker.html', null, "help=no,status=no,scrollbars=no,resizable=no" + move + ",width=" + w + ",height=" + h + ",dependent=yes", true);
	o_colWindow.opener = window;
	o_colWindow.focus();
}

function TCBuildCell (R, G, B, w, h) {
	return '<td bgcolor="#' + this.dec2hex((R << 16) + (G << 8) + B) + '"><a href="javascript:P.S(\'' + this.dec2hex((R << 16) + (G << 8) + B) + '\')" onmouseover="P.P(\'' + this.dec2hex((R << 16) + (G << 8) + B) + '\')">&nbsp;&nbsp;</a></td>';
}
function TCBuildCell2 (colorCode) {
	return '<td bgcolor="#' + colorCode + '"><a href="javascript:P.S(\'' + colorCode + '\')" onmouseover="P.P(\'' + colorCode + '\')"></a></td>';
}

function TCSelect(c) {
	this.field.value = '#' + c.toUpperCase();
	this.win.close();
}

function TCPaint(c, b_noPref) {
	c = (b_noPref ? '' : '#') + c.toUpperCase();
	if (this.o_samp) 
		this.o_samp.innerHTML = '<font face=Tahoma size=2>' + c +' <font color=white>' + c + '</font></font>'
	if(this.doc.layers)
		this.sample.bgColor = c;
	else { 
		if (this.sample.backgroundColor != null) this.sample.backgroundColor = c;
		else if (this.sample.background != null) this.sample.background = c;
	}
}

function TCGenerateSafe() {
	var s = '';
	for (j = 0; j < 12; j ++) {
		s += "<tr>";
		for (k = 0; k < 3; k ++)
			for (i = 0; i <= 5; i ++)
				s += this.bldCell(k * 51 + (j % 2) * 51 * 3, Math.floor(j / 2) * 51, i * 51, 8, 10);
		s += "</tr>";
	}
	return s;
}

function TCGenerateWind() {
	var s = '';
	for (j = 0; j < 12; j ++) {
		s += "<tr>";
		for (k = 0; k < 3; k ++)
			for (i = 0; i <= 5; i++)
				s += this.bldCell(i * 51, k * 51 + (j % 2) * 51 * 3, Math.floor(j / 2) * 51, 8, 10);
		s += "</tr>";
	}
	return s	
}
function TCGenerateMac() {
	var s = '';
	var c = 0,n = 1;
	var r,g,b;
	for (j = 0; j < 15; j ++) {
		s += "<tr>";
		for (k = 0; k < 3; k ++)
			for (i = 0; i <= 5; i++){
				if(j<12){
				s += this.bldCell( 255-(Math.floor(j / 2) * 51), 255-(k * 51 + (j % 2) * 51 * 3),255-(i * 51), 8, 10);
				}else{
					if(n<=14){
						r = 255-(n * 17);
						g=b=0;
					}else if(n>14 && n<=28){
						g = 255-((n-14) * 17);
						r=b=0;
					}else if(n>28 && n<=42){
						b = 255-((n-28) * 17);
						r=g=0;
					}else{
						r=g=b=255-((n-42) * 17);
					}
					s += this.bldCell( r, g,b, 8, 10);
					n++;
				}
			}
		s += "</tr>";
	}
	return s;
}

function TCGenerateGray() {
	var s = '';
	for (j = 0; j <= 15; j ++) {
		s += "<tr>";
		for (k = 0; k <= 15; k ++) {
			g = Math.floor((k + j * 16) % 256);
			s += this.bldCell(g, g, g, 9, 7);
		}
		s += '</tr>';
	}
	return s
}

function TCGenerateJuno() {
	var html = '';

	var colorCodeArray = [
		//darkest, darker,  dark,   base,    light,   lighter,  lightest
		['401616','661a1a','991f1f','cc2929','e65c5c','e68a8a','ffcccc'], //red
		['4d2a08','80460d','b36212','e67e17','e69545','f2b679','ffd9b3'], //orange
		['4d420f','806e19','b39b24','e6c72e','e6cf5c','f2e291','fff7cc'], //yellow
		['243811','406619','609926','80cc33','a6e667','bde695','e6ffcc'], //lime
		['0d3313','1a6626','238c35','30bf48','62d975','95e6a3','ccffd4'], //green
		['0d3326','165943','238c69','30bf8f','62d9b1','95e6cb','ccffee'], //teal
		['0d3333','165959','238c8c','30bfbf','62d9d9','95e6e6','ccffff'], //cyan
		['102f40','164259','23678c','308dbf','62afd9','8ac5e6','ccedff'], //blue
		['1a2040','1f2b66','263999','334dcc','5c73e6','8a99e6','ccd5ff'], //indigo
		['231b4d','302080','432db3','5639e6','7c67e6','ac9df2','d4ccff'], //violet
		['301640','4b1f66','6d2699','9133cc','a15ccc','c795e6','ebccff'], //grape
		['401624','661933','99264d','cc3366','e66791','e6a1b8','ffccdd'], //pink
	];

	for(var i = 0; i < colorCodeArray.length; i++)
	{
		html += "<tr>";
		for(var j=0; j < colorCodeArray[i].length; j++)
		{
			html += this.bldCell2(colorCodeArray[i][j]);
		}
		html += "</tr>";
	}
	return html;
}

function TCDec2Hex(v) {
	v = v.toString(16);
	for(; v.length < 6; v = '0' + v);
	return v;
}

function TCChgMode(v) {
	for (var k in this.divs) this.hide(k);
	this.show(v);
}

function TColorPicker(field) {
	this.build0 = TCGenerateJuno;
	this.build1 = TCGenerateSafe;
	this.build2 = TCGenerateWind;
	this.build3 = TCGenerateGray;
	this.build4 = TCGenerateMac;
	this.show = document.layers ? 
		function (div) { this.divs[div].visibility = 'show' } :
		function (div) { this.divs[div].visibility = 'visible' };
	this.hide = document.layers ? 
		function (div) { this.divs[div].visibility = 'hide' } :
		function (div) { this.divs[div].visibility = 'hidden' };
	// event handlers
	this.C       = TCChgMode;
	this.S       = TCSelect;
	this.P       = TCPaint;
	this.popup   = TCPopup;
	this.draw    = TCDraw;
	this.dec2hex = TCDec2Hex;
	this.bldCell = TCBuildCell;
	this.bldCell2 = TCBuildCell2;
	this.divs = [];
}

function TCDraw(o_win, o_doc) {
	this.win = o_win;
	this.doc = o_doc;
	var 
	s_tag_openT  = o_doc.layers ? 
		'layer visibility=hidden top=54 left=5 width=182' : 
		'div style=visibility:hidden;position:absolute;left:6px;top:54px;width:182px;height:0',
	s_tag_openS  = o_doc.layers ? 'layer top=32 left=6' : 'div',
	s_tag_close  = o_doc.layers ? 'layer' : 'div';
		
	this.doc.write('<' + s_tag_openS + ' id=sam name=sam>' +
		'<table cellpadding=0 cellspacing=0 border=1 align=center class=bd>' +
			'<tr>' +
			'<td align=center>' +
			'<div id="samp">' +
			'<font face=Tahoma size=2>sample ' +
			'<font color=white>sample</font>' +
			'</font>' +
			'</div>' +
			'</td>' +
			'</tr>' +
		'</table>' +
		'</' + s_tag_close + '>');
	this.sample = o_doc.layers ? o_doc.layers['sam'] : 
		o_doc.getElementById ? o_doc.getElementById('sam').style : o_doc.all['sam'].style;

	for (var k = 0; k < 5; k ++) {
		this.doc.write('<' + s_tag_openT + ' id="p' + k + '" name="p' + k + '"><table cellpadding=0 cellspacing=0 border=1 align=center>' + this['build' + k]() + '</table></' + s_tag_close + '>');
		this.divs[k] = o_doc.layers 
			? o_doc.layers['p' + k] : o_doc.all 
				? o_doc.all['p' + k].style : o_doc.getElementById('p' + k).style
	}
	if (!o_doc.layers && o_doc.body.innerHTML) 
		this.o_samp = o_doc.all 
			? o_doc.all.samp : o_doc.getElementById('samp');
	this.C(this.initPalette);
	if (this.field.value) this.P(this.field.value, true)
}
