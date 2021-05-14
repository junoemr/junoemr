<juno-modal id="edit-roster-status-modal"
            component-style="$ctrl.resolve.style">
    <modal-title class="roster-status-header">
        <h3 ng-hide="$ctrl.editMode">Add Status</h3>
        <h3 ng-show="$ctrl.editMode">Edit Status</h3>
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
        <div ng-if="$ctrl.status.systemManaged">
            This status is managed by the system. You cannot edit this.
        </div>
        <juno-input label="Status"
                    component-style="$ctrl.componentStyle"
                    ng-model="$ctrl.status.rosterStatus"
                    disabled="$ctrl.status.systemManaged">
        </juno-input>
        <juno-input label="Description"
                    component-style="$ctrl.componentStyle"
                    ng-model="$ctrl.status.statusDescription"
                    disabled="$ctrl.status.systemManaged">
        </juno-input>
        <juno-check-box label="Is Rostered"
                        component-style="$ctrl.componentStyle"
                        ng-model="$ctrl.status.rostered"
                        disabled="$ctrl.status.systemManaged">
        </juno-check-box>
        <juno-check-box label="Is Terminated"
                        component-style="$ctrl.componentStyle"
                        ng-model="$ctrl.status.terminated"
                        disabled="$ctrl.status.systemManaged">
        </juno-check-box>
    </modal-body>
    <modal-footer>
        <juno-button
                ng-if="!$ctrl.status.systemManaged"
                ng-click="$ctrl.onSave()"
                button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
                button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                component-style="$ctrl.componentStyle">
            Save
        </juno-button>
    </modal-footer>
</juno-modal>