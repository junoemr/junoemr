<%--
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
--%>
<div class="accordion-list-component">
	<nav>
		<ul>
			<li class="accordion-group" ng-repeat="group in $ctrl.itemList">
				<h5 class="accordion-group-header">
					<a href="javascript:" ng-class="group.expanded ? 'expanded-group' : ''" ng-click="$ctrl.onGroupClick(group)" data-toggle="collapse" data-target="{{ '#accordion-collapse-target-' + $ctrl.getGroupCollapseId(group)}}">
						<div>{{ group.name }}</div>
						<i class="icon icon-chevron-right" ng-if="!group.expanded"></i>
						<i class="icon icon-chevron-down" ng-if="group.expanded"></i>
					</a>
				</h5>
				<nav class="group-items-list" ng-class="group.expanded ? 'in' : 'collapse'" id="{{ 'accordion-collapse-target-' + $ctrl.getGroupCollapseId(group)}}">
					<ul>
						<li class="accordion-item" ng-repeat="item in group.items">
							<a href="javascript:" ng-click="$ctrl.onItemClicked(item)">
								<div class="accordion-item-content">
									{{ item.name }}
								</div>
							</a>
						</li>
					</ul>
				</nav>
				{{ $ctrl.bindCollapseListener($index) }}
			</li>
		</ul>
	</nav>
</div>