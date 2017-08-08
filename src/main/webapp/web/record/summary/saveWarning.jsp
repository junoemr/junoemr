<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>
<div class="modal-content">

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-label="Close" ng-click="saveWarningCtrl.cancel()">
			<span aria-hidden="true" >&times;</span>
		</button>
		<h3 class="modal-title">Warning</h3>
	</div>

	<div class="modal-body">
		<p>You have unsaved changes to a note! Do you want to save them?</p>
	</div>
		
	<div class="modal-footer">
		<button ng-click="saveWarningCtrl.cancel()" type="button" class="btn btn-default">Cancel</button>
		<button ng-click="saveWarningCtrl.clearChanges()" type="button" class="btn btn-danger">No</button>	
		<button ng-click="saveWarningCtrl.save()" type="button" class="btn btn-success">Yes</button>
	</div>
</div>
