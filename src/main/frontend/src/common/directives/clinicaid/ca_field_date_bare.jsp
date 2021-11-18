<span class="date" id="{{ date_picker_id }}">
	<input type="text"
				 id="input-{{ date_picker_id }}"
				 class="form-control"
				 ng-model="model"
				 ng-focus="on_focus($event)"
				 ng-change="delayed_change_fn()"
				 ng-disabled="disabled"
				 ng-click="on_click($event)"
				 ng-keyup="key_up($event)"
				 ng-keydown="key_down($event)"
				 tabindex="{{tab_index}}"
				 autocomplete="{{autocompleteOff}}"
				 placeholder="yyyy-mm-dd"/>
	<span class="{{ date_picker_id }}-body" style="position: relative">
	</span>
</span>
