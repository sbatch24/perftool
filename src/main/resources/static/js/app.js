    (function() {
        var app = angular.module("perftool", []);


        var panelController = app.controller("panelController", function($scope, $http, $log, $interval) {
            /*Initialize the data*/
            $scope.programData = {
                edit: false
            };
            $scope.promotionSetupData = {
                edit: false
            };

            $scope.cardSetupData = {
                edit: false
            };

            $scope.offerSetupData = {
                edit: false
            }

            $scope.activityLog = [];
            $scope.testGoingOn = true;

            $scope.CHANNEL_TYPES = ["CATALINA_IN_STORE", "LOAD_TO_CARD"];
            $scope.BOOLEAN_TYPES = [true, false];
            $scope.PROMOTION_TYPES = ["STRINGPRINTS", "THRESHOLDS"]

            //        $interval(function() {
            //            $scope.checkStatus();
            //        }, 5000);

            $http.get("/config")
                .success(function(response) {
                    $scope.config = response;
                    $scope.setup = response.server.setup;
                }).error(function(response) {
                    $log.error("Problem in calling config resource");
                });

            $scope.updateConfiguration = function() {
                $scope.config.server.setup = $scope.setup;
                $http.post('/update', $scope.config).
                success(function(data, status, headers, config) {
                    $log.info("Configuration updated successful\n\r");
                    $scope.activityLog += data.status + "\r\n";
                }).
                error(function(data, status, headers, config) {
                    $log.info("Unable to update configuration");
                });
            }

            var formatData = function(activityList) {
                for (var i = 0; i < activityList.length; i++) {
                    var jsonValue = JSON.stringify(activityList[i]);
                    $scope.activityLog += jsonValue + "\r\n";
                }
            }

            $scope.publishData = function() {
                $http.get("/publish")
                    .success(function(response) {
                        if (response.activityLogList.length > 0) {
                            $scope.activityLog += JSON.stringify(response.activityLogList, null, 4);
                            $scope.activityLog += "\n";
                        }

                        // formatData(response.activityLogList);
                    }).error(function(response) {
                        $log.error("Problem in calling publish resource\n\r");
                        if (response.activityLogList.length > 0) {
                            $scope.activityLog += JSON.stringify(response.activityLogList, null, 4);
                            $scope.activityLog += "\n";
                        }
                    });
            }

            $scope.resetData = function() {
                $http.get("/reset")
                    .success(function(response) {
                        if (response.activityLogList.length > 0) {
                            $scope.activityLog += JSON.stringify(response.activityLogList, null, 4);
                            $scope.activityLog += "\n";
                        }
                        //formatData(response.activityLogList);
                    }).error(function(response) {
                        $log.error("Problem in calling reset resource\n\r");
                        if (response.activityLogList.length > 0) {
                            $scope.activityLog += JSON.stringify(response.activityLogList, null, 4);
                            $scope.activityLog += "\n";
                        }
                        //formatData(response.activityLogList);
                    });
            }

            $scope.stopTest = function() {
                $http.get("/stop")
                    .success(function(response) {
                        $scope.activityLog += response.status + "\r\n";
                    }).error(function(response) {
                        $log.error("Test could not be stopped");
                        $scope.activityLog += "Test could not be stopped \r\n";
                    });
            }

            $scope.startTest = function() {
                $http.get("/start")
                    .success(function(response) {
                        $scope.activityLog += response.status + "\r\n";
                        $scope.activityLog += JSON.stringify(response.workerList, null, 4);
                        $scope.activityLog += "\n";
                        //formatData(response.workerList);
                    }).error(function(response) {
                        $log.error("Problem in calling start test resource");
                        $scope.activityLog("Request to start a test could not be fulfilled\r\n");
                    });
            }


            $scope.getStatus = function() {
                $http.get("/status")
                    .success(function(response) {
                        $scope.testGoingOn = response.testGoingOn;
                        if (response.workerList.length > 0) {
                            $scope.activityLog += JSON.stringify(response.workerList, null, 4);
                            $scope.activityLog += "\n\r";
                        }
                        if (response.status.length > 0) {
                            $scope.activityLog += JSON.stringify(response.status, null, 4);
                            $scope.activityLog += "\n\r";
                        }
                        //formatData(response.status);
                        //formatData(response.workerList);
                    }).error(function(response) {
                        $log.error("Problem in calling status resource");
                        $scope.activityLog += response.status + "\r\n";
                        formatData(response.workerList);
                    });
            }

            $scope.checkStatus = function() {
                $http.get("/checkStatus")
                    .success(function(response) {
                        $scope.testGoingOn = response.testGoingOn;
                        if (response.workerList.length > 0) {
                            $scope.activityLog += JSON.stringify(response.workerList, null, 4);
                            $scope.activityLog += "\n";
                        }
                        if (response.status.length > 0) {
                            $scope.activityLog += JSON.stringify(response.status, null, 4);
                            $scope.activityLog += "\n";
                        }
                    }).error(function(response) {
                        $log.error("Problem in calling status resource");
                        $scope.activityLog += response.status + "\r\n";
                        formatData(response.workerList);
                    });
            }

            /**/
            $scope.editProgramSetup = function(index) {
                $log.info("Index selected " + index);
                $scope.programData['index'] = index;
                $scope.programData.edit = true;
                $scope.programData.programId = $scope.setup.programSetup[index].programId;
                $scope.programData.contractId = $scope.setup.programSetup[index].contractId;
                $scope.programData.cap = $scope.setup.programSetup[index].cap;
                $scope.programData.variance = $scope.setup.programSetup[index].variance;
            }

            /*  Edit promotion setup 
             * 
             */
            $scope.editPromotionSetup = function(index) {
                $log.info("Index selected " + index);
                $scope.promotionSetupData.edit = true;
                $scope.promotionSetupData['index'] = index;
                $scope.promotionSetupData.awardId = $scope.setup.promotionSetup[index].awardId;
                $scope.promotionSetupData.awardCap = $scope.setup.promotionSetup[index].awardCap;
                $scope.promotionSetupData.awardVariance = $scope.setup.promotionSetup[index].awardVariance;
                $scope.promotionSetupData.controlPercentage = $scope.setup.promotionSetup[index].controlPercentage;
                $scope.promotionSetupData.randomValue = $scope.setup.promotionSetup[index].randomValue;
                
                $scope.promotionSetupData.mediaId = $scope.setup.promotionSetup[index].mediaId;
                $scope.promotionSetupData.mediaCap = $scope.setup.promotionSetup[index].mediaCap;
                $scope.promotionSetupData.mediaVariance = $scope.setup.promotionSetup[index].mediaVariance;
                
                $scope.promotionSetupData.channelMediaId = $scope.setup.promotionSetup[index].channelMediaId;
                $scope.promotionSetupData.channelMediaCap = $scope.setup.promotionSetup[index].channelMediaCap;
                $scope.promotionSetupData.channelMediaVariance = $scope.setup.promotionSetup[index].channelMediaVariance;
                
                $scope.promotionSetupData.houseHolded = $scope.setup.promotionSetup[index].houseHolded;
                $scope.promotionSetupData.thresholdSequence = $scope.setup.promotionSetup[index].thresholdSequence;
                $scope.promotionSetupData.promotionType = $scope.setup.promotionSetup[index].promotionType;
                
                $scope.promotionSetupData.unlimited = $scope.setup.promotionSetup[index].unlimited;
                
                $scope.promotionSetupData.channelType = $scope.setup.promotionSetup[index].channelType;
                $scope.promotionSetupData.programSetupId = $scope.setup.promotionSetup[index].programSetupId;
                
                $scope.promotionSetupData.cardRangeId = $scope.setup.promotionSetup[index].cardRangeId;
                $scope.promotionSetupData.consumerCap = $scope.setup.promotionSetup[index].consumerCap;
                $scope.promotionSetupData.startDate = $scope.setup.promotionSetup[index].startDate;
                $scope.promotionSetupData.endDate = $scope.setup.promotionSetup[index].endDate;
            }

            /**/
            $scope.editCardRangeSetup = function(index) {
                $log.info("Index selected " + index);
                $scope.cardSetupData['index'] = index;
                $scope.cardSetupData.edit = true;
                $scope.cardSetupData.cardRangeId = $scope.setup.cardSetup[index].cardRangeId;
                $scope.cardSetupData.cardRange = $scope.setup.cardSetup[index].cardRange;

            }

            $scope.newPromotionSetup = function() {
                $scope.promotionSetupData = {};
                $scope.promotionSetupData['edit'] = false;
            }

            $scope.newProgramSetup = function() {
                $log.info("Clearing data");
                $scope.programData = {};
                $scope.programData['edit'] = false;
            }

            $scope.newCardRange = function() {
                $scope.cardSetupData = {};
                $scope.cardSetupData['edit'] = false;
            }

            $scope.deleteProgram = function(index) {
                $log.info("Deleting program");
                $scope.programData = {};
                clearModel($scope.programData);
                $scope.setup.programSetup.splice(index, 1);
            }

            $scope.deletePromotionData = function(index) {
                $log.info("Deleting promotion setup");
                $scope.promotionSetupData = {};
                clearModel($scope.promotionSetupData);
                $scope.setup.promotionSetup.splice(index, 1);
            }

            $scope.deleteCardRangeSetup = function(index) {
                $log.info("Deleting card setup");
                $scope.cardSetupData = {};
                clearModel($scope.cardSetupData);
                $scope.setup.cardSetup.splice(index, 1);
            }

            $scope.saveProgramData = function() {
                if ($scope.programData.edit == false) {
                    $scope.setup.programSetup.push({
                        'contractId': $scope.programData.contractId,
                        'programId': $scope.programData.programId,
                        'cap': $scope.programData.cap,
                        'variance': $scope.programData.variance
                    });
                } else {
                    $scope.setup.programSetup[$scope.programData.index].contractId = $scope.programData.contractId;
                    $scope.setup.programSetup[$scope.programData.index].programId = $scope.programData.programId;
                    $scope.setup.programSetup[$scope.programData.index].cap = $scope.programData.cap;
                    $scope.setup.programSetup[$scope.programData.index].variance = $scope.programData.variance;
                }

                clearModel($scope.programData);
            };

            $scope.saveCardRangeData = function() {
                if ($scope.cardSetupData.edit == false) {
                    $scope.setup.cardSetup.push({
                        'cardRange': $scope.cardSetupData.cardRange,
                        'cardRangeId': $scope.cardSetupData.cardRangeId,
                    });
                } else {
                    $scope.setup.cardSetup[$scope.cardSetupData.index].cardRange = $scope.cardSetupData.cardRange;
                    $scope.setup.cardSetup[$scope.cardSetupData.index].cardRangeId = $scope.cardSetupData.cardRangeId;
                }
                clearModel($scope.cardSetupData);
            };


            $scope.savePromotionSetup = function() {
                if ($scope.promotionSetupData.edit == false) {
                    $scope.setup.promotionSetup.push({
                        'awardId': $scope.promotionSetupData.awardId,
                        'mediaId': $scope.promotionSetupData.mediaId,
                        'channelMediaId' : $scope.promotionSetupData.channelMediaId,
                        'awardCap': $scope.promotionSetupData.awardCap,
                        'awardVariance': $scope.promotionSetupData.awardVariance,
                        'controlPercentage' : $scope.promotionSetupData.controlPercentage,
                        'randomValue' : $scope.promotionSetupData.randomValue,
                        'mediaCap': $scope.promotionSetupData.mediaCap,
                        'mediaVariance': $scope.promotionSetupData.mediaVariance,
                        'channelMediaCap' : $scope.promotionSetupData.channelMediaCap,
                        'channelMediaVariance' : $scope.promotionSetupData.channelMediaVariance,
                        'unlimited' : $scope.promotionSetupData.unlimited,
                        'thresholdSequence' : $scope.promotionSetupData.thresholdSequence,
                        'promotionType' : $scope.promotionSetupData.promotionType,
                        'houseHolded' : $scope.promotionSetupData.houseHolded,
                        'channelType': $scope.promotionSetupData.channelType,
                        'consumerCap': $scope.promotionSetupData.consumerCap,
                        'startDate': $scope.promotionSetupData.startDate,
                        'endDate': $scope.promotionSetupData.endDate,
                        'cardRangeId': $scope.promotionSetupData.cardRangeId,
                        'programSetupId': $scope.promotionSetupData.programSetupId
                    });

                } else {
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].awardId = $scope.promotionSetupData.awardId;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].mediaId = $scope.promotionSetupData.mediaId;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].channelMediaId =           $scope.promotionSetupData.channelMediaId;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].awardCap = $scope.promotionSetupData.awardCap;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].awardVariance = $scope.promotionSetupData.awardVariance;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].controlPercentage = $scope.promotionSetupData.controlPercentage;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].randomValue = $scope.promotionSetupData.randomValue;
                    
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].mediaCap = $scope.promotionSetupData.mediaCap;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].mediaVariance = $scope.promotionSetupData.mediaVariance;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].channelMediaCap = $scope.promotionSetupData.channelMediaCap;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].channelMediaVariance = $scope.promotionSetupData.channelMediaVariance;
                    
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].unlimited = $scope.promotionSetupData.unlimited;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].thresholdSequence = $scope.promotionSetupData.thresholdSequence;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].houseHolded = $scope.promotionSetupData.houseHolded;
                    
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].channelType = $scope.promotionSetupData.channelType;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].consumerCap = $scope.promotionSetupData.consumerCap;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].startDate = $scope.promotionSetupData.startDate;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].endDate = $scope.promotionSetupData.endDate;
                    $scope.setup.promotionSetup[$scope.promotionSetupData.index].cardRangeId = $scope.promotionSetupData.cardRangeId,
                        $scope.setup.promotionSetup[$scope.promotionSetupData.index].programSetupId = $scope.promotionSetupData.programSetupId;

                }
                clearModel($scope.promotionSetupData);

            };


            $scope.editOfferSetup = function(index) {
                $log.info("Index selected " + index);
                $scope.offerSetupData['index'] = index;
                $scope.offerSetupData.edit = true;
                $scope.offerSetupData.campaignId = $scope.setup.offerSetup[index].campaignId;
                $scope.offerSetupData.offerList = $scope.setup.offerSetup[index].offerList.join();
            }

            $scope.newOfferSetup = function() {
                $scope.offerSetupData = {};
                $scope.offerSetupData['edit'] = false;
            }

            $scope.deleteOfferSetup = function(index) {
                $log.info("Deleting offer setup");
                $scope.offerSetupData = {};
                clearModel($scope.offerSetupData);
                $scope.setup.offerSetup.splice(index, 1);
            }

            $scope.saveOfferSetup = function() {
                if ($scope.offerSetupData.edit == false) {
                    $scope.setup.offerSetup.push({
                        'campaignId': $scope.offerSetupData.campaignId,
                        'offerList': $scope.offerSetupData.offerList.split(",")
                    });
                } else {
                    $scope.setup.offerSetup[$scope.offerSetupData.index].campaignId = $scope.offerSetupData.campaignId;
                    $scope.setup.offerSetup[$scope.offerSetupData.index].offerList = $scope.offerSetupData.offerList.split(",");
                }
                clearModel($scope.cardSetupData);
            };


            /*Private function which clears the model*/
            var clearModel = function(obj) {
                for (attrib in obj) {
                    obj[attrib] = '';
                }
            }
        });
    })();