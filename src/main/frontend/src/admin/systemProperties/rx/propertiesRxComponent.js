angular.module('Admin').component('systemPropertiesRx',
    {
        templateUrl: 'src/admin/systemProperties/rx/propertiesRx.jsp',
        bindings: {},
        controller: ['$scope', '$http', '$httpParamSerializer', '$state', function ($scope, $http, $httpParamSerializer, $state)
        {
            let ctrl = this;
            ctrl.message = "foobar";

            ctrl.$onInit = () => {
                console.log("hello from rx");
            };

            ctrl.changeTab = function (state)
            {
                $state.go(state);
            };

            ctrl.isTabActive = function(tabState)
            {
                return tabState === $state.current.name;
            };
        }]
    });