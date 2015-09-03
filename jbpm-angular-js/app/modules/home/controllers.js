'use strict';

angular.module('Home')

.controller('HomeController',
    ['$scope', '$rootScope',
    function ($scope, $rootScope) {
        $scope.user = $rootScope.globals.currentUser.username;
        $scope.serverInfo = $rootScope.kieServer;
    }])

.controller('HeaderController',
        ['$scope', '$location',
            function ($scope, $location)  {
            $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        }
    }]);