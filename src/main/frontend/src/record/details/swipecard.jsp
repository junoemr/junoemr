<%--

	Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

	This software was written for
	CloudPractice Inc.
	Victoria, British Columbia
	Canada

--%>
<juno-modal id="swipecard-modal">
	<modal-title>
		<i class="icon icon-plus-square icon-modal-header"></i>
		<div class="align-baseline">
			<h3>Swipe Card</h3>
		</div>
	</modal-title>
	<modal-ctl-buttons>
		<button type="button" class="btn btn-icon" aria-label="Close"
		        ng-click="swipecardController.cancel()"
		        title="Cancel">
			<i class="icon icon-modal-ctl icon-close"></i>
		</button>
	</modal-ctl-buttons>
	<modal-body>
		<form class="form-horizontal">
			<div class="row">
				<div class="row">
					<ca-info-messages
							ca-errors-object="displayMessages"
							ca-field-value-map="fieldValueMapping"
							ca-prepend-name-to-field-errors="false">
					</ca-info-messages>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<h1>Ready to Swipe Card</h1>
				</div>
				<div class="col-md-12">
					<ca-field-text
							ca-focus-field="swipecardController.focusInput"
							ca-title="Card Data"
							ca-name="card-data"
							ca-model="swipecardController.cardDataString"
							ca-keypress-enter="swipecardController.onEnterKeyDown()"
					>
					</ca-field-text>
				</div>
			</div>
		</form>
	</modal-body>
	<modal-footer>
		<button type="button" class="btn btn-danger" aria-label="Close"
		        ng-click="swipecardController.cancel()"
		        title="Cancel">
			Cancel
		</button>
	</modal-footer>
</juno-modal>