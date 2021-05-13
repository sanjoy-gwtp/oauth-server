/**
 * Created by sanjoy on 5/19/19.
 */



(function (window, document) {

    var NgSso = function(config) {
        if(!(this instanceof NgSso)) {
            return new NgSso(config);
        }

        var ngSso = this;
        var initConfig = {
            flow : 'standard',
            responseMode : 'query'
        };
        var sessionCheckConf = {};

        ngSso.authenticated = false;
        ngSso.lastActivityTime = Date.now();

        var SessionStatus = {
            UNCHANGED : 'UNCHANGED',
            CHANGED : 'CHANGED',
            ERROR: 'ERROR'
        };

        ngSso.init = function(_initConfig) {

            if(_initConfig) {
                if(_initConfig.flow) initConfig.flow = _initConfig.flow;
                if(_initConfig.responseMode) initConfig.responseMode = _initConfig.responseMode;
            }

            var promise = createPromise();

            // function onActivity() {
            //     var now = Date.now();
            //     var diff = now - ngSso.lastActivityTime;
            //     if(diff >= 1000) {
            //         console.debug("User heartbeat present.")
            //         if(ngSso.authenticated) {
            //             ngSso.lastActivityTime = now;
            //             if(!isValidToken()) {
            //                 ngSso.renewToken().success(function(r){}).error(function(r){});
            //                 console.debug("Sent token renew request.");
            //             }
            //         }
            //     }
            // }

            function onAuthSuccess (jwt, publish = true) {
                var msg = {
                    clientId: config.clientId,
                    clientSecret: config.clientSecret,
                    type:'TOKEN',
                    token: jwt
                };
                ngSso.token = jwt;

                if(ngSso.authenticated == false) {
                    ngSso.authenticated = true;
                    promise.setSuccess({authenticated : true, token : jwt.access_token});
                }

                if(publish == true ) {
                    sessionCheckConf.iframe.contentWindow.postMessage(msg,sessionCheckConf.origin);
                }
            }

            function processAuthentication() {
                var authCallback = parseCallback(window.location.href)

                if(authCallback) {
                    window.history.replaceState({},null,authCallback.origin);
                    processAuthCallback(authCallback).success(onAuthSuccess);

                } else {
                    processPreAuthenticatedAuthentication();
                }
            }

            function processPreAuthenticatedAuthentication() {

                console.debug('ssoctrl: loaded');

                var msg = {
                    clientId: config.clientId,
                    clientSecret: config.clientSecret,
                    type:'INIT'
                };

                window.addEventListener('message', function(event) {

                    sessionCheckConf.iframeReady = true;

                    if(event.data.status=='TOKEN'){

                        ngSso.token = event.data.token;

                        if(Date.now() < (decodeToken(ngSso.token.refresh_token).exp * 1000)) {
                            if(Date.now() < (decodeToken(ngSso.token.access_token).exp * 1000)) {
                                ngSso.checkToken(ngSso.token)
                                    .success(function(result) {
                                        onAuthSuccess(ngSso.token, false);
                                    }).error(function(result){
                                    ngSso.renewToken()
                                        .success(function(result) {
                                            onAuthSuccess(ngSso.token, false);
                                        })
                                        .error(function(result) {
                                            ngSso.login();
                                        });
                                });
                            } else {
                                ngSso.renewToken()
                                    .success(function(result) {
                                        onAuthSuccess(ngSso.token, false);
                                    })
                                    .error(function(result) {
                                        ngSso.login();
                                    });
                            }
                        } else {
                            ngSso.login();
                        }

                    } else if(event.data.status=='NO_TOKEN'){
                        ngSso.login();
                    }
                }, false);

                sessionCheckConf.iframeReady = false;

                sessionCheckConf.iframe.contentWindow.postMessage(msg,sessionCheckConf.origin);

                var readyStateChecker = setInterval(function(){
                    if(sessionCheckConf.iframeReady == true) {
                        console.debug('ssoctrl: ready');
                        clearInterval(readyStateChecker);
                    } else {
                        console.debug('ssoctrl: not ready');
                        sessionCheckConf.iframe.contentWindow.postMessage(msg,sessionCheckConf.origin);
                    }
                },1000);
            }

            function initAuthentication() {
                initSessionIFrame().success(function(){
                    processAuthentication();
                });
            }

            setTimeout(initAuthentication,500);

            return promise.promise;
        }

        ngSso.login = function (prompt) {
            if(prompt) {

            } else {
                var params = [
                    'response_type=code',
                    'response_mode=' + initConfig.responseMode,
                    'client_id=' + config.clientId,
                    'redirect_uri=' + encodeURIComponent(window.location.href),
                    'state=' + UUID()
                ];

                var url = config.authUrl + '/oauth/authorize?';

                params.forEach(function (value, index) {
                    if(index > 0) {
                        url += '&';
                    }
                    url += value;
                });

                window.location.href = url;
            }
        }

        ngSso.logout = function () {
            var accessToken = decodeToken(ngSso.token.access_token);
            var url = config.authUrl + "/logout?redirect_uri=" + encodeURIComponent(window.location.href) + "&" + encodeURIComponent("_us") + "=" + encodeURIComponent( accessToken.user_name +  ":" + ngSso.token.jti + ":" + config.clientId);
            window.location.href = url;
        }

        ngSso.changePassword = function () {
            var url = config.authUrl + "/password?redirect_uri=" + encodeURIComponent(window.location.href);
            window.location.href = url;
        }



        ngSso.switchBranch = function () {
            var url = config.authUrl + "/branch?redirect_uri=" + encodeURIComponent(window.location.href);
            window.location.href = url;
        }

        function initSessionIFrame() {
            var targetSrc = config.authUrl + '/resource/sso/ssoctrl.html';
            var iframe = document.getElementById("ng-sso-iframe");
            if(iframe != null) {
                return;
            }

            iframe = document.createElement('iframe');

            sessionCheckConf.iframe = iframe;
            sessionCheckConf.promise = createPromise();

            iframe.setAttribute('src',targetSrc);
            iframe.setAttribute('title','ng-session-iframe');
            iframe.style.display = 'none';

            iframe.onload = function (ev) {
                sessionCheckConf.origin = targetSrc.substring(0, targetSrc.indexOf('/', 8));
                sessionCheckConf.promise.setSuccess();
            }

            document.body.appendChild(iframe);

            window.addEventListener('message', messageCallback, false);

            return sessionCheckConf.promise.promise;
        }

        ngSso.account = function() {
            //TODO change after implementing loadAccount();

            let promise = createPromise();

            if(ngSso.authenticated && ngSso.token) {
                let token = decodeToken(ngSso.token.access_token);

                promise.setSuccess({
                    username : token.user_name,
                    homeBranch: token.homeBranch,
                    activeBranch: token.activeBranch
                });
            }

            return promise.promise;
        }

        ngSso.accessToken = function() {
            var promise = createPromise();

            setTimeout(function() {
                if(isValidToken()) {
                    promise.setSuccess(ngSso.token.access_token);
                } else {
                    ngSso.renewToken()
                        .success(function(r){
                            promise.setSuccess(ngSso.token.access_token);
                        })
                        .error(function(r){
                            promise.setError(r);
                        });
                }
            },500);

            return promise.promise;
        }

        ngSso.checkToken = function(token) {
            var promise = createPromise();

            var req = new XMLHttpRequest();
            var url = config.authUrl + '/oauth/check_token';
            var params = 'token=' + token.access_token;

            req.open('POST', url, true);
            req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            req.setRequestHeader('Authorization', 'Basic ' + btoa(config.clientId + ':' + config.clientSecret));
            // req.withCredentials = true;

            req.onreadystatechange = function() {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        promise.setSuccess({valid: true});
                    } else {
                        promise.setError({error:'invalid_access_token',error_description:'Access Token Not Valid'});
                    }
                }
            };

            req.send(params);

            return promise.promise;
        }

        ngSso.renewToken = function() {
            var promise = createPromise();

            var req = new XMLHttpRequest();
            var url = config.authUrl + '/oauth/token';
            var params = 'grant_type=refresh_token';
            params += '&refresh_token=' + ngSso.token.refresh_token;

            req.open('POST', url, true);
            req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            req.setRequestHeader('Authorization', 'Basic ' + btoa(config.clientId + ':' + config.clientSecret));
            // req.withCredentials = true;

            req.onreadystatechange = function() {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        ngSso.token = JSON.parse(req.responseText);

                        var msg = {
                            clientId: config.clientId,
                            clientSecret: config.clientSecret,
                            type:'TOKEN',
                            token: ngSso.token
                        };

                        sessionCheckConf.iframe.contentWindow.postMessage(msg,sessionCheckConf.origin);

                        promise.setSuccess(ngSso.token);
                    } else {
                        promise.setError({error:'',error_description:'', status: req.status});
                    }
                }
            };

            req.send(params);

            return promise.promise;
        }

        function isValidToken() {
            if(ngSso.token.access_token) {
                const payload = ngSso.token.access_token.split('.')[1];
                const payloadObj = JSON.parse(atob(payload));
                if(((payloadObj.exp * 1000) + (1000 * 10)) > Date.now()) return true;
            }
            return false;
        }

        function loadAccount() {
            //TODO need implementation ...
        }

        function messageCallback(event) {
            console.log(event.data);
            if(event.data.status=='CHANGED'){
                console.log(event.data.status);
                ngSso.token= event.data.token
            } else if(event.data.status=='DESTROYED'){
                console.log(event.data.status);
                ngSso.token= null;
            } else{
                console.log(event.data.status);
            }
        }

        function processAuthCallback(authCallback) {

            var promise = createPromise();

            var req = new XMLHttpRequest();
            var url = config.authUrl + '/oauth/token';
            var params = 'grant_type=authorization_code';
            params += '&code=' + authCallback.code;
            params += '&redirect_uri=' + encodeURIComponent(window.location.href);

            req.open('POST', url, true);
            req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            req.setRequestHeader('Authorization', 'Basic ' + btoa(config.clientId + ':' + config.clientSecret));
            // req.withCredentials = true;

            req.onreadystatechange = function() {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        var jwt = JSON.parse(req.responseText);
                        promise.setSuccess(jwt);
                    } else {
                        promise.setError(req.responseText);
                    }
                }
            };

            req.send(params);

            return promise.promise;
        }

        function parseCallback(url) {
            var oauth = parseCallbackUrl(url);
            if (!oauth) {
                return;
            }
            return oauth.authParams;
        }

        function parseCallbackUrl(url) {
            var paramNames = ['code','state','error','error_description'];

            var queryIndex = url.indexOf('?');
            var fragmentIndex = url.indexOf('#');

            if(initConfig.responseMode === 'query' && queryIndex !== -1) {
                var params = parseParams(url.substring(queryIndex + 1, fragmentIndex !== -1 ? fragmentIndex : url.length), paramNames);
                params.authParams.origin = url.substring(0,queryIndex);

                if(params.queryParams !== '') {
                    params.authParams.origin += '?' + params.queryParams;
                }

                if(fragmentIndex !== -1) {
                    params.authParams.origin += url.substring(fragmentIndex);
                }
            } else if(initConfig.responseMode === 'fragment' && fragmentIndex !== -1) {
                var params = parseParams(url.substring(fragmentIndex + 1), paramNames);
                params.authParams.origin = url.substring(0,fragmentIndex);

                if(params.queryParams !== '') {
                    params.authParams.origin += '#' + params.queryParams;
                }
            }

            if(params !== undefined && params.authParams !== undefined){
                if(params.authParams.code || params.authParams.error || params.error_description) {
                    return params;
                }
            }

        }

        function parseParams(queryString, paramNames) {
            var keyValues = queryString.split('&');

            var authParams = {};
            var queryParams = '';

            for (var i = 0; i < keyValues.length; i++) {
                var keyValue = keyValues[i].split('=');
                if (paramNames.indexOf(keyValue[0]) !== -1) {
                    authParams[keyValue[0]] = keyValue[1];
                } else {
                    if(queryParams !== '') {
                        queryParams += '&';
                    }

                    queryParams += keyValues[i];
                }
            }

            return {authParams: authParams,queryParams: queryParams};
        }

        function checkSession() {
            sessionCheckConf.iframe.contentWindow.postMessage('message-test',sessionCheckConf.origin);
        }

        function createPromise() {
            if (typeof Promise === "function") {
                return createNativePromise();
            } else {
                return createLegacyPromise();
            }
        }

        function createNativePromise() {
            var p = {
                setSuccess: function(result) {
                    p.success = true;
                    p.resolve(result);
                },

                setError: function(result) {
                    p.success = false;
                    p.reject(result);
                }
            };
            p.promise = new Promise(function(resolve, reject) {
                p.resolve = resolve;
                p.reject = reject;
            });
            p.promise.success = function(callback) {
                p.promise.then(callback);
                return p.promise;
            }
            p.promise.error = function(callback) {
                p.promise.catch(callback);
                return p.promise;
            }
            return p;
        }

        function createLegacyPromise() {
            var p = {
                setSuccess: function(result) {
                    p.success = true;
                    p.result = result;
                    if (p.successCallback) {
                        p.successCallback(result);
                    }
                },

                setError: function(result) {
                    p.error = true;
                    p.result = result;
                    if (p.errorCallback) {
                        p.errorCallback(result);
                    }
                },

                promise: {
                    success: function(callback) {
                        if (p.success) {
                            callback(p.result);
                        } else if (!p.error) {
                            p.successCallback = callback;
                        }
                        return p.promise;
                    },
                    error: function(callback) {
                        if (p.error) {
                            callback(p.result);
                        } else if (!p.success) {
                            p.errorCallback = callback;
                        }
                        return p.promise;
                    }
                }
            }
            return p;
        }

        function decodeToken (token) {
            var base64Url = token.split('.')[1];
            var base64 = base64Url.replace('-', '+').replace('_', '/');
            return JSON.parse(window.atob(base64));
        }

        function UUID() {
            var s = [];
            var hexDigits = '0123456789abcdef';
            for (var i = 0; i < 36; i++) {
                s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
            }
            s[14] = '4';
            s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
            s[8] = s[13] = s[18] = s[23] = '-';
            var uuid = s.join('');
            return uuid;
        }
    }

    if ( typeof module === "object" && module && typeof module.exports === "object" ) {
        module.exports = NgSso;
    } else {
        window.NgSso = NgSso;

        if ( typeof define === "function" && define.amd ) {
            define( "NgSso", [], function () { return NgSso; } );
        }
    }

})(window,document);