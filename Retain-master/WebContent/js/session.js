$(window).load(function() {
	$('#loginDialog .ok').click(function() {
		login();
	});
	
	
	
	$('#loginPassword').keyup(function(e) {
		if (e.which == 13) {
			login();
		}
	});

	$('#logout').click(function(e) {
		e.preventDefault();
		logout();
	});
});

function login() {
	var retainid = $('#loginRetainID').val();
	var retainpassword = $('#loginRetainPassword').val();
	var intranetid = $('#loginIntranetID').val();
	var intranetpassword = $('#loginIntranetPassword').val();
	var timezone = (-1) * new Date().getTimezoneOffset();
		
	global.user = {};
	global.user.retainid = retainid;

	$.post('login', {
		retainid: retainid,
		retainpassword: retainpassword,
		intranetid: intranetid,
		intranetpassword: intranetpassword,
		timezone: timezone

	}).done(function(data) {
		setCookie('sessionID', data.sessionID);
		console.log(data.sessionID);
		closeDialog();
		$('#userName').text(data.userName);
		$('#loginError').text('');			
		init();

	}).fail(function(err) {
		$('#loginError').text(getErrorMessage(err));
	});
};

function checksession(callback) {
	var sessionID = getCookie('sessionID');
	if (sessionID) {
		$.post('checksession', {

		}).done(function(data) {
			$('#loginError').text('');
			global.user = {};
			global.user.retainid = data.retainid;
			$('#userName').text(data.name);
			callback();

		}).fail(function(err) {
			$('#loginError').text(getErrorMessage(err));
			logout();
		});

	} else {
		showDialog('loginDialog');
	}
};

function logout() {
	$('#mainContent').empty();
	clearCookie('sessionID');
	$('#userName').text('');
	checksession();
};

