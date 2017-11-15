<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>AJAX with Servlets using AngularJS</title>
<script type="text/javascript" src="js/angular.min.js"></script>
<script>
	var app = angular.module('myApp', []);

	
        /*
         function MyController($scope, $http) {

		$scope.getDataFromServer = function() {
			$http({
				method : 'GET',
				url : 'resources/tools/getsaludo'
			}).then(function(response) {
				$scope.respuesta = response.data;
			},function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
			});

		};
	};
        
        
        app.controller('MyController', MyController);
         */
        /*
         app.controller('MyController', function ($scope, $http) 
        {
           $scope.getDataFromServer = function() 
                {
			$http({
				method : 'GET',
				url : 'resources/tools/getsaludo'
			}).then(function(response) {
				$scope.respuesta = response.data;
			},function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
			});

		};
        }); 
         
         */
        app.controller('MyController', function ($scope, $http) 
        {
           $scope.getDataFromServer = function() 
                {
 			var onSuccessGet = function (response) {
                            $scope.respuesta = response.data;
                        };
                       $http.get("resources/tools/getsaludo").then(onSuccessGet);
		};
           $scope.sendDataToServer = function() 
                {
 			var onSuccessPost = function (response) {
                            $scope.respuesta = response.data;
                        };
                       $http.post("resources/tools/setnombre",{ miNombre: $scope.minombre }).then(onSuccessPost);
		};     
                
            $scope.resquestHoraToServer = function() 
                {
 			var onSuccessPost = function (response) {
                            $scope.respuesta = response.data;
                        };
                       $http.post("resources/tools/gethora",{ miNombre: $scope.minombre }).then(onSuccessPost);
		};   
        
        });
</script>
</head>
<body>
	<div ng-app="myApp">
		<div ng-controller="MyController">
			<button ng-click="getDataFromServer()">Get Saludo</button>
                        <p>JSON COMPLETO {{respuesta}}</p>
			<p>SALUDO {{respuesta.saludo.texto}}</p>
                        <input type="text" id="firstName" ng-model="minombre" /> <br />
                        <button ng-click="sendDataToServer()">Send Nombre</button>
                        
                        <button ng-click="requestHoraToServer()">Get Hora</button>

		</div>
	</div>
</body>
</html>