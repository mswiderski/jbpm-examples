'use strict';

angular.module('ProcessInstances')

        .controller('ProcessInstancesController',
        ['$scope', '$rootScope', '$location', 'ProcessInstanceService',
            function ($scope, $rootScope, $location, ProcessInstanceService) {
                $scope.dataLoading = true;
                $scope.convertStatus = ProcessInstanceService.convertStatus;

                ProcessInstanceService.GetInstances(function (response) {

                    if (response.success) {
                        $scope.processinstances = response.data;
                        $scope.convertStatus = ProcessInstanceService.convertStatus;
                        $location.path('/processinstances');
                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }])

        .controller('ProcessInstanceController',
        ['$scope', '$routeParams', 'ProcessInstanceService',
            function ($scope, $routeParams, ProcessInstanceService) {
                $scope.dataLoading = true;
                $scope.convertStatus = ProcessInstanceService.convertStatus;

                ProcessInstanceService.GetInstance($routeParams.containerId, $routeParams.processInstanceId, function (response) {

                    if (response.success) {
                        $scope.processInstance = response.data;
                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }])

        .controller('AbortProcessInstanceController',
        ['$scope', '$routeParams', '$location', 'ProcessInstanceService',
            function ($scope, $routeParams, $location, ProcessInstanceService) {
                $scope.dataLoading = true;
                $scope.abortProcessInstance = function(containerId, processInstanceId) {

                    ProcessInstanceService.AbortInstance(containerId, processInstanceId, function (response) {

                        if (response.success) {
                            $location.path('/processinstances');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });
                }
            }])
        .controller('NewProcessInstanceController',
        ['$scope', '$routeParams', '$location', '$parse', 'ProcessService',
            function ($scope, $routeParams, $location, $parse, ProcessService) {
                $scope.dataLoading = true;
                $scope.formData = {};


                $scope.submit = function() {

                    var data = '{';
                    angular.forEach($scope.formData.variables, function (variable) {
                        if (variable.value != null && variable.value != '') {
                            data += '"' + variable.name + '":' + variable.value + ',';
                        }
                    });
                    if (data.length > 1) {
                        data = data.substring(0, data.length - 1);
                    }
                    data+= '}';

                    ProcessService.NewInstance($scope.process['container-id'], $scope.process['process-id'], data, function (response) {

                        if (response.success) {
                            $location.path('/processinstances');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });
                }

                ProcessService.GetDefinition($routeParams.containerId, $routeParams.processId, function (response) {

                    if (response.success) {
                        $scope.process = response.data;
                        var procVars = [];
                        var variables = $scope.process['process-variables'];

                        angular.forEach(variables, function (value, key) {
                            var item = {
                                'name' : key,
                                'value' : ''
                            }
                            procVars.push(item)

                        });
                        $scope.formData.variables = procVars;
                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }])