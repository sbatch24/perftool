(function(){
    
    
    var configService = function($http) {
        var config = {};
            
        this.getConfig = function() {
            return this.config;
        }
        
        this.getConfigFromServer = function(url) {
            return $http.get(url);
        }
        
        this.clearModel = function (obj) {
            for (attrib in obj) {
                obj[attrib] = '';
            }
        }
        
        getConfigFromServer("/config").then(function(response){            
            this.config = response.data.config;
        },function(response){
            
        });
    }
    
    angular.module("perftool").service('configService', configService);
    configService.$inject = ['$http'];
    
})();