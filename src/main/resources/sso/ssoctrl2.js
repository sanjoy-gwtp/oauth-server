$(function(){

    var $scope = this;

    $scope.TOKEN_REQUEST_DONE = false;

    function onLocalStoreEvent(event) {
        if(event.key == 'requestSession') {
            console.debug('Session request received',new Date());
            sendSession();
        } else if(event.key == 'destroySession') {
            destroySession();
        } else if (event.key == 'session') {
            if(event.newValue) {
                onSession(JSON.parse(event.newValue));
            }
        }
    }

    function onSession(sessionData) {

        var token = getToken();

        if(token != null) {
            if(token.jti == parseToken(sessionData['token']).jti) return;
        }

        console.debug('Received New Session',new Date());

        for (key in sessionData) {
            sessionStorage.setItem(key, sessionData[key]);
        }

        if($scope.TOKEN_REQUEST_DONE == true) {
            notifyApp({type : 'CHANGED'});
        }
    }

    function onToken(token) {
        console.debug('New Token Received',new Date());
        sessionStorage.setItem("token", JSON.stringify(token));
        sendSession();
        $scope.TOKEN_REQUEST_DONE = true;
    }

    function onInit() {

        console.debug('On Init Called',new Date());

        if (!sessionStorage.length) {
            requestSession();
        
            var count = 0;
            var timer = setInterval(function() {
                var token = getToken();
                if(token) {
                    clearInterval(timer);
                    notifyApp({type: 'TOKEN'});
                    $scope.TOKEN_REQUEST_DONE = true;
                } else if(count > 5) {
                    clearInterval(timer);
                    notifyApp({type: 'NO_TOKEN'});
                    $scope.TOKEN_REQUEST_DONE = true;
                }
                count++;
            }, 500);
        } else {
            notifyApp({type: 'TOKEN'});
            $scope.TOKEN_REQUEST_DONE = true;
        }
    }

    function onLogout() {
        destroySession(false);

        //notify other apps
        requestDestroySession();
    }

    function onMessageEvent(event) {
        $scope.$$origin = event.origin;
        $scope.$$source = event.source;

        if(event.data.clientId) {
            $scope.clientId = event.data.clientId;
        }

        if(event.data.clientSecret) {
            $scope.clientSecret = event.data.clientSecret;
        }

        if(event.data.type) {
            switch(event.data.type) {
                case 'TOKEN' : 
                    onToken(event.data.token); 
                    break;

                case 'INIT' : 
                    onInit();
                    break;

                case 'LOGOUT' : 
                    onLogout();
                    break; 
            }
        }
    }

    function notifyApp(event) {
        $scope.$$source.postMessage({ status: event.type, token :  getToken()}, $scope.$$origin);
    }

    function getTokenPayload(token) {
        var payloadString = token.split('.')[1];
        return JSON.parse(atob(payloadString));
    }

    function getToken() {
        var token = sessionStorage.getItem("token")
        if (token) {
            return parseToken(token);
        } else {
            return token;
        }
    }

    function parseToken(tokenSrc) {
        return JSON.parse(tokenSrc);
    }

    function sendSession() {
        if(getToken() != null) {
            console.debug('Sending Session',new Date());
            localStorage.setItem('session', JSON.stringify(sessionStorage));
            localStorage.removeItem('session');
        }
    }

    function destroySession(notify) {
        console.debug('Destroy Session')
        sessionStorage.clear();

        if(notify == true) {
            notifyApp({type: 'DESTROYED'})
        }
    }

    function registerListeners() {
        window.addEventListener('message', onMessageEvent , false);
        window.addEventListener('storage', onLocalStoreEvent, false);
    }

    function requestSession() {
        console.debug('Request Session Called',new Date());
        localStorage.setItem('requestSession', Date.now());
        localStorage.removeItem('requestSession');
    }

    function requestDestroySession() {
        console.debug('Request Destroy Session Called',new Date());
        localStorage.setItem('destroySession', Date.now());
        localStorage.removeItem('destroySession');
    }

    registerListeners();
});
