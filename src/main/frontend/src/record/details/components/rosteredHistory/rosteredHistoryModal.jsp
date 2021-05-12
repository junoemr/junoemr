<juno-modal id="view-rostered-history-modal"
            component-style="$ctrl.resolve.style"
            hide-footer="true">
    <modal-title>
        <h3>Rostered History</h3>
    </modal-title>

    <modal-ctl-buttons>
        <button type="button"
                class="btn btn-icon"
                aria-label="Close"
                ng-click="$ctrl.onCancel()"
                title="Close">
            <i class="icon icon-modal-ctl icon-close"></i>
        </button>
    </modal-ctl-buttons>

    <modal-body>
        <div class="height-100 overflow-auto roster-history">
            <table ng-table="$ctrl.tableParams" class="table table-striped roster-history-table">
                <tbody>
                <tr ng-repeat="entry in $ctrl.rosteredHistory">
                    <td data-title="'Status Description'">
                        {{entry.statusDescription}}
                    </td>
                    <td data-title="'Rostered To'">
                        {{entry.provider}}
                    </td>
                    <td data-title="'Rostered Date'">
                        {{entry.rosterDate}}
                    </td>
                    <td data-title="'Terminated Date'">
                        {{entry.rosterTerminationDate}}
                    </td>
                    <td data-title="'Termination Reason'">
                        {{entry.rosterTerminationDescription}}
                    </td>
                    <td data-title="'Modified At'">
                        {{entry.addedAt}}
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </modal-body>
</juno-modal>