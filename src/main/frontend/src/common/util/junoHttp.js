'use strict';

angular.module('Common.Util').service("junoHttp", [
        '$http',
        '$window',
        '$q',

        function (
            $http,
            $window,
            $q
        )
        {
            // This service wraps the methods in $http so that authentication can be added.
            var http_util = {};

            http_util.request =  function request(request_hash)
            {
                var deferred = $q.defer();

                $http(request_hash).then(
                    function success(response)
                    {
                        http_util.success_function(response, request_hash, deferred);
                    }, function error(errors)
                    {
                        http_util.errors_function(errors, request_hash, deferred);
                    }, function notify(progress)
                    {
                        deferred.notify(progress);
                    });

                return deferred.promise;
            };

            http_util.success_function = function success_function(response, request_hash, deferred)
            {
                var request_result = response;
                try {
                    if (request_result.data.status === 'SUCCESS') {
                        var results = {
                            meta: angular.copy(request_result.data.headers),
                            data: angular.copy(request_result.data.body)
                        };
                        deferred.resolve(results);
                    }
                    else if (request_result.data.status === 'ERROR'){
                        var errors = {
                            meta: angular.copy(request_result.data.headers),
                            data: angular.copy(request_result.data.error.message)
                        };
                        deferred.reject(errors);
                    }
                    else {
                        throw "Invalid Response Status";
                    }
                }
                catch(e) {
                    var errors = {
                        meta: {},
                        data: "An Error has occurred. please contact technical support"
                    };
                    deferred.reject(errors);
                }
            };

            http_util.errors_function = function errors_function(errors, request_hash, deferred)
            {
                deferred.reject(errors);
            };

            http_util.call = function call(method, url, data)
            {
                var request_hash = {
                    method: method,
                    url: url,
                    data: data
                };
                return http_util.request(request_hash);
            };

            http_util.get = function get(url, config)
            {
                var request_hash = {
                    method: 'GET',
                    url: url
                };
                Juno.Common.Util.mergeHash(request_hash, config);
                return http_util.request(request_hash);
            };

            http_util.post = function post(url,data,config={})
            {
                var request_hash = {
                    method: 'POST',
                    url: url,
                    data: data
                };
                Juno.Common.Util.mergeHash(request_hash, config);
                return http_util.request(request_hash);
            };

            http_util.put = function put(url,data,config={})
            {
                var request_hash = {
                    method: 'PUT',
                    url: url,
                    data: data
                };
                Juno.Common.Util.mergeHash(request_hash, config);
                return http_util.request(request_hash);
            };

            // 'delete' is a reserved keyword -- leave me alone
            http_util.del = function del(url,data)
            {
                var request_hash = {
                    method: 'DELETE',
                    url: url,
                    data: data
                };
                return http_util.request(request_hash);
            };

            return http_util;
        }
    ]
);
