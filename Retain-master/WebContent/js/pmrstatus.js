var PMRStatus = {

	create : function(attributes, callback) {
		$.post('pmrstatus/create', attributes)
		
		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},

	read : function(attributes, callback) {
		$.post('pmrstatus/read', attributes)

		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
};

function loadPMRStatus(callback) {
	PMRStatus.read({}, function(data) {
		global.pmrstatus = data.objects;
		callback();
	});
};

function getPMRStatus(pmr) {
	if (global.pmrstatus) {
		for (var i=0; i < global.pmrstatus.length; i++) {
			if (pmr.number === global.pmrstatus[i].pmr) {
				return global.pmrstatus[i].status;
			}
		}
	}
	return '';
};
