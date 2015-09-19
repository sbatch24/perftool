(function() {
    
    /**
     * Card controller handles the card view and any action that user performs on this view.
     */
    var cardController = function($scope,configService, $log) {
        
        // Initialize cardsetup data.
        $scope.cardSetupData = {
            edit: false
        };
        
        /**
         *  Retrieve the setup object which contains the configuration information.
         */
        if(configService.getModel() != undefined) {
            $scope.setup = configService.getModel();
        } else {
            $log.error("Configuration object cannot be null at this stage.");
        }

        $scope.editCardRangeSetup = function (index) {
            $log.info("Index selected " + index);
            angular.copy($scope.setup.cardSetup[index], $scope.cardSetupData);
            $scope.cardSetupData['index'] = index;
            $scope.cardSetupData.edit = true;
        }
        
        /**
         * Function gets called when the new card setup button is clicked.
         */
        $scope.newCardRange = function () {
            $scope.cardSetupData = {};
            $scope.cardSetupData['edit'] = false;
        }
        
        /**
         * Function deletes the card setup from the view and from the model.
         */
        $scope.deleteCardRangeSetup = function (index) {
            $log.info("Deleting card setup");
            $scope.cardSetupData = {};
            configService.clearModel($scope.cardSetupData);
            $scope.setup.cardSetup.splice(index, 1);
        }
        
        /**
         * Function saves the newly added card range data.
         */
        $scope.saveCardRangeData = function () {
            if ($scope.cardSetupData.edit == false) {
                $scope.setup.cardSetup.push({
                    'cardRange': $scope.cardSetupData.cardRange,
                    'cardRangeId': $scope.cardSetupData.cardRangeId,
                });
            } else {
                angular.copy($scope.cardSetupData, $scope.setup.cardSetup[$scope.cardSetupData.index]);
            }
            configService.clearModel($scope.cardSetupData);
        } 
        
        $scope.$on("$destroy", function() {
            configService.setModel($scope.setup);
        });
    }
    
    angular.module("perftool").controller('cardController', cardController);
    cardController.$inject = ['$scope','configService','$log'];
})();