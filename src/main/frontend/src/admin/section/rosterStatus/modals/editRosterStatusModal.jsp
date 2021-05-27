<juno-modal class="roster-status-edit-modal" component-style="$ctrl.componentStyle">
    <modal-ctl-buttons>
        <button type="button"
                class="btn btn-icon"
                aria-label="Close"
                ng-click="$ctrl.onCancel()"
                title="Close">
            <i class="icon icon-modal-ctl icon-close"></i>
        </button>
    </modal-ctl-buttons>

    <modal-title class="roster-status-header">
        <h3 ng-hide="$ctrl.editMode">Add Status</h3>
        <h3 ng-show="$ctrl.editMode">Edit Status</h3>
    </modal-title>

    <modal-body>
        <div class="roster-status-system-managed" ng-if="$ctrl.status.systemManaged">
            This status is managed by the system. You cannot edit this.
        </div>
        <juno-input class="roster-status-input"
                label="Status"
                component-style="$ctrl.componentStyle"
                ng-model="$ctrl.status.rosterStatus"
                disabled="$ctrl.editMode">
        </juno-input>
        <juno-input class="roster-status-input"
                label="Description"
                component-style="$ctrl.componentStyle"
                ng-model="$ctrl.status.statusDescription"
                disabled="$ctrl.status.systemManaged">
        </juno-input>
        <div>
            <div class="col-md-4">
                <juno-check-box class="roster-status-checkbox"
                                label="Is Rostered"
                                component-style="$ctrl.componentStyle"
                                ng-model="$ctrl.status.rostered"
                                disabled="$ctrl.status.systemManaged">
                </juno-check-box>
            </div>
            <div class="col-md-4">
                <juno-check-box
                    class="roster-status-checkbox"
                    label="Is Terminated"
                    component-style="$ctrl.componentStyle"
                    ng-model="$ctrl.status.terminated"
                    disabled="$ctrl.status.systemManaged">
                </juno-check-box>
            </div>
            <div class="col-md-4">
                <juno-check-box class="roster-status-checkbox"
                                label="Enabled"
                                component-style="$ctrl.componentStyle"
                                ng-model="$ctrl.status.active"
                                disabled="$ctrl.status.systemManaged">
                </juno-check-box>
            </div>
        </div>
    </modal-body>
    <modal-footer>
        <div class="col-md-6">
            <juno-button
                    ng-click="$ctrl.onCancel()"
                    button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
                    button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                    component-style="$ctrl.componentStyle">
                Cancel
            </juno-button>
        </div>
        <div class="col-md-6">
            <juno-button
                    ng-if="!$ctrl.status.systemManaged"
                    ng-click="$ctrl.onSave()"
                    button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
                    button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                    component-style="$ctrl.componentStyle">
                Save
            </juno-button>
        </div>
    </modal-footer>
</juno-modal>