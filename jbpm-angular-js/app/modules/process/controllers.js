'use strict';

angular.module('Processes')

        .controller('ProcessesController',
        ['$scope', '$rootScope', '$location', 'ProcessService',
            function ($scope, $rootScope, $location, ProcessService) {
                $scope.dataLoading = true;

                ProcessService.GetDefinitions(function (response) {

                    if (response.success) {
                        $scope.processes = response.data;
                        $location.path('/processes');
                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }])

        .controller('ProcessController',
        ['$scope', '$routeParams', 'ProcessService',
            function ($scope, $routeParams, ProcessService) {
                $scope.dataLoading = true;

                ProcessService.GetDefinition($routeParams.containerId, $routeParams.processId, function (response) {

                    if (response.success) {
                        $scope.process = response.data;
                    } else {
                        $scope.error = response.message;
                        $scope.dataLoading = false;
                    }
                });
            }]);