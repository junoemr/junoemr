<div class="demographic-details-section roster-section">
    <h4 class="title">
        Rostering
    </h4>

    <div class="fields">
        <div class="column">
            <juno-input component-style="$ctrl.componentStyle"
                        ng-model="$ctrl.ngModel.rosteredProvider"
                        label="Rostered Physician">
            </juno-input>
            <!-- Roster Status -->
            <juno-select ng-model="$ctrl.ngModel.rosterStatus"
                         options="$ctrl.rosterStatusList"
                         label="Roster Status"
                         component-style="$ctrl.componentStyle">
            </juno-select>
            <!-- Termination Reason -->
            <juno-select ng-if="$ctrl.ngModel.rosterStatus === 'TE'"
                         ng-model="$ctrl.ngModel.rosterTerminationReason"
                         options="$ctrl.rosterTermReasons"
                         label="Termination Reason"
                         component-style="$ctrl.componentStyle">
            </juno-select>
        </div>

        <div class="divider">
        </div>

        <div class="column">
            <juno-button ng-click="$ctrl.openRosteredHistoryModal()"
                         button-color="JUNO_BUTTON_COLOR.GREYSCALE_LIGHT"
                         button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
                View Rostered History
            </juno-button>
            <!-- Date Rostered -->
            <juno-date-select ng-model="$ctrl.ngModel.rosterDate"
                              label="Roster Date"
                              on-validity-change="$ctrl.rosterDateValid = valid"
                              component-style="$ctrl.componentStyle">
            </juno-date-select>
            <!-- Termination Date -->
            <juno-date-select ng-if="$ctrl.ngModel.rosterStatus === 'TE'"
                              ng-model="$ctrl.ngModel.rosterTerminationDate"
                              label="Termination Date"
                              on-validity-change="$ctrl.terminationDateValid = valid"
                              component-style="$ctrl.componentStyle">
            </juno-date-select>
        </div>

    </div>
</div>
