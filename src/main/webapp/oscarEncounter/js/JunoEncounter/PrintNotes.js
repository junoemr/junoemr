'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};

if (!Juno.OscarEncounter.JunoEncounter.PrintNotes) Juno.OscarEncounter.JunoEncounter.PrintNotes = function PrintNotes(pageData, pageState)
{
	this.pageData = pageData;

	this.togglePrint = function togglePrint(noteId, e)
	{
		var selected = this.pageData.contextPath + "/oscarEncounter/graphics/printerGreen.png";
		var unselected = this.pageData.contextPath + "/oscarEncounter/graphics/printer.png";
		var imgId = "print" + noteId;
		var imgIdMinimized = "minimizedPrint" + noteId;
		var idx;
		var idx2;
		var tmp = "";

		//see whether we're called in a click event or not
		if (e != null)
			Event.stop(e);

		//if selected note has been inserted into print queue, remove it and update image src
		//else insert note into print queue
		idx = this.noteIsQueued(noteId);
		if (idx >= 0)
		{
			$(imgId).src = unselected;
			if($(imgIdMinimized))
			{
				$(imgIdMinimized).src = unselected;
			}

			//if we're slicing first note off list
			if (idx === 0)
			{
				idx2 = $F("notes2print").indexOf(",");
				if (idx2 > 0)
					tmp = $F("notes2print").substring(idx2 + 1);
			}
			//or we're slicing after first element
			else
			{
				idx2 = $F("notes2print").indexOf(",", idx);
				//are we in the middle of the list?
				if (idx2 > 0)
				{
					tmp = $F("notes2print").substring(0, idx);
					tmp += $F("notes2print").substring(idx2 + 1);
				}
				//or are we at the end of the list; don't copy comma
				else
					tmp = $F("notes2print").substring(0, idx - 1);

			}

			$("notes2print").value = tmp;
		}
		else
		{
			$(imgId).src = selected;
			if($(imgIdMinimized))
			{
				$(imgIdMinimized).src = selected;
			}
			if ($F("notes2print").length > 0)
				$("notes2print").value += "," + noteId;
			else
				$("notes2print").value = noteId;
		}

		return false;
	};

	this.clearAll = function clearAll(e)
	{
		var idx;
		var noteId;
		var notesDiv;
		var pos;
		var imgId;

		Event.stop(e);

		//cycle through container divs for each note
		for (idx = 1; idx <= pageState.notesOffset; ++idx)
		{
			var ncIdx = $("nc" + idx);

			if (ncIdx == null)
			{
				continue;
			}

			notesDiv = ncIdx.down("div[id^='n']");
			noteId = notesDiv.id.substr(1);  //get note id
			imgId = "print" + noteId;

			//if print img present, add note to print queue if not already there
			if ($(imgId) != null)
			{
				pos = this.noteIsQueued(noteId);
				if (pos >= 0)
				{
					this.removePrintQueue(noteId, pos);
				}
			}
		}

		if ($F("printCPP") === "true")
		{
			this.printInfo("imgPrintCPP", "printCPP");
		}

		if ($F("printRx") === "true")
		{
			this.printInfo("imgPrintRx", "printRx");
		}

		if ($F("printLabs") === "true")
		{
			this.printInfo("imgPrintLabs", "printLabs");
		}

		return false;
	};

	this.noteIsQueued = function noteIsQueued(noteId)
	{
		var foundIdx = -1;
		var curpos = 0;
		var arrNoteIds = $F("notes2print").split(",");

		for (var idx = 0; idx < arrNoteIds.length; ++idx)
		{
			if (arrNoteIds[idx] === noteId)
			{
				foundIdx = curpos;
				break;
			}
			curpos += arrNoteIds[idx].length + 1;
		}


		return foundIdx;
	};

	this.printToday = function printToday(e)
	{
		this.clearAll(e);

		$("printStartDate").value = moment().format("DD-MMM-YYYY");
		$("printEndDate").value = $F("printStartDate");
		$("printopDates").checked = true;

		this.printNotes();
	};

	this.printInfo = function printInfo(img, item)
	{
		var selected = this.pageData.contextPath + "/oscarEncounter/graphics/printerGreen.png";
		var unselected = this.pageData.contextPath + "/oscarEncounter/graphics/printer.png";

		if ($F(item) === "true")
		{
			$(img).src = unselected;
			$(item).value = "false";
		}
		else
		{
			$(img).src = selected;
			$(item).value = "true";
		}

		return false;
	};

	this.getPrintDates = function getPrintDates()
	{
		var startDate = $F("printStartDate");
		var endDate = $F("printEndDate");

		if(startDate.length === 0 || endDate.length === 0)
		{
			alert(pageData.printDateMsg);
			return null;
		}

		var startMoment = moment(startDate, "DD-MMM-YYYY");
		var endMoment = moment(endDate, "DD-MMM-YYYY");

		if(startMoment.isAfter(endMoment))
		{
			alert(pageData.printDateOrderMsg);
			return null;
		}

		return {
			start: startMoment.toDate(),
			end: endMoment.toDate()
		};
	};

	this.removePrintQueue = function removePrintQueue(noteId, idx)
	{
		var unselected = this.pageData.contextPath + "/oscarEncounter/graphics/printer.png";
		var imgId = "print" + noteId;
		var imgIdMinimized = "minimizedPrint" + noteId;
		var tmp = "";
		var idx2;

		$(imgId).src = unselected; //imgPrintgrey.src;
		if($(imgIdMinimized))
		{
			$(imgIdMinimized).src = unselected;
		}

		//if we're slicing first note off list
		if (idx === 0)
		{
			idx2 = $F("notes2print").indexOf(",");
			if (idx2 > 0)
				tmp = $F("notes2print").substring(idx2 + 1);
		}
		//or we're slicing after first element
		else
		{
			idx2 = $F("notes2print").indexOf(",", idx);
			//are we in the middle of the list?
			if (idx2 > 0)
			{
				tmp = $F("notes2print").substring(0, idx);
				tmp += $F("notes2print").substring(idx2 + 1);
			}
			//or are we at the end of the list; don't copy comma
			else
				tmp = $F("notes2print").substring(0, idx - 1);

		}

		$("notes2print").value = tmp;
	};

	this.printSetup = function printSetup(e)
	{
		if ($F("notes2print").length > 0)
		{
			$("printopSelected").checked = true;
		}
		else
		{
			$("printopAll").checked = true;
		}

		var printOps = $("printOps");
		printOps.style.right = (pageWidth() - Event.pointerX(e)) + "px";
		printOps.style.bottom = (pageHeight() - Event.pointerY(e)) + "px";
		printOps.style.display = "block";

		return false;
	};

	this.printNotes = function printNotes()
	{
		var printType = null;
		var dateObject = null;

		if ($("printopAll").checked)
		{
			printType = "all";
		}
		else if ($("printopDates").checked)
		{
			dateObject = this.getPrintDates();

			if(dateObject == null)
			{
				return false;
			}

			printType = "dates";
		}

		var selectedNoteCsv = $F("notes2print");

		var noteArray = [];
		if(selectedNoteCsv.length > 0)
		{
			noteArray = selectedNoteCsv.split(",");
		}

		var printConfig = {
			printType: printType,
			dates: dateObject,
			cpp: $F("printCPP"),
			rx: $F("printRx"),
			labs: $F("printLabs"),
			selectedList: noteArray
		};

		var jsonString = JSON.stringify(printConfig);

		var url = "../ws/rs/recordUX/" + this.pageData.demographicNo + "/print?printOps=" + encodeURIComponent(jsonString);

		// Open the link as a download to match the old encounter page
		var link = document.createElement('a');
		link.href = url;
		link.download = "Encounter-" + moment().format("YYYY-MM-DD.HH.mm.ss") + ".pdf";
		link.dispatchEvent(new MouseEvent('click'));

		return false;
	};
};

