'use strict';

angular.module('Processes')

.factory('ProcessService',
        ['KieServerInfoService', '$http', '$rootScope',
            function (KieServerInfoService, $http, $rootScope) {
                var service = {};

                service.GetDefinitions = function (callback) {

                    $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/queries/processes/definitions"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 200, message : status, data : data['processes']};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.GetDefinition = function (containerId, processId, callback) {

                    $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/processes/definitions/" + processId}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 200, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.NewInstance = function (containerId, processId, data, callback) {

                    $http({method: 'POST',
                        url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/processes/" + processId + "/instances",
                        data : data}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 201, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };


                return service;
            }]);