(function(){
     
    var configService = function($http) {
        this.model = undefined;
        
        this.getConfig = function() {
            return this.model;
        }
        
        this.getConfigFromServer = function(url) {
            return $http.get(url);
        }
        
        this.clearModel = function (obj) {
            for (attrib in obj) {
                obj[attrib] = '';
            }
        }
        
        this.setConfig = function(val) {
            this.model = val;
        }
    }
    
    angular.module("perftool").service('configService', configService);
    configService.$inject = ['$http'];
})();