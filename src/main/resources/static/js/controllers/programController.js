(function(){
    var CONFIG_URL = "/config";
    
    
    var programController = function ($scope,configService,$log) {
        $scope.programData = {
           edit: false
        };
        
        if(configService.getModel() == undefined) {
            configService.getConfigFromServer(CONFIG_URL).then(function(response){            
            $scope.setup = response.data.server.setup;
            $scope.config = response.data;
        },function(response){
            
        });
        } else {
            $scope.setup = configService.getModel();
        }      
        
        $scope.editProgramSetup = function (index) {
            $log.info("Index selected " + index);
            angular.copy($scope.setup.programSetup[index], $scope.programData);
            $scope.programData['index'] = index;
            $scope.programData.edit = true;
        }
        
        $scope.newProgramSetup = function () {
            $log.info("Clearing data");
            $scope.programData = {};
            $scope.programData['edit'] = false;
        }
        
        $scope.deleteProgram = function (index) {
            $log.info("Deleting program");
            $scope.programData = {};
            configService.clearModel($scope.programData);
            $scope.setup.programSetup.splice(index, 1);
        }
        
        $scope.saveProgramData = function () {
            if ($scope.programData.edit == false) {
                $scope.setup.programSetup.push({
                    'contractId': $scope.programData.contractId,
                    'programId': $scope.programData.programId,
                    'cap': $scope.programData.cap,
                    'variance': $scope.programData.variance
                });
            } else {
                angular.copy($scope.programData, $scope.setup.programSetup[$scope.programData.index]);
            }

            configService.clearModel($scope.programData);
        };
        
        $scope.$on("$destroy", function() {
            configService.setModel($scope.setup);
            configService.setConfig($scope.config);
        });
    }
    
    angular.module("perftool").controller('programController', programController);    
    programController.$inject = ['$scope', 'configService','$log'];
})();