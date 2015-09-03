'use strict';

// declare modules
angular.module('Authentication', []);
angular.module('Home', []);
angular.module('Containers', []);
angular.module('Processes', []);
angular.module('ProcessInstances', []);
angular.module('TaskInstances', []);

angular.module('BasicHttpAuthExample', [
    'ngRoute',
    'Authentication',
    'Home',
    'Containers',
    'Processes',
    'ProcessInstances',
    'TaskInstances',
    'ngCookies'
])

 .filter("asDate", function () {
        return function (input) {
            return new Date(input);
        }
    })

.config(['$routeProvider', function ($routeProvider) {

    $routeProvider
        .when('/login', {
            controller: 'LoginController',
            templateUrl: 'modules/authentication/views/login.html'
        })

        .when('/', {
            controller: 'HomeController',
            templateUrl: 'modules/home/views/home.html'
        })

        .when('/containers', {
            controller: 'ContainersController',
            templateUrl: 'modules/containers/views/containers.html'
        })

        .when('/processes', {
            controller: 'ProcessesController',
            templateUrl: 'modules/process/views/processlist.html'
        })
        .when('/processes/:containerId/:processId', {
            templateUrl: 'modules/process/views/process.html',
            controller: 'ProcessController'
        })
        .when('/processinstances/:containerId/instance/:processId', {
            templateUrl: 'modules/processinst/views/newprocessinstance.html',
            controller: 'NewProcessInstanceController'
        })
        .when('/processinstances', {
            controller: 'ProcessInstancesController',
            templateUrl: 'modules/processinst/views/processinstlist.html'
        })
        .when('/processinstances/:containerId/:processInstanceId', {
            templateUrl: 'modules/processinst/views/processinstance.html',
            controller: 'ProcessInstanceController'
        })
        .when('/tasks', {
            controller: 'TaskInstancesController',
            templateUrl: 'modules/task/views/tasklist.html'
        })
        .when('/tasks/:containerId/:taskId', {
            controller: 'TaskInstanceController',
            templateUrl: 'modules/task/views/task.html'
        })
        .when('/task/:containerId/:taskId/:processId/:taskName', {
            templateUrl: 'modules/task/views/completetask.html',
            controller: 'CompleteTaskInstanceController'
        })

        .otherwise({ redirectTo: '/login' });
}])

.run(['$rootScope', '$location', '$cookieStore', '$http',
    function ($rootScope, $location, $cookieStore, $http) {
        // keep user logged in after page refresh
        $rootScope.globals = $cookieStore.get('globals') || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            // redirect to login page if not logged in
            if ($location.path() !== '/login' && !$rootScope.globals.currentUser) {
                $location.path('/login');
            }
        });
    }]);
