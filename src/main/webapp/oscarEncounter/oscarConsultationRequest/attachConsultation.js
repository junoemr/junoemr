/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

*/

'use strict';

window.Oscar = window.Oscar || {};
window.Oscar.AttachConsultation = {

	DOCTYPE_DOC: 'D',
	DOCTYPE_LAB: 'L',
	DOCTYPE_EFORM: 'E',
	DOCTYPE_HRM: 'H',

	//if consultation has not been saved, load existing docs into proper select boxes
	init: function init(loadFromOpener, docs)
	{
		if(loadFromOpener)
		{
			docs = window.opener.document.EctConsultationFormRequestForm.documents.value;
		}
		docs = docs.split("|");
		this.checkDocuments(docs);
	},

	// set the checked state of attachments
	checkDocuments: function checkDocuments(docs)
	{
		if (docs == null)
		{
			return;
		}
		for (let idx = 0; idx < docs.length; ++idx)
		{
			if (docs[idx].length < 2)
			{
				continue;
			}

			let inputName = "";
			switch (docs[idx].charAt(0))
			{
				case this.DOCTYPE_DOC:
					inputName = "docNo";
					break;
				case this.DOCTYPE_LAB:
					inputName = "labNo";
					break;
				case this.DOCTYPE_EFORM:
					inputName = "eFormNo";
					break;
				case this.DOCTYPE_HRM:
					inputName = "hrmId";
					break;
				default:
					console.error("Invalid doctype: " + docs[idx].charAt(0) + " for value '" + docs[idx] + "'");
					continue;
			}
			$("input[name='" + inputName + "'][value='" + docs[idx].substring(1) + "']").attr("checked", "checked");
		}
	},

	save: function save(emptyMessage)
	{
		var ret;
		console.info(document.forms[0].requestId.value);
		if (document.forms[0].requestId.value == null || document.forms[0].requestId.value === "null")
		{
			var saved = "";
			var list = window.opener.document.getElementById("attachedList");
			var paragraph = window.opener.document.getElementById("attachDefault");

			paragraph.innerHTML = "";

			//delete what we have before adding new docs to list
			while (list.firstChild)
			{
				list.removeChild(list.firstChild);
			}

			$("input[name='docNo']:checked").each(function ()
			{
				saved += (saved === "" ? "" : "|") + Oscar.AttachConsultation.DOCTYPE_DOC + $(this).val();
				let listElem = window.opener.document.createElement("li");
				listElem.innerHTML = $(this).next().get(0).innerHTML;
				listElem.className = "doc";
				list.appendChild(listElem);
			});
			$("input[name='labNo']:checked").each(function ()
			{
				saved += (saved === "" ? "" : "|") + Oscar.AttachConsultation.DOCTYPE_LAB + $(this).val();
				let listElem = window.opener.document.createElement("li");
				listElem.innerHTML = $(this).next().get(0).innerHTML;
				listElem.className = "lab";
				list.appendChild(listElem);
			});
			$("input[name='eFormNo']:checked").each(function ()
			{
				saved += (saved === "" ? "" : "|") + Oscar.AttachConsultation.DOCTYPE_EFORM + $(this).val();
				let listElem = window.opener.document.createElement("li");
				listElem.innerHTML = $(this).next().get(0).innerHTML;
				listElem.className = "eform";
				list.appendChild(listElem);
			});
			$("input[name='hrmId']:checked").each(function ()
			{
				saved += (saved === "" ? "" : "|") + Oscar.AttachConsultation.DOCTYPE_HRM + $(this).val();
				let listElem = window.opener.document.createElement("li");
				listElem.innerHTML = $(this).next().get(0).innerHTML;
				listElem.className = "hrm";
				list.appendChild(listElem);
			});

			window.opener.document.EctConsultationFormRequestForm.documents.value = saved;

			if (list.childNodes.length === 0)
			{
				paragraph.innerHTML = emptyMessage;
			}
			ret = false;
		}
		else
		{
			window.opener.updateAttached();
			ret = true;
		}
		if (!ret) window.close();
		return ret;
	},

    previewPDF: function previewPDF(docId, url, contextPath)
    {
        var path = contextPath + "/oscarEncounter/oscarConsultationRequest/displayImage.jsp?url="
            + encodeURIComponent(contextPath + "/dms/ManageDocument.do?method=view&doc_no=" + docId)
            + "&link=" + encodeURIComponent(url);

        Oscar.AttachConsultation._setPreviewFrame(path);
    },

	previewHTML: function previewHTML(url)
	{
        Oscar.AttachConsultation._setPreviewFrame(url);
	},

	previewImage: function previewImage(url, contextPath)
	{
	    var path = contextPath + "/oscarEncounter/oscarConsultationRequest/displayImage.jsp?url=" + encodeURIComponent(url);
        Oscar.AttachConsultation._setPreviewFrame(path);
	},

	toggleSelectAll: function toggleSelectAll()
	{
		$("input[type='checkbox']").attr("checked", $("#selectAll").attr("checked"));
	},

    _setPreviewFrame: function(srcPath)
    {
        $("#previewPane").html('<iframe id="previewPane" class="flexGrow" sandbox="allow-same-origin" src="' + srcPath + '">')
    },
};