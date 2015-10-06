(function(){
    var pimController = function($scope, configService, $log) {
        
        /**
         * [[Description]]
         * @param {[[Type]]} value [[Description]]
         */
        $scope.publishPimData = function () {
             var data = JSON.parse($scope.pimMessage);
             configService.postPimMessage("/publishPimData", data).then(function (response) {
                    $scope.activityLog += response.data.status + "\r\n";
                }, function (response) {
                    $log.error("Problem posting pim message");
                    $scope.activityLog += "Problem posting pim message \r\n";
                });
        }
    }
    
    angular.module("perftool").controller('pimController', pimController);
    pimController.$inject = ['$scope', 'configService', '$log'];
    
})();