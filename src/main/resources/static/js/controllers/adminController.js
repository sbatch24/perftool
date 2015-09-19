(function() {
    
    var UPDATE_URL = "/update";
    var RESET_URL = "/reset";
    var PUBLISH_URL = "/publish";
    var START_URL = "/start";
    var STOP_URL = "/stop";
    var STATUS_URL = "/status";
    
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
        
        $scope.publishData = function () {
            $log.info("Calling publish " + new Date().toLocaleDateString);
            configService.executeTask(PUBLISH_URL)
                .then(function (response) {
                    if (response.data.activityLogList.length > 0) {
                        $scope.activityLog += JSON.stringify(response.data.activityLogList, null, 4);
                        $scope.activityLog += "\n";
                    }
                }, function (response) {
                    $log.error("Problem in calling publish resource\n\r");
                    if (response.data.errorLogList.length > 0) {
                        $scope.activityLog += JSON.stringify(response.data.errorLogList, null, 4);
                        $scope.activityLog += "\n";
                    }
                });
        }

        $scope.resetData = function () {
            configService.executeTask(RESET_URL)
                .then(function (response) {
                    if (response.data.activityLogList.length > 0) {
                        $scope.activityLog += JSON.stringify(response.data.activityLogList, null, 4);
                        $scope.activityLog += "\n";
                    }
                }, function (response) {
                    $log.error("Problem in calling reset resource\n\r");
                    if (response.data.activityLogList.length > 0) {
                        $scope.activityLog += JSON.stringify(response.data.activityLogList, null, 4);
                        $scope.activityLog += "\n";
                    }
                });
        }

        $scope.stopTest = function () {
            configService.executeTask(STOP_URL)
                .then(function (response) {
                    $scope.activityLog += response.data.status + "\r\n";
                }, function (response) {
                    $log.error("Test could not be stopped");
                    $scope.activityLog += "Test could not be stopped \r\n";
                });
        }

        $scope.startTest = function () {
            configService.executeTask(START_URL)
                .then(function (response) {
                    $scope.activityLog += response.status + "\r\n";
                    $scope.activityLog += JSON.stringify(response.data.workerList, null, 4);
                    $scope.activityLog += "\n";
                }, function (response) {
                    $log.error("Problem in calling start test resource");
                    $scope.activityLog("Request to start a test could not be fulfilled\r\n");
                });
        }


        $scope.getStatus = function () {
            configService.executeTask(STATUS_URL)
                .then(function (response) {
                    $scope.testGoingOn = response.data.testGoingOn;
                    if (response.data.workerList.length > 0) {
                        $scope.activityLog += JSON.stringify(response.data.workerList, null, 4);
                        $scope.activityLog += "\n\r";
                    }
                    if (response.data.status.length > 0) {
                        $scope.activityLog += JSON.stringify(response.data.status, null, 4);
                        $scope.activityLog += "\n\r";
                    }
                }, function (response) {
                    $log.error("Problem in calling status resource");
                    $scope.activityLog += response.data.status + "\r\n";
                    formatData(response.workerList);
                });
        }
    }
    
    angular.module("perftool").controller('adminController', adminController);
    adminController.$inject = ['$scope', 'configService', '$log'];
    
})();