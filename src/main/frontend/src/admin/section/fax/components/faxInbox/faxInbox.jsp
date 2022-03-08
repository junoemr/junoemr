
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="fax-inbox">
	<h1 class="header"><bean:message bundle="ui" key="admin.fax.sr.inbox.header-title"/></h1>
	<div ng-if="$ctrl.masterFaxEnabledInbound" ng-show="$ctrl.initialized">
		<span><bean:message bundle="ui" key="admin.fax.sr.inbox.checkNewFaxesAt"/></span>
		<span class="text-bold">{{$ctrl.nextPullTimeDisplay()}}.</span>
		<span><bean:message bundle="ui" key="admin.fax.sr.inbox.unreadOnlyWarning"/></span>
	</div>
	<div ng-if="!$ctrl.masterFaxEnabledInbound" ng-show="$ctrl.initialized">
		<span><bean:message bundle="ui" key="admin.fax.sr.inbox.disabledMessage"/></span>
	</div>

	<div class="flex-row flex-gap-16 p-t-16 p-b-16 align-items-flex-end">
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
		<juno-select
				label="<bean:message bundle="ui" key="admin.fax.sr.choose-account"/>"
				label-position="$ctrl.LABEL_POSITION.TOP"
				ng-model="$ctrl.selectedFaxAccountId"
				options="$ctrl.faxAccountOptions"
				on-change="$ctrl.updateSelectedAccount(value, option)">
		</juno-select>
		<div class="w-128">
			<juno-button click="$ctrl.loadInboxItems();"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
				<bean:message bundle="ui" key="global.search"/>
			</juno-button>
		</div>
	</div>

	<div>
		<table ng-show="$ctrl.inboxItemList.length > 0"
		       ng-table="$ctrl.tableParamsInbox"
		       show-filter="false"
		       class="table table-striped table-bordered">
			<tbody>
			<tr ng-repeat="item in $ctrl.inboxItemList">
				<td class="w-32">
					<icon-badge icon="icon-check"></icon-badge>
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-hdr.systemDateReceived"/>'">
					{{$ctrl.formatDateForDisplay(item.systemDateReceived)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-hdr.sentFrom"/>'">
					{{$ctrl.getFaxDisplayNumber(item.sentFrom)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.inbox.tbl-hdr.accountName"/>'">
					{{$ctrl.getFaxAccountDisplayName(item.faxAccountId)}}
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