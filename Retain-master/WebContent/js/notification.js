var Notification = {

	create : function(attributes, callback) {
		$.post('notification/create', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
	
	read : function(attributes, callback) {
		$.post('notification/read', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
	
	get : function(attributes, callback) {
		$.post('notification/get', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
	
	ack : function(attributes, callback) {
		$.post('notification/ack', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
};

var numOfNotifications = 0;
function getNotifications() {
	var f = function() {
		Notification.get({}, function(data) {
			
			$('#editNotificationsNotificationsDiv').empty();
			
			var newpmrs = [];
			var alteredpmrs = [];				
	
			for (var i=0; i < data.objects.length; i++) {
				var n = data.objects[i];
				
				if (n.type == "NEW_PMR_ON_QUEUE" && evalExpression($('#editNotificationsNewPMROnQueue').val(), n.pmr)) {
					newpmrs.push(n);
					$('#editNotificationsNotificationsDiv').prepend('<p>PMR ' + n.pmr.number + ' arrived on queue ' + getQueueName(n.pmr.queue) + '.</p>');
				}
				
				if (n.type == "PMR_ALTERED" && evalExpression($('#editNotificationsAlteredPMRs').val(), n.pmr)) {
					alteredpmrs.push(n);
					$('#editNotificationsNotificationsDiv').prepend('<p>PMR ' + n.pmr.number + ' altered</p>');
				}
			}			
			
			numOfNotifications = newpmrs.length + alteredpmrs.length;
			var n = $('#notification');
			var b =  $('#menu');
			
			if (numOfNotifications > 0) {
				n.text(numOfNotifications + ' notification' + (numOfNotifications > 1 ? 's' : ''));
				document.title = 'TicketView (' + numOfNotifications + ')';
				n.css('top', b.offset().top - 2);
				n.css('left', b.offset().left - 90);
				n.css('display', 'block');
			
			} else {
				n.text(0);
				n.css('display', 'none');
				document.title = 'TicketView';
				$('#editNotificationsNotificationsDiv').prepend('<p>No Notifications.</p>');
			}
		});
	};
	f();
	setInterval(f, 3*60*1000);
};

$(window).load(function() {
	$('body').on('click', '#editNotifications', function() {
		calculateEditNotificationsDialogSize();
		showDialog('editNotificationsDialog');
	});
	
	$('body').on('click', '#editNotificationsDialog .ok', function() {
		saveNotifications();
	});
	
	$('body').on('click', '#editNotificationsAck', function() {
		ackNotifications();
	});
	
	$(window).resize(function() {
		calculateEditNotificationsDialogSize();
	});
});	

function calculateEditNotificationsDialogSize() {
	var height = $(window).height();
	if (height % 2 != 0)
		height++;
	$('#editNotificationsDialog .dialogContainer').height((height - 300) + 'px');
	
	var height = $('#editNotificationsDialog .dialogContainer').height() - ($('#editNotificationsDialog .dialogBody').offset().top - $('#editNotificationsDialog .dialogContainer').offset().top) - 10;
	$('#editNotificationsDialog .dialogBody').height(height + 'px');
};

function saveNotifications() {
	var newpmers = $('#editNotificationsNewPMROnQueue').val();
	var alteredpmrs = $('#editNotificationsAlteredPMRs').val();
	
	$('#editNotificationsError').text('');
	
	if (!validateExpression(newpmers)) {
		$('#editNotificationsError').text('Expression of New PMRs On Queue is not valid');
		return;
	}
	
	if (!validateExpression(alteredpmrs)) {
		$('#editNotificationsError').text('Expression of Altered PMRs is not valid');
		return;
	}
	
	var json = {};	
	json.newpmrs = newpmers;
	json.alteredpmrs = alteredpmrs;
	
	Notification.create({json: JSON.stringify(json)}, function(data) {
		showLog(data);
		closeDialog();
	});
};

function loadNotifications() {
	Notification.read({}, function(data) {
		if (data.objects.length > 0) {
			$('#editNotificationsNewPMROnQueue').val(data.objects[0].json.newpmrs);
			highlightSyntax($('#editNotificationsNewPMROnQueue'));
			$('#editNotificationsAlteredPMRs').val(data.objects[0].json.alteredpmrs);
			highlightSyntax($('#editNotificationsAlteredPMRs'));
		
		} else {
			$('#editNotificationsNewPMROnQueue').val("1 == 0");
			highlightSyntax($('#editNotificationsNewPMROnQueue'));
			$('#editNotificationsAlteredPMRs').val("1 == 0");
			highlightSyntax($('#editNotificationsAlteredPMRs'));
		}
		getNotifications();
	});
};

function ackNotifications() {
	Notification.ack({}, function(data) {
		var n = $('#notification');
		n.text(0);
		n.css('display', 'none');
		document.title = 'TicketView';
		$('#editNotificationsNotificationsDiv').empty();
		$('#editNotificationsNotificationsDiv').prepend('<p>No Notifications.</p>');
		closeDialog();
	});
};