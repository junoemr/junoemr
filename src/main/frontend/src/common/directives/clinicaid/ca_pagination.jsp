
<ul class="pagination">
	<li ng-show="mode == 'pages'"
			ng-class="{'disabled': pagination.current_page <= 1 || pagination.total_pages == 0 }">
		<a href="" ng-click="change_page(pagination.current_page - 1);">
			<span class="previous" alt=""></span>
		</a>
	</li>
	<li ng-show="mode == 'pages'"
			ng-repeat="page_number in pagination.page_options track by $index"
			ng-class="{'active': pagination.current_page == page_number}">
		<a ng-show="page_number == '...'" href="" ng-click="expand_ellipsis()">{{page_number}}</a>
		<a ng-show="page_number != '...'" href="" ng-click="change_page(page_number)">{{page_number}}</a>
	</li>
	<li ng-show="mode == 'pages'"
		ng-class="{'disabled': pagination.current_page == pagination.total_pages || pagination.total_pages == 0}">
		<a href="" ng-click="change_page(pagination.current_page + 1)">
			<span class="next"></span>
		</a>
	</li>

	<li ng-show="mode == 'goto'" class="go-to-cancel">
		<a href="" ng-click="close_ellipsis()">...</a>
	</li>
	<li ng-show="mode == 'goto'" class="go-to">
		Go to page
	</li>
	<li ng-show="mode == 'goto'" class="go-to">
		<form class="form-inline" ng-submit="go_to_page()">
			<input class="form-control input-sm" ng-model="go_to_page_num" type="text"/>
		</form>
	</li>
	<li ng-show="mode == 'goto'" class="go-to">
		of {{pagination.total_pages}}
	</li>
</ul>

