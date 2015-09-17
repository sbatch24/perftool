(function(){
    
    
    var configService = function($http) {
        this.getConfig = function(url) {
            return $http.get(url);
        },
        
        this.clearModel = function (obj) {
            for (attrib in obj) {
                obj[attrib] = '';
            }
        }
    }
    
    angular.module("perftool").service('configService', configService);
    configService.$inject = ['$http'];
    
})();