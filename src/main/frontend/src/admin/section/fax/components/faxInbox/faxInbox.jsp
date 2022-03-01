
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="fax-inbox">
	<h1><bean:message bundle="ui" key="admin.fax.sr.inbox.header-title"/></h1>
	<div ng-show="$ctrl.faxAccount.enableInbound == true">
		<span><bean:message bundle="ui" key="admin.fax.sr.inbox.checkNewFaxesAt"/> {{$ctrl.nextPullTime}}</span>
		<br/>
		<span><bean:message bundle="ui" key="admin.fax.sr.inbox.unreadOnlyWarning"/></span>
	</div>
	<div ng-show="$ctrl.faxAccount.enableInbound == false">
		<span><bean:message bundle="ui" key="admin.fax.sr.inbox.disabledMessage"/></span>
	</div>

	<filter-panel label="Search Options"
	              tooltip="Show/Hide inbox filters">
		<div class="flex-row p-16 align-items-flex-end">
			<juno-date-select
					ng-model="$ctrl.searchParams.startDate"
					label="<bean:message bundle="ui" key="admin.fax.sr.search.startDate"/>"
					label-position="$ctrl.LABEL_POSITION.TOP">
			</juno-date-select>
			<juno-date-select
					ng-model="$ctrl.searchParams.endDate"
					label="<bean:message bundle="ui" key="admin.fax.sr.search.endDate"/>"
					label-position="$ctrl.LABEL_POSITION.TOP">
			</juno-date-select>
			<div class="w-128">
				<juno-button click="$ctrl.loadInboxItems();"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
					<bean:message bundle="ui" key="global.search"/>
				</juno-button>
			</div>
		</div>
	</filter-panel>

	<div>
		<table ng-table="$ctrl.tableParamsInbox" show-filter="false" class="table table-striped table-bordered">
			<tbody>
			<tr ng-repeat="item in $ctrl.inboxItemList">

				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-hdr.systemDateReceived"/>'">
					{{$ctrl.formatDateForDisplay(item.systemDateReceived)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-hdr.sentFrom"/>'">
					{{item.sentFrom}}
				</td>
				<td class="w-128">
					<juno-button click="$ctrl.openDocument(item.documentId)"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
						<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-btn.viewDocument"/>
					</juno-button>
				</td>
			</tr>
			</tbody>
		</table>
	</div>
</div>