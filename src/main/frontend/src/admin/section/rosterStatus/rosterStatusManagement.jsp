<div>
    <table ng-table="ctrl.tableParams"
           class="table table-striped roster-status-table">
        <tbody>
        <tr ng-repeat="status in $ctrl.rosterStatuses" class="flex-fill-row">
            <td data-title="'Status'">
                {{status.rosterStatus}}
            </td>
            <td data-title="'Description'">
                {{status.statusDescription}}
            </td>
            <td data-title="'Created At'">
                {{status.createdAt}}
            </td>
            <td data-title="'Last Updated'">
                {{status.updatedAt}}
            </td>
            <td data-title="'Updated By'">
                {{status.updatedBy}}
            </td>
            <td data-title="'Is Rostered'">
                {{status.rostered}}
            </td>
            <td data-title="'Is Terminated'">
                {{status.terminated}}
            </td>
            <td data-title="Edit">
                <button class="btn btn-warning btn-active-hover sm-margin-left sm-margin-right"
                title="Edit Status"
                ng-click="$ctrl.editStatus(status)">
                    <i class="icon icon-write"></i>
                </button>
            </td>
        </tr>
        </tbody>
    </table>
    <juno-button ng-click="$ctrl.addStatus()"
                 button-color="JUNO_BUTTON_COLOR.PRIMARY"
                 button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
        Add New Status
    </juno-button>
</div>