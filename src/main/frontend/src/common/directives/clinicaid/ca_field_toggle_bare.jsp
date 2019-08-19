<input type="checkbox"
			 bs-switch
			 switch-on-text="{{true_text}}"
			 switch-off-text="{{false_text}}"
			 ng-true-value="'{{true_value}}'"
			 ng-false-value="'{{false_value}}'"
			 switch-active="{{!disabled}}"
			 id="input-{{name}}"
			 ng-model="model"
			 ng-focus="focus_fn()"
			 ng-change="change_fn()"
			 tabindex="{{tab_index}}"/>
