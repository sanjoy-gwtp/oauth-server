$(function () {
    angular.module('ssoSession', ['ngCookies'])

        .run(["$window", "$rootScope", function ($window, $rootScope) {
            $window.addEventListener('message', function (event) {
                $rootScope.$broadcast('message', event);
            }, false);
        }])

        .controller('SsoSessionController', ['$scope','$http', '$cookies', '$window', '$rootScope',
            function ($scope, $http, $cookies, $window, $rootScope) {

                $scope.init = false;
                $scope.watcher = {enabled: false};

                var SessionStatus = {
                    UNCHANGED: 'UNCHANGED',
                    CHANGED: 'CHANGED',
                    EXISTST: 'EXISTST',
                    TOKEN_ACTIVE: 'TOKEN_ACTIVE',
                    NO_TOKEN: 'NO_TOKEN',
                    NO_ACCESS_TOKEN: 'NO_ACCESS_TOKEN',
                    ERROR: 'ERROR',
                    DESTROYED: 'DESTROYED',
                    LOGOUT: 'LOGOUT'
                };

                $rootScope.$on('message', function (a, event) {
                    console.log({ e: event });
                    var msg = event.data;

                    $scope.origin = event.origin;
                    $scope.source = event.source;

                    if (msg) {
                        if (msg.type === "TOKEN") {
                            $scope.clientId = msg.clientId;
                            $scope.clientSecret = msg.clientSecret;
                            $scope.token = msg.token;
                            $scope.access_token = $scope.getTokenPayload(msg.token.access_token);
                            $scope.lastActivityTime = Date.now();

                            var tokenObject = $scope.getStoredToken();

                            if (tokenObject) {
                                if (tokenObject.jti != msg.token.jti)
                                    $scope.setStoredToken(msg.token);
                            } else {
                                $scope.setStoredToken(msg.token);
                            }

                            $scope.initTokenWatcher();
                        } else if (msg.type === "CHECK_TOKEN") {
                            $scope.clientId = msg.clientId;
                            $scope.clientSecret = msg.clientSecret;
                            $scope.checkToken(msg);
                        }
                    }
                });

                $scope.startTimer = function () {
                    $scope.onTokenChange();
                }

                $scope.getStoredToken = function () {
                    var token = localStorage.getItem("token")
                    if (token) {
                        return JSON.parse(token);
                    } else {
                        return token;
                    }

                }

                $scope.setStoredToken = function (token) {
                    return localStorage.setItem("token", JSON.stringify(token));
                }

                $scope.removeStoredToken = function() {
                    localStorage.removeItem("token");
                }

                $scope.checkToken = function (msg) {
                    console.log("Check Token Request:",msg);
                    var token = $scope.getStoredToken();
                    if(token) {

                        var refreshToken = $scope.getTokenPayload(token.refresh_token);

                        var REFRESH_TOKEN_VALID = (refreshToken.exp * 1000) > Date.now();

                        $http({
                            method: 'POST',
                             url: "/oauth/check_token",
//                            url: "http://192.168.1.140:30082/oauth/check_token",
                            headers: {
                                'Authorization': 'Basic ' + btoa($scope.clientId + ':' + $scope.clientSecret),
                                'Content-Type': 'application/x-www-form-urlencoded'},
                            data: 'token='+token.access_token
                        }).then(function (response) {

                            $scope.token = token;
                            $scope.access_token = $scope.getTokenPayload(token.access_token);
                            $scope.lastActivityTime = Date.now();

                            $scope.source.postMessage(
                                {
                                    status: SessionStatus.TOKEN_ACTIVE,
                                    token: token
                                }, $scope.origin);

                            $scope.initTokenWatcher();
                        }, function(response) {
                            console.log("error-from-auth:",response);
                            $scope.removeStoredToken();
                            $scope.source.postMessage(
                                {
                                    status: (REFRESH_TOKEN_VALID == true) ? SessionStatus.NO_ACCESS_TOKEN : SessionStatus.NO_TOKEN,
                                    token: token
                                }, $scope.origin);
                        });

                    } else {
                        $scope.source.postMessage(
                            {
                                status: SessionStatus.NO_TOKEN
                            }, $scope.origin);
                    }
                }

                // $scope.onTokenExp = function() {
                //     var expiresIn = ($scope.access_token.exp * 1000) - ((1000 * 60) * 1);

                //     var startTime = ($scope.access_token.exp  - $scope.token.expires_in) * 1000;

                //     var maxIdleTime = (($scope.access_token.exp * 1000) - startTime) - ((1000 * 60) * 2);

                //     var idleTime = Date.now() - $scope.lastActivityTime;
                //     var remainingTokenTime = ($scope.access_token.exp * 1000) - Date.now();

                //     if(remainingTokenTime <= (100 * 1000)) {

                //     }


                //     //if()
                // }

                $scope.onTokenChange = function () {
                    var storedToken = $scope.getStoredToken();
                    if (storedToken) {
                        if ($scope.token.jti != storedToken.jti) {
                            $scope.token = storedToken;
                            $scope.access_token = $scope.getTokenPayload(storedToken.access_token);
                            $scope.source.postMessage(
                                {
                                    status: SessionStatus.CHANGED,
                                    token: storedToken
                                }, $scope.origin);
                        }
                    } else {
                        if($scope.token) {
                            delete $scope.token;
                            $scope.source.postMessage({ status: SessionStatus.DESTROYED }, $scope.origin);
                        }
                    }
                }

                $scope.initTokenWatcher = function() {
                    if(!$scope.watcher.enabled) {
                        $scope.watcher.enabled = true;
                        setInterval(function () {
                            $scope.startTimer()
                        }, 500);
                    }
                }

                $scope.getTokenPayload = function(access_token) {
                    var payloadString = access_token.split('.')[1];
                    return JSON.parse(atob(payloadString));
                }

                // console.log('ssoctrl.html',$window.sessionStorage.);

            }]);

    angular.bootstrap(document, ['ssoSession']);
});