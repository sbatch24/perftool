(function(){
     
    var configService = function($http) {
        
        this.model = undefined;
        this.config = undefined;
        
        this.getConfig = function() {
            return this.config;
        }
        
        this.getModel = function() {
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

        this.setModel = function(val) {
            this.model = val;
        }
        
        this.setConfig = function(val) {
            this.config = val;
        }
        
        this.updateConfig = function(url,val) {
            return $http.post(url, val);
        }
    }
    
    angular.module("perftool").service('configService', configService);
    configService.$inject = ['$http'];
})();