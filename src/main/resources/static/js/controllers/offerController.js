(function(){
    
    var offerController = function($scope, configService, $log) {
        
        $scope.offerSetupData = {
          edit: false
        }
        
         /**
         *  Retrieve the setup object which contains the configuration information.
         */
        if(configService.getModel() != undefined) {
            $scope.setup = configService.getModel();
        } else {
            $log.error("Configuration object cannot be null at this stage.");
        }
        
        $scope.editOfferSetup = function (index) {
            $log.info("Index selected " + index);
            $scope.offerSetupData['index'] = index;
            $scope.offerSetupData.edit = true;
            $scope.offerSetupData.campaignId = $scope.setup.offerSetup[index].campaignId;
            $scope.offerSetupData.offerList = $scope.setup.offerSetup[index].offerList.join();
        }

        $scope.newOfferSetup = function () {
            $scope.offerSetupData = {};
            $scope.offerSetupData['edit'] = false;
        }

        $scope.deleteOfferSetup = function (index) {
            $log.info("Deleting offer setup");
            $scope.offerSetupData = {};
            configService.clearModel($scope.offerSetupData);
            $scope.setup.offerSetup.splice(index, 1);
        }

        $scope.saveOfferSetup = function () {
            if ($scope.offerSetupData.edit == false) {
                $scope.setup.offerSetup.push({
                    'campaignId': $scope.offerSetupData.campaignId,
                    'offerList': $scope.offerSetupData.offerList.split(",")
                });
            } else {
                $scope.setup.offerSetup[$scope.offerSetupData.index].campaignId = $scope.offerSetupData.campaignId;
                $scope.setup.offerSetup[$scope.offerSetupData.index].offerList = $scope.offerSetupData.offerList.split(",");
            }
            configService.clearModel($scope.offerSetupData);
        }
        
        $scope.$on("$destroy", function() {
            configService.setModel($scope.setup);
        });
    }
    
    angular.module("perftool").controller('offerController',offerController);
    offerController.$inject = ['$scope','configService','$log'];   
})();