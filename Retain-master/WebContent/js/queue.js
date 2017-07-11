var Queue = {

	read : function(attributes, callback) {
		$.post('queue/read', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
};

function loadQueues() {
	$('#editViewAllQueues .accordionGroupItemsDiv').empty();	
	Queue.read({}, function(data) {
		global.queues = data.objects;
		for (var i=0; i < data.objects.length; i++) {
			$('#editViewAllQueues .accordionGroupItemsDiv').append('<p columntype="queue" columnvalue="' + data.objects[i].name + '">' + data.objects[i].label + '</p>');
			$('#expressionHelpAllQueues .accordionGroupItemsDiv').append('<p columntype="queue" columnvalue="' + data.objects[i].name + '">' + data.objects[i].label + ' (' + data.objects[i].name +  ')</p>');
			$('#editMonitoringQueuesAvailableQueues .dialogBodyDivBody').append('<p queue="' + data.objects[i].name + '">' + data.objects[i].label + '</p>');
		}
	});
};

function getQueueName(queue) {
	for (var i=0; i < global.queues.length; i++) {
		if (global.queues[i].name === queue) {
			return global.queues[i].label;
		}
	}
	return queue;
};

function getQueue(label) {
	for (var i=0; i < global.queues.length; i++) {
		if (global.queues[i].label === label) {
			return global.queues[i].name;
		}
	}
	return label;
};