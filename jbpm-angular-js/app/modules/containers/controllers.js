'use strict';

angular.module('Containers')

.controller('ContainersController',
    ['$scope', '$rootScope', '$location', 'ContainerService',
    function ($scope, $rootScope, $location, ContainerService) {
            $scope.dataLoading = true;

            ContainerService.GetContainers(function (response) {

                if (response.success) {
                    $scope.containers = response.data;
                    $location.path('/containers');
                } else {
                    $scope.error = response.message;
                    $scope.dataLoading = false;
                }
            });
    }]);