var Client = {

	read : function(attributes, callback) {
		$.post('client/read', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
};

function loadClients() {
	Client.read({}, function(data) {
		global.clients = data.objects;
		for (var i=0; i < data.objects.length; i++) {
			$('#editViewClients .accordionGroupItemsDiv').append('<p columntype="client" columnvalue="' + data.objects[i].icn + '">' + data.objects[i].name + '</p>');
			$('#expressionHelpClients .accordionGroupItemsDiv').append('<p columntype="client" columnvalue="' + data.objects[i].icn + '">' + data.objects[i].name + ' (' + data.objects[i].icn + ')</p>');
		}
	});
};

function getClientName(icn) {
	for (var i=0; i < global.clients.length; i++) {
		if (global.clients[i].icn === icn) {
			return global.clients[i].name;
		}
	}
	return undefined;
};