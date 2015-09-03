'use strict';

angular.module('TaskInstances')

        .factory('TaskInstanceService',
        ['KieServerInfoService', '$http', '$rootScope',
            function (KieServerInfoService, $http, $rootScope) {
                var service = {};

                service.GetTaskDefOutputs = function (containerId, processId, taskName, callback) {

                    $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/processes/definitions/"+ processId + "/tasks/users/" + taskName + "/outputs"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 200, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.GetInstances = function (callback) {

                    $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/queries/tasks/instances/pot-owners?status=Ready&status=Reserved&status=InProgress"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 200, message : status, data : data['task-summary']};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.GetInstance = function (containerId, taskId, callback) {

                    $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/tasks/" + taskId + "?withInputData=true&withOutputData=true"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 200, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.ClaimInstance = function (containerId, taskId, callback) {

                    $http({method: 'PUT', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/tasks/" + taskId + "/states/claimed"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 201, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.StartInstance = function (containerId, taskId, callback) {

                    $http({method: 'PUT', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/tasks/" + taskId + "/states/started"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 201, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.ReleaseInstance = function (containerId, taskId, callback) {

                    $http({method: 'PUT', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/tasks/" + taskId + "/states/released"}).
                            success(function(data, status, headers, config) {

                                var response = { success: status == 201, message : status, data : data};
                                callback(response);
                            }).
                            error(function(data, status, headers, config) {

                                var response = { success: false, message : status};
                                callback(response);
                            });
                };

                service.CompleteInstance = function (containerId, taskId, data, callback) {

                    $http({method: 'PUT', url: KieServerInfoService.data.kieServer.location+"/containers/" + containerId + "/tasks/" + taskId + "/states/completed",
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