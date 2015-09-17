'use strict';

angular.module('TaskInstances')

        .controller('TaskInstancesController',
        ['$scope', '$rootScope', '$location', 'TaskInstanceService',
            function ($scope, $rootScope, $location, TaskInstanceService) {
                $scope.dataLoading = true;

                TaskInstanceService.GetInstances(function (response) {

                    if (response.success) {
                        $scope.tasks = response.data;
                        $location.path('/tasks');
                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }])
        .controller('TaskInstanceController',
        ['$scope', '$routeParams', '$location', 'TaskInstanceService',
            function ($scope, $routeParams, $location, TaskInstanceService) {
                $scope.dataLoading = true;


                $scope.claimTask = function(containerId, taskId) {

                    TaskInstanceService.ClaimInstance(containerId, taskId, function (response) {

                        if (response.success) {

                            $location.path('/tasks');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });
                }

                $scope.startTask = function(containerId, taskId) {
                    TaskInstanceService.StartInstance(containerId, taskId, function (response) {

                        if (response.success) {

                            $location.path('/tasks');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });

                }

                $scope.releaseTask = function(containerId, taskId) {
                    TaskInstanceService.ReleaseInstance(containerId, taskId, function (response) {

                        if (response.success) {
                            $location.path('/tasks');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });

                }

                $scope.completeTask = function(containerId, taskId) {
                    var data = '{';
                    angular.forEach($scope.formData.variables, function (variable) {
                        if (variable.value != null && variable.value != '') {
                            data += '"' + variable.name + '":' + variable.value + ',';
                        }
                    });
                    data = data.substring(0, data.length - 1);
                    data+= '}';

                    TaskInstanceService.CompleteInstance(containerId, taskId, data, function (response) {

                        if (response.success) {
                            $location.path('/tasks');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });

                }

                $scope.goToCompleteTask  = function ( containerId, processId, taskName, taskId ) {

                    $location.path( '/task/' + containerId + "/" + taskId + "/"+processId+"/"+taskName );

                };

                TaskInstanceService.GetInstance($routeParams.containerId, $routeParams.taskId, function (response) {

                    if (response.success) {
                        $scope.task = response.data;

                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }])

        .controller('CompleteTaskInstanceController',
        ['$scope', '$routeParams', '$location', 'TaskInstanceService',
            function ($scope, $routeParams, $location, TaskInstanceService) {
                $scope.dataLoading = true;
                $scope.formData = {};

                $scope.completeTask = function(containerId, taskId) {
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

                    TaskInstanceService.CompleteInstance(containerId, taskId, data, function (response) {

                        if (response.success) {
                            $location.path('/tasks');
                        } else {
                            $scope.error = response.message;
                            $scope.dataLoading = false;
                        }
                    });

                }
                TaskInstanceService.GetInstance($routeParams.containerId, $routeParams.taskId, function (response) {

                    if (response.success) {
                        $scope.task = response.data;

                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });

                TaskInstanceService.GetTaskDefOutputs($routeParams.containerId, $routeParams.processId, $routeParams.taskName, function (response) {

                    if (response.success) {
                        $scope.task = response.data;

                        var taskOutputs = [];
                        var variables = $scope.task['outputs'];

                        angular.forEach(variables, function (value, key) {
                            var item = {
                                'name' : key,
                                'value' : ''
                            }
                            taskOutputs.push(item)

                        });
                        $scope.formData.variables = taskOutputs;

                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }]);