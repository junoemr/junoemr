<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="fax-outbox">
	<h1 class="header"><bean:message bundle="ui" key="admin.fax.sr.outbox.header-title"/></h1>
	<div ng-if="$ctrl.masterFaxEnabledOutbound" ng-show="$ctrl.initialized">
		<span><bean:message bundle="ui" key="admin.fax.sr.outbox.resendAtMessage"/></span>
		<span class="text-bold">{{$ctrl.nextPushTimeDisplay()}}.</span>
	</div>
	<div ng-if="!$ctrl.masterFaxEnabledOutbound" ng-show="$ctrl.initialized">
		<span><bean:message bundle="ui" key="admin.fax.sr.outbox.disabledMessage"/></span>
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
				ng-model="$ctrl.searchParams.combinedStatus"
				options="$ctrl.statusFilterOptions"
				label="<bean:message bundle="ui" key="admin.fax.sr.search.combinedStatus"/>"
				label-position="$ctrl.LABEL_POSITION.TOP">
		</juno-select>
		<juno-select
				label="<bean:message bundle="ui" key="admin.fax.sr.choose-account"/>"
				label-position="$ctrl.LABEL_POSITION.TOP"
				ng-model="$ctrl.selectedFaxAccountId"
				options="$ctrl.faxAccountOptions"
				on-change="$ctrl.updateSelectedAccount(value, option)">
		</juno-select>
		<div class="w-128">
			<juno-button click="$ctrl.loadOutboxItems();"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
				<bean:message bundle="ui" key="global.search"/>
			</juno-button>
		</div>
	</div>

	<div>
		<table ng-show="$ctrl.outboxItemList.length > 0"
		       ng-table="$ctrl.tableParamsOutbox"
		       show-filter="false"
		       class="table table-striped table-bordered">
			<tbody>
			<tr ng-repeat="item in $ctrl.outboxItemList">
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.systemDateSent"/>'">
					{{$ctrl.formatDateForDisplay(item.systemSentDateTime)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.providerName"/>'">
					{{item.providerName}} ({{item.providerId}})
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.accountName"/>'">
					{{$ctrl.getFaxAccountDisplayName(item.faxAccountId)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.fileType"/>'">
					{{item.fileType}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.toFaxNumber"/>'">
					{{$ctrl.getFaxDisplayNumber(item.toFaxNumber)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.sentStatus"/>'">
					<div class="flex-row align-items-start">
						<icon-badge icon="{{$ctrl.getStatusIcon(item.combinedStatus)}}"
						            ng-class="$ctrl.getBadgeClasses(item.combinedStatus)">
						</icon-badge>
						<span title="{{item.systemStatusMessage}}" class="m-l-4">
							{{$ctrl.getStatusDisplayLabel(item.combinedStatus)}}
						</span>
					</div>
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.integrationDateSent"/>'">
					<div ng-hide="$ctrl.hideResendButton(item)">
						<juno-button click="$ctrl.resendFax(item);"
						             title="<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.resend-tooltip"/>"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT">
							<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.resend"/>
						</juno-button>
					</div>
					<span ng-show="$ctrl.hideResendButton(item)">
						{{$ctrl.formatDateForDisplay(item.integrationSentDateTime)}}
					</span>
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.actionButtons"/>'" class="w-128">
					<juno-button click="$ctrl.viewDownloadFile(item.id);"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
						<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.download"/>
					</juno-button>
				</td>
				<td ng-if="$ctrl.displayNotificationColumn" class="w-128">
					<juno-button ng-if="item.isNotificationStatusNotify"
					             click="$ctrl.dismissNotification(item);"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
						<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.notification.dismiss"/>
					</juno-button>
					<span ng-show="item.isNotificationStatusSilent">
						<bean:message bundle="ui" key="admin.fax.sr.outbox.notification.dismissed"/>
					</span>
				</td>
			</tr>
			</tbody>
		</table>
	</div>
</div>