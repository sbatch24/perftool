(function() {
    
    var UPDATE_URL = "/update";
    var adminController = function($scope, configService, $log) {
        
        $scope.updateConfiguration = function () {
            var updateConfig = configService.getConfig();
            updateConfig.server.setup = configService.getModel();
            configService.updateConfig(UPDATE_URL, updateConfig).then(function (response) {
                $log.info("Configuration updated successful\n\r");
                $scope.activityLog += response.data.status + "\r\n";
            }, function (response) {
                $log.info("Unable to update configuration");
            });
        }
    }
    
    angular.module("perftool").controller('adminController', adminController);
    adminController.$inject = ['$scope', 'configService', '$log'];
    
})();