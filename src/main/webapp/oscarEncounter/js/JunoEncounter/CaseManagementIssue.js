'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter.CaseManagementIssue) Juno.OscarEncounter.JunoEncounter.CaseManagementIssue =
	function CaseManagementIssue(pageData)
	{
		this.pageData = pageData;

		this.displayResolvedIssues = function displayResolvedIssues(clickContext)
		{
			this.showIssues(
				clickContext,
				'resolvedIssues',
				'noteIssues-resolved',
				'<bean:message key="oscarEncounter.referenceResolvedIssues.title"/>'
			);
		};

		this.displayUnresolvedIssues = function displayUnresolvedIssues(clickContext)
		{
			this.showIssues(
				clickContext,
				'unresolvedIssues',
				'noteIssues-unresolved',
				'<bean:message key="oscarEncounter.referenceUnresolvedIssues.title"/>'
			);
		};

		this.showIssues = function showIssues(clickContext, method, divId, title)
		{
			var me = this;

			jQuery.ajax({
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				url: "../ws/rs/demographic/" + this.pageData.demographicNo + "/" + method,
				success: function (response)
				{
					if(!response || !jQuery.isArray(response.body))
					{
						return false;
					}

					jQuery('#' + divId).empty();
					me.displayExistingIssueList(clickContext, response.body, divId, title);
				}
			});
		};

		this.getIssueIdArray = function getIssueIdArray(assignedCMIssues)
		{
			var issueIdArray = [];

			if(!jQuery.isArray(assignedCMIssues))
			{
				return issueIdArray;
			}

			for(var i = 0; i < assignedCMIssues.length; i++)
			{
				issueIdArray.push(assignedCMIssues[i].issue.id);
			}

			return issueIdArray;
		};

		this.removeMatchingIssueCheckboxes = function removeMatchingIssueCheckboxes(issueIdArray)
		{
			jQuery("#noteIssueIdList input:checkbox[name=issue_id]").each(function ()
			{
				var issueId = parseInt(jQuery(this).val());
				console.log('"' + issueId + '"');
				console.log(issueIdArray);
				console.log(jQuery.inArray(issueId, issueIdArray));
				console.log(jQuery.inArray(6279, issueIdArray));
				console.log(jQuery.inArray("6279", issueIdArray));

				if(jQuery.inArray(issueId, issueIdArray) >= 0)
				{
					jQuery(this).remove();
				}
			});
		};

		this.displayExistingIssueList = function displayExistingIssueList(clickContext, issueArray, divId, title)
		{
			var assignedIssueIdArray = this.getIssueIdArray(pageState.currentAssignedCMIssues);

			this.removeMatchingIssueCheckboxes(this.getIssueIdArray(issueArray));

			var templateParameters = {
				title: title,
				issueArray: issueArray,
				assignedIssueArray: assignedIssueIdArray,
				propertyArray: [
					{
						name: "acute",
						label: "acute",
						fieldName: "acute",
						fieldValue: "true",
						class: ""
					},
					{
						name: "chronic",
						label: "chronic",
						fieldName: "acute",
						fieldValue: "false",
						class: "setIssueListControlRight"
					},
					{
						name: "certain",
						label: "certain",
						fieldName: "certain",
						fieldValue: "true",
						class: ""
					},
					{
						name: "uncertain",
						label: "uncertain",
						fieldName: "certain",
						fieldValue: "false",
						class: "setIssueListControlRight"
					},
					{
						name: "major",
						label: "major",
						fieldName: "major",
						fieldValue: "true",
						class: ""
					},
					{
						name: "not_major",
						label: "not major",
						fieldName: "major",
						fieldValue: "false",
						class: "setIssueListControlRight"
					},
					{
						name: "resolved",
						label: "resolved",
						fieldName: "resolved",
						fieldValue: "true",
						class: ""
					},
					{
						name: "unresolved",
						label: "unresolved",
						fieldName: "resolved",
						fieldValue: "false",
						class: "setIssueListControlRight"
					},
				]
			};

			clickContext.jQuery('#existingIssueTemplate').tmpl(templateParameters).appendTo('#' + divId);

			Element.show(divId);
		};

		this.getSaveIssueUrl = function getSaveIssueUrl(issueId)
		{
			return "../ws/rs/demographic" +
				"/" + this.pageData.demographicNo +
				"/caseManagementIssue" +
				"/" + encodeURIComponent(issueId) +
				"/updateProperty"
		};

		this.saveIssueProperty = function saveIssueProperty(clickContext, issueId, propertyName, propertyValue)
		{
			var postData = {
				propertyName: propertyName,
				propertyValue: propertyValue
			};

			var jsonString = JSON.stringify(postData);

			var me = this;

			jQuery.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: this.getSaveIssueUrl(issueId),
				data: jsonString,
				success: function (response)
				{
					if(propertyName == 'resolved')
					{
						// Update both lists if resolvedness changes
						me.displayResolvedIssues(clickContext);
						me.displayUnresolvedIssues(clickContext);
					}
					console.log(response);
				}
			});
		};

		this.toggleIssueWidget = function toggleIssueWidget(issueId)
		{
			var widget = jQuery('#setIssueListWidget' + issueId);

			if(widget.is(":visible"))
			{
				widget.css('background-color', '');
				widget.css('border', '');
				widget.hide();
			}
			else
			{
				jQuery.ajax({
					type: "GET",
					url: "../ws/rs/demographic/" + this.pageData.demographicNo + "/caseManagementIssue/" + issueId,
					success: function(response)
					{
						if(response.status != "SUCCESS")
						{
							return;
						}

						var issue = response.body;

						widget.css('background-color', '#dde3eb');
						widget.css('border', '1px solid #464f5a');
						widget.show();


						this.checkIssueItem(issueId, 'acute', 'chronic', issue.acute);
						this.checkIssueItem(issueId, 'certain', 'uncertain', issue.certain);
						this.checkIssueItem(issueId, 'major', 'not_major', issue.major);
						this.checkIssueItem(issueId, 'resolved', 'unresolved', issue.resolved);
					}
				});
			}
		};

		this.checkIssueItem = function checkIssueItem(issueId, trueField, falseField, value)
		{
			var field = falseField;
			if(value)
			{
				field = trueField;
			}

			jQuery('#issueCheckList' + issueId + '\\.' + field).prop('checked', true);
		};
}
