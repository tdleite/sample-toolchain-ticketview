var Engineer = {

	read : function(attributes, callback) {
		$.post('engineer/read', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
};

function loadEngineers() {
	Engineer.read({}, function(data) {
		global.engineers = data.objects;
		for (var i=0; i < data.objects.length; i++) {
			$('#editViewEngineers .accordionGroupItemsDiv').append('<p columntype="engineer" columnvalue="' + data.objects[i].retainid + '">' + data.objects[i].name + '</p>');
			$('#expressionHelpEngineers .accordionGroupItemsDiv').append('<p columntype="engineer" columnvalue="' + data.objects[i].retainid + '">' + data.objects[i].name + ' (' + data.objects[i].retainid + ')</p>');
		}
	});
};

function getEngineerName(retainid) {
	for (var i=0; i < global.engineers.length; i++) {
		if (global.engineers[i].retainid === retainid) {
			return global.engineers[i].name;
		}
	}
	return retainid;
};

function getEngineerID(name) {
	for (var i=0; i < global.engineers.length; i++) {
		if (global.engineers[i].name === name) {
			return global.engineers[i].retainid;
		}
	}
	return name;
};