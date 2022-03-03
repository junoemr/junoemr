<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="fax-outbox">
	<h1><bean:message bundle="ui" key="admin.fax.sr.outbox.header-title"/></h1>
	<div>
		<span><bean:message bundle="ui" key="admin.fax.sr.outbox.resendAtMessage"/> {{$ctrl.nextPushTime}}</span>
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
			<juno-select
					ng-model="$ctrl.searchParams.combinedStatus"
					options="$ctrl.statusFilterOptions"
					label="<bean:message bundle="ui" key="admin.fax.sr.search.combinedStatus"/>"
					label-position="$ctrl.LABEL_POSITION.TOP">
			</juno-select>
			<div class="w-128">
				<juno-button click="$ctrl.loadOutboxItems();"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
					<bean:message bundle="ui" key="global.search"/>
				</juno-button>
			</div>
		</div>
	</filter-panel>

	<div>
		<table ng-table="$ctrl.tableParamsOutbox" show-filter="false" class="table table-striped table-bordered">
			<tbody>
			<tr ng-repeat="item in $ctrl.outboxItemList">
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.systemDateSent"/>'">
					{{$ctrl.formatDateForDisplay(item.systemSentDateTime)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.providerName"/>'">
					{{item.providerName}} ({{item.providerNo}})
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.fileType"/>'">
					{{item.fileType}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.toFaxNumber"/>'">
					{{item.toFaxNumber}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.sentStatus"/>'">
					<span title="{{item.systemStatusMessage}}">
						{{$ctrl.getStatusDisplayLabel(item.combinedStatus)}}
					</span>
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.integrationDateSent"/>'">
					{{$ctrl.formatDateForDisplay(item.integrationSentDateTime)}}
				</td>
				<td data-title="'<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-hdr.actionButtons"/>'">
					<div class="flex-row justify-content-end">
						<div class="w-128"
						     ng-hide="$ctrl.hideResendButton(item)">
							<juno-button click="$ctrl.resendFax(item);"
							             title="<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.resend-tooltip"/>"
							             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
								<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.resend"/>
							</juno-button>
						</div>
						<div class="w-128">
							<juno-button click="$ctrl.viewDownloadFile(item.id);"
							             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
								<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.download"/>
							</juno-button>
						</div>
					</div>
				</td>
				<td ng-if="$ctrl.displayNotificationColumn == true">
					<div class="w-128"
					     ng-show="item.isNotificationStatusNotify()">
						<juno-button click="$ctrl.dismissNotification(item);"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
							<bean:message bundle="ui" key="admin.fax.sr.outbox.tbl-btn.notification.dismiss"/>
						</juno-button>
					</div>
					<span ng-show="item.isNotificationStatusSilent()">
						<bean:message bundle="ui" key="admin.fax.sr.outbox.notification.dismissed"/>
					</span>
				</td>
			</tr>
			</tbody>
		</table>
	</div>
</div>