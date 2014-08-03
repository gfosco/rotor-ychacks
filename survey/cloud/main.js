
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.afterSave("SurveyAnswer", function(request) {
	Parse.Cloud.httpRequest({
		url: "http://rtrp.io/client/RotorSurvey?rating=" + request.object.get("rating")
	}).then(function() {
		console.log("successfully logged: " + request.object.get("rating"));
	}, function() {
		console.log("failed to log: " + request.object.get("rating"));
	});
});
