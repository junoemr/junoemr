<div class="juno-input-save" ng-class="$ctrl.componentClasses()">

    <label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
        {{$ctrl.label}}
    </label>
    <div class ="left">
        <juno-input ng-model="$ctrl.ngModel"
                    component-style="$ctrl.componentStyle"
                    invalid="$ctrl.invalid"
                    character-limit="4"
                    hide-character-limit="true">
        </juno-input>
    </div>
    <div class="right">
        <juno-button button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                     button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
                     component-style="$ctrl.componentStyle"
                     click="$ctrl.onClick()"
                     title="Save Phone Prefix">
            <i class="icon-logout fa-lg"></i>
        </juno-button>
    </div>
</div>