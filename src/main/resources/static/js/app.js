(function () {
    var app = angular.module("perftool", ['ui.date','ngRoute']);

    
    app.config(function($routeProvider){
         $routeProvider
            .when('/', {
                controller: 'programController',
                templateUrl: '/views/programSetup.html'
            })
            .when('/promotionSetup', {
                controller: 'promotionController',
                templateUrl: '/views/promotionSetup.html'
            })
            .when('/programSetup',{
                controller : 'programController',
                templateUrl : '/views/programSetup.html'
            })
            .when('/cardSetup',{
                controller : 'cardController',
                templateUrl : '/views/cardSetup.html'
            })
            .when('/offerSetup', {
                controller : 'offerController',
                templateUrl : '/views/offerSetup.html'
            })
            .when('/adminSetup', {
                controller : 'adminController',
                templateUrl : '/views/adminSetup.html'
            })
            .otherwise( { redirectTo: '/' } );
    });
    
    
    
//
//    var panelController = app.controller("panelController", ['$scope','$http', '$log', '$interval','$window','perftoolfactory', function ($scope, $http, $log, $interval, $window,perftoolfactory) {
//        /*Initialize the data*/
//        $scope.programData = {
//            edit: false
//        };
//        $scope.promotionSetupData = {
//            edit: false
//        };
//
//        $scope.cardSetupData = {
//            edit: false
//        };
//
//        $scope.offerSetupData = {
//            edit: false
//        }
//
//        $scope.activityLog = [];
//        $scope.testGoingOn = true;
//
//        $scope.dateOptions = {
//            changeYear: true,
//            changeMonth: true,
//            yearRange: '1900:-0'
//        };
//
//        $scope.CHANNEL_TYPES = ["CATALINA_IN_STORE", "LOAD_TO_CARD"];
//        $scope.BOOLEAN_TYPES = [true, false];
//        $scope.PROMOTION_TYPES = ["StringPrints", "Threshold", "GType", "Transactional"]
//
//
//
//        perftoolfactory.initConfig().then(function (response) {
//            $scope.config = response.data.config;
//            $scope.setup = response.data.server.setup;
//        }, function (response) {
//            $log.error("Problem in calling config resource");
//        });
//
//        $scope.updateConfiguration = function () {
//            $scope.config.server.setup = $scope.setup;
//            $http.post('/update', $scope.config).
//                then(function (response) {
//                    $log.info("Configuration updated successful\n\r");
//                    $scope.activityLog += response.data.status + "\r\n";
//                }, function (response) {
//                    $log.info("Unable to update configuration");
//                });
//        }
//
//        var formatData = function (activityList) {
//            for (var i = 0; i < activityList.length; i++) {
//                var jsonValue = JSON.stringify(activityList[i]);
//                $scope.activityLog += jsonValue + "\r\n";
//            }
//        }
//
//        $scope.publishData = function () {
//            $log.info("Calling publish " + new Date().toLocaleDateString);
//            $http.get("/publish")
//                .then(function (response) {
//                    if (response.data.activityLogList.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.activityLogList, null, 4);
//                        $scope.activityLog += "\n";
//                    }
//
//                    // formatData(response.activityLogList);
//                }, function (response) {
//                    $log.error("Problem in calling publish resource\n\r");
//                    if (response.data.errorLogList.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.errorLogList, null, 4);
//                        $scope.activityLog += "\n";
//                    }
//                });
//        }
//
//        $scope.resetData = function () {
//            $http.get("/reset")
//                .then(function (response) {
//                    if (response.data.activityLogList.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.activityLogList, null, 4);
//                        $scope.activityLog += "\n";
//                    }
//                    //formatData(response.activityLogList);
//                }, function (response) {
//                    $log.error("Problem in calling reset resource\n\r");
//                    if (response.data.activityLogList.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.activityLogList, null, 4);
//                        $scope.activityLog += "\n";
//                    }
//                    //formatData(response.activityLogList);
//                });
//        }
//
//        $scope.stopTest = function () {
//            $http.get("/stop")
//                .then(function (response) {
//                    $scope.activityLog += response.data.status + "\r\n";
//                }, function (response) {
//                    $log.error("Test could not be stopped");
//                    $scope.activityLog += "Test could not be stopped \r\n";
//                });
//        }
//
//        $scope.startTest = function () {
//            $http.get("/start")
//                .then(function (response) {
//                    $scope.activityLog += response.status + "\r\n";
//                    $scope.activityLog += JSON.stringify(response.data.workerList, null, 4);
//                    $scope.activityLog += "\n";
//                    //formatData(response.workerList);
//                }, function (response) {
//                    $log.error("Problem in calling start test resource");
//                    $scope.activityLog("Request to start a test could not be fulfilled\r\n");
//                });
//        }
//
//
//        $scope.getStatus = function () {
//            $http.get("/status")
//                .then(function (response) {
//                    $scope.testGoingOn = response.data.testGoingOn;
//                    if (response.data.workerList.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.workerList, null, 4);
//                        $scope.activityLog += "\n\r";
//                    }
//                    if (response.data.status.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.status, null, 4);
//                        $scope.activityLog += "\n\r";
//                    }
//                    //formatData(response.status);
//                    //formatData(response.workerList);
//                }, function (response) {
//                    $log.error("Problem in calling status resource");
//                    $scope.activityLog += response.data.status + "\r\n";
//                    formatData(response.workerList);
//                });
//        }
//
//        $scope.checkStatus = function () {
//            $http.get("/checkStatus")
//                .then(function (response) {
//                    $scope.testGoingOn = response.data.testGoingOn;
//                    if (response.data.workerList.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.workerList, null, 4);
//                        $scope.activityLog += "\n";
//                    }
//                    if (response.data.status.length > 0) {
//                        $scope.activityLog += JSON.stringify(response.data.status, null, 4);
//                        $scope.activityLog += "\n";
//                    }
//                }, function (response) {
//                    $log.error("Problem in calling status resource");
//                    $scope.activityLog += response.data.status + "\r\n";
//                    formatData(response.data.workerList);
//                });
//        }
//
//        /**/
//        $scope.editProgramSetup = function (index) {
//            $log.info("Index selected " + index);
//            angular.copy($scope.setup.programSetup[index], $scope.programData);
//            $scope.programData['index'] = index;
//            $scope.programData.edit = true;
//        }
//
//        /*  Edit promotion setup
//         *
//         */
//        $scope.editPromotionSetup = function (index) {
//            $log.info("Index selected " + index);
//            angular.copy($scope.setup.promotionSetup[index], $scope.promotionSetupData);
//            $scope.promotionSetupData['index'] = index;
//            $scope.promotionSetupData['endDate'] = new Date($scope.setup.promotionSetup[index].endDate);
//            $scope.promotionSetupData['startDate'] = new Date($scope.setup.promotionSetup[index].startDate);
//            $scope.promotionSetupData.edit = true;
//        }
//
//        /**/
//        $scope.editCardRangeSetup = function (index) {
//            $log.info("Index selected " + index);
//            angular.copy($scope.setup.cardSetup[index], $scope.cardSetupData);
//            $scope.cardSetupData['index'] = index;
//            $scope.cardSetupData.edit = true;
//        }
//
//        $scope.newPromotionSetup = function () {
//            $scope.promotionSetupData = {};
//            $scope.promotionSetupData['edit'] = false;
//            $scope.promotionSetupData['promotionType'] = $scope.PROMOTION_TYPES[3];
//            $scope.promotionSetupData['mediaCapDisabled'] = $scope.BOOLEAN_TYPES[1];
//            $scope.promotionSetupData['channelCapDisabled'] = $scope.BOOLEAN_TYPES[1];
//            $scope.promotionSetupData['unlimited'] = $scope.BOOLEAN_TYPES[1];
//            $scope.promotionSetupData['houseHolded'] = $scope.BOOLEAN_TYPES[1];
//            $scope.promotionSetupData['channelType'] = $scope.CHANNEL_TYPES[0];
//        }
//
//        $scope.newProgramSetup = function () {
//            $log.info("Clearing data");
//            $scope.programData = {};
//            $scope.programData['edit'] = false;
//        }
//
//        $scope.newCardRange = function () {
//            $scope.cardSetupData = {};
//            $scope.cardSetupData['edit'] = false;
//        }
//
//        $scope.deleteProgram = function (index) {
//            $log.info("Deleting program");
//            $scope.programData = {};
//            clearModel($scope.programData);
//            $scope.setup.programSetup.splice(index, 1);
//        }
//
//        $scope.deletePromotionData = function (index) {
//            $log.info("Deleting promotion setup");
//            $scope.promotionSetupData = {};
//            clearModel($scope.promotionSetupData);
//            $scope.setup.promotionSetup.splice(index, 1);
//        }
//
//        $scope.deleteCardRangeSetup = function (index) {
//            $log.info("Deleting card setup");
//            $scope.cardSetupData = {};
//            clearModel($scope.cardSetupData);
//            $scope.setup.cardSetup.splice(index, 1);
//        }
//
//        $scope.saveProgramData = function () {
//            if ($scope.programData.edit == false) {
//                $scope.setup.programSetup.push({
//                    'contractId': $scope.programData.contractId,
//                    'programId': $scope.programData.programId,
//                    'cap': $scope.programData.cap,
//                    'variance': $scope.programData.variance
//                });
//            } else {
//                angular.copy($scope.programData, $scope.setup.programSetup[$scope.programData.index]);
//            }
//
//            clearModel($scope.programData);
//        };
//
//        $scope.saveCardRangeData = function () {
//            if ($scope.cardSetupData.edit == false) {
//                $scope.setup.cardSetup.push({
//                    'cardRange': $scope.cardSetupData.cardRange,
//                    'cardRangeId': $scope.cardSetupData.cardRangeId,
//                });
//            } else {
//                angular.copy($scope.cardSetupData, $scope.setup.cardSetup[$scope.cardSetupData.index]);
//            }
//            clearModel($scope.cardSetupData);
//        };
//
//
//        $scope.savePromotionSetup = function () {
//            if ($scope.promotionSetupData.edit == false) {
//                if ($scope.promotionSetupData.rank == undefined) {
//                    $scope.promotionSetupData.rank = 256;
//                }
//
//                $scope.setup.promotionSetup.push({
//                    'awardId': $scope.promotionSetupData.awardId,
//                    'rank': $scope.promotionSetupData.rank,
//                    'mediaId': $scope.promotionSetupData.mediaId,
//                    'channelMediaId': $scope.promotionSetupData.channelMediaId,
//                    'awardCap': $scope.promotionSetupData.awardCap,
//                    'awardVariance': $scope.promotionSetupData.awardVariance,
//                    'controlPercentage': $scope.promotionSetupData.controlPercentage,
//                    'randomValue': $scope.promotionSetupData.randomValue,
//                    'mediaCap': $scope.promotionSetupData.mediaCap,
//                    'campaignId': $scope.promotionSetupData.campaignId,
//                    'mediaVariance': $scope.promotionSetupData.mediaVariance,
//                    'mediaCapDisabled': $scope.promotionSetupData.mediaCapDisabled,
//                    'channelMediaCap': $scope.promotionSetupData.channelMediaCap,
//                    'channelMediaVariance': $scope.promotionSetupData.channelMediaVariance,
//                    'channelCapDisabled': $scope.promotionSetupData.channelCapDisabled,
//                    'unlimited': $scope.promotionSetupData.unlimited,
//                    'thresholdSequence': $scope.promotionSetupData.thresholdSequence,
//                    'promotionType': $scope.promotionSetupData.promotionType,
//                    'houseHolded': $scope.promotionSetupData.houseHolded,
//                    'channelType': $scope.promotionSetupData.channelType,
//                    'consumerCap': $scope.promotionSetupData.consumerCap,
//                    'startDate': $scope.promotionSetupData.startDate,
//                    'endDate': $scope.promotionSetupData.endDate,
//                    'cardRangeId': $scope.promotionSetupData.cardRangeId,
//                    'programSetupId': $scope.promotionSetupData.programSetupId
//                });
//
//            } else {
//                angular.copy($scope.promotionSetupData, $scope.setup.promotionSetup[$scope.promotionSetupData.index]);
//            }
//            clearModel($scope.promotionSetupData);
//        };
//
//        $scope.clonePromotion = function (index) {
//            $log.info("Cloning promotion setup " + index);
//            $scope.promotionSetupData = {};
//            var temp = {};
//            angular.copy($scope.setup.promotionSetup[index], temp);
//            $scope.setup.promotionSetup.splice(index + 1, 0, temp);
//            angular.copy($scope.setup.promotionSetup[index + 1], $scope.promotionSetupData);
//            $scope.promotionSetupData['index'] = index + 1;
//            $scope.promotionSetupData['edit'] = true;
//        }
//
//        $scope.editOfferSetup = function (index) {
//            $log.info("Index selected " + index);
//            $scope.offerSetupData['index'] = index;
//            $scope.offerSetupData.edit = true;
//            $scope.offerSetupData.campaignId = $scope.setup.offerSetup[index].campaignId;
//            $scope.offerSetupData.offerList = $scope.setup.offerSetup[index].offerList.join();
//        }
//
//        $scope.newOfferSetup = function () {
//            $scope.offerSetupData = {};
//            $scope.offerSetupData['edit'] = false;
//        }
//
//        $scope.deleteOfferSetup = function (index) {
//            $log.info("Deleting offer setup");
//            $scope.offerSetupData = {};
//            clearModel($scope.offerSetupData);
//            $scope.setup.offerSetup.splice(index, 1);
//        }
//
//        $scope.saveOfferSetup = function () {
//            if ($scope.offerSetupData.edit == false) {
//                $scope.setup.offerSetup.push({
//                    'campaignId': $scope.offerSetupData.campaignId,
//                    'offerList': $scope.offerSetupData.offerList.split(",")
//                });
//            } else {
//                $scope.setup.offerSetup[$scope.offerSetupData.index].campaignId = $scope.offerSetupData.campaignId;
//                $scope.setup.offerSetup[$scope.offerSetupData.index].offerList = $scope.offerSetupData.offerList.split(",");
//            }
//            clearModel($scope.offerSetupData);
//        };
//
//
//        /*Private function which clears the model*/
//        var clearModel = function (obj) {
//            for (attrib in obj) {
//                obj[attrib] = '';
//            }
//        }
//    }]);
})();