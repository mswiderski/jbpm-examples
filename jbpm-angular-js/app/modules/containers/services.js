'use strict';

angular.module('Containers')

.factory('ContainerService',
    ['KieServerInfoService', '$http', '$rootScope',
    function (KieServerInfoService, $http, $rootScope) {
        var service = {};

        service.GetContainers = function (callback) {

            $http({method: 'GET', url: KieServerInfoService.data.kieServer.location+"/containers"}).
                    success(function(data, status, headers, config) {

                        var response = { success: status == 200, message : status, data : data.result['kie-containers']['kie-container']};
                        callback(response);
                    }).
                    error(function(data, status, headers, config) {

                        var response = { success: false, message : status};
                        callback(response);
                    });
        };


        return service;
    }]);