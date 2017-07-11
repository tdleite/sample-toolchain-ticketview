var PrivateNote = {
		
		create : function(attributes, callback) {
			$.post('privatenote/create', attributes)

			.done(function(data) {
				callback(data);

			}).fail(function(err) {
				showError(err);
			});
		},
};

$(window).load(function() {
	$('body').on('click', '#pmrDetailsActionsAddPrivateNote', function() {
		var pmr = $('#pmrDetailsInformationPmrNumber').text();
		var html = '<textarea id="inputDialogBody" cols="120" rows="25"></textarea>';
		showInputDialog('Add Private Note', '300px', '650px', html, function() {
			var body = $('#inputDialogBody').val();
			if (body) {
				PrivateNote.create({pmr: pmr, body: body}, function() {
					PMR.read({pmr: pmr}, function(data) {
						PMRDetails(data.objects[0]);
					});
				});
				closeDialog();
			}
		});
	});
});