angular.module('Common').directive('caQuill',
	[

			'$timeout',

	function($timeout)
{
		var link_function = function link_function(scope, element, attrs, ngModel)
		{

			var updateModel = function updateModel(value) {
					scope.$apply(function () {
						ngModel.$setViewValue(value);
					});
				},
				options = {
					modules: {
						'toolbar': { container: '.wysiwyg-toolbar' },
						'image-tooltip': true,
						'link-tooltip': true
					},
					theme: 'snow'
				},
				extraOptions = attrs.quill ?
														scope.$eval(attrs.quill) : {},
				editor;

			angular.extend(options, extraOptions);

			$timeout(function () {

				editor = new Quill(element.children()[1], options);

				ngModel.$render();

				editor.on('text-change', function(delta, source) {
					updateModel(this.getHTML());
				});

				/*
				editor.once('selection-change', function(hasFocus) {
					$(editor).toggleClass('focus', hasFocus);
					// Hack for inability to scroll on mobile
					if (/mobile/i.test(navigator.userAgent)) {
						$(editor).css('height', quill.root.scrollHeight + 30)   // 30 for padding
					}
				});
				*/

			});


			ngModel.$render = function () {
				if (angular.isDefined(editor)) {
					$timeout(function() {
						editor.setHTML(ngModel.$viewValue || '');
					});
				}

			};

		};
	return {
		restrict: 'A',
		require: "ngModel",
		replace: true,
		templateUrl: 'src/common/directives/clinicaid/ca_quill.jsp',
		link: link_function
	}
}]);

