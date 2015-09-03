'use strict';

angular.module('ProcessInstances')

        .factory('ProcessInstanceService',
        ['KieServerInfoService', '$http', '$rootScope',
            function (KieServerInfoService, $http, $rootScope) {
                var service = {};

                service.GetInstances = function (callback) {

                    $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/queries/processes/instances?status=1&status=2&status=3"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 200, message : status, data : data['process-instance']};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.GetInstance = function (containerId, processInstanceId, callback) {

                    $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/processes/instances/"+processInstanceId+"?withVars=true"}).
                            success(function(data, status, headers, config) {
                                var response = { success: status == 200, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.StartInstance = function (containerId, processId, callback) {

                    $http({method: 'POST', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/processes/" + processId + "/instances/"}).
                            success(function(data, status, headers, config) {
                                var response = { success: status == 201, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.AbortInstance = function (containerId, processInstanceId, callback) {

                    $http({method: 'DELETE', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/processes/instances/"+processInstanceId}).
                            success(function(data, status, headers, config) {
                                var response = { success: status == 204, message : status};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.convertStatus = function (status) {
                    if (status == 1) {
                        return 'Active';
                    }
                    if (status == 2) {
                        return 'Completed';
                    }
                    if (status == 3) {
                        return 'Aborted';
                    }
                };


                return service;
            }]);