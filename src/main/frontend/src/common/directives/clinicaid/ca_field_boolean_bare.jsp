<input type="checkbox"
			 bs-switch
			 switch-on-text="Yes"
			 switch-off-text="No"
			 switch-active="{{!disabled}}"
			 id="input-{{name}}"
			 ng-model="model"
			 ng-focus="focus_fn()"
			 ng-change="change_fn()"
			 tabindex="{{tab_index}}"/>
