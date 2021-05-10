angular.module('Record.Details').component('rosteredHistoryModal', {
    templateUrl: 'src/record/details/components/rosteredHistory/rosteredHistoryModal.jsp',
    bindings: {
        modalInstance: "<",
        resolve: "<"
    },
    controller: [
        "$scope",
        "$http",
        "$httpParamSerializer",
        function(
            $scope,
            $http,
            $httpParamSerializer
        )
    {
        let ctrl = this;

    }]
});