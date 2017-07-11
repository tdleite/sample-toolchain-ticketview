var PMR = {

	read : function(attributes, callback) {
		$.post('pmr/read', attributes)
		
		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
	
	update : function(attributes, callback) {
		$.post('pmr/update', attributes)
		
		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
};

$(window).load(function() {

	$('body').on('click', '.postIt', function() {
		clearPMRDetails();
		showLoading();		
		var pmr = $(this).attr('pmrnumber');
		PMR.read({pmr: pmr}, function(data) {
			showDialog('pmrDetailsDialog');
			PMRDetails(data.objects[0]);
		});
	});

	$('body').on('keyup', '#searchPMR', function(e) {
		if (e.which == 13 && $(this).val()) {
			searchPMR();
		}
	});
	
	$('body').on('click', '#editCustomColumnDialog .help', function() {
		calculateExpressionHelpDialogSize();
		showDialog('expressionHelpDialog');
	});
	
	$('body').on('click', '#pmrDetailsActionsSetResolver5', function() {
		var pmr = $('#pmrDetailsInformationPmrNumber').text();
	
		var html = '<input type="text" style="width: 250px;" id="inputDialogResolver5" list="engineers"><datalist id="engineers">';
		for (var i=0; i < global.engineers.length; i++) {
			html += '<option>' + global.engineers[i].name + '</option>';
		}
		html += '</datalist>';
				
		showInputDialog('Set Resolver 5', '250px', '100px', html, function() {
			var resolver5 = $('#inputDialogResolver5').val();
			if (resolver5) {
				showLoading();
				resolver5 = getEngineerID(resolver5);				
				PMR.update({pmr: pmr, resolver5: resolver5}, function(data) {					
					buildView();
				});
				closeDialog();
				closeDialog();
			}
		});
	});
	
	$('body').on('click', '#pmrDetailsActionsChangeQueue', function() {
		var pmr = $('#pmrDetailsInformationPmrNumber').text();
	
		var html = '<input type="text" style="width: 250px;" id="inputDialogQueue" list="queues"><datalist id="queues">';
		for (var i=0; i < global.queues.length; i++) {
			html += '<option>' + global.queues[i].label + '</option>';
		}
		html += '</datalist>';
				
		showInputDialog('Change Queue', '250px', '100px', html, function() {
			var queue = $('#inputDialogQueue').val();
			if (queue) {
				showLoading();
				queue = getQueue(queue);				
				PMR.update({pmr: pmr, queue: queue}, function(data) {					
					buildView();
				});
				closeDialog();
				closeDialog();
			}
		});
	});
	
	$(window).resize(function() {
		calculatePMRDetailsDialogSize();
		calculateExpressionHelpDialogSize();
	});
});

function calculatePMRDetailsDialogSize() {
	var height = $(window).height();
	if (height % 2 != 0)
		height++;
	$('#pmrDetailsDialog .dialogContainer').height((height - 200) + 'px');
	
	var height = $('#pmrDetailsDialog .dialogContainer').height() - ($('#pmrDetailsDialog .dialogBody').offset().top - $('#pmrDetailsDialog .dialogContainer').offset().top) - 10;
	$('#pmrDetailsDialog .dialogBody').height(height + 'px');
	$('#pmrDetailsText').height((height - 10) + 'px');
	$('#pmrDetailsDialog .accordion').height((height - 10) + 'px');
	
	$('#pmrDetailsDialog .dialogContainer').css('width', '');
	$('#pmrDetailsDialog .accordion').css('width', '');
	var width = $('#pmrDetailsDialog .accordion').width() + $('#pmrDetailsText').width() + 85;
	$('#pmrDetailsDialog .dialogContainer').width(width + 'px');
	$('#pmrDetailsDialog .accordion').width((width - $('#pmrDetailsText').width() - 85) + 'px');
};

function calculateExpressionHelpDialogSize() {
	var height = $(window).height();
	if (height % 2 != 0)
		height++;
	$('#expressionHelpDialog .dialogContainer').height((height - 300) + 'px');
	
	var height = $('#expressionHelpDialog .dialogContainer').height() - ($('#expressionHelpDialog .dialogBody').offset().top - $('#expressionHelpDialog .dialogContainer').offset().top) - 40;
	$('#expressionHelpDialog .dialogBodyDivBody').height(height + 'px');

	var width = $('#expressionHelpDialog .dialogContainer').width() - 20;
	$('#expressionHelpDialog .dialogBody').width(width + 'px');
};

function searchPMR() {
	var pmr = $('#searchPMR').val();
	$('#searchPMR').prop('value', '');
	showLoading();
	clearPMRDetails();
	PMR.read({pmr: pmr}, function(data) {
		if (data.objects.length > 0) {
			showDialog('pmrDetailsDialog');			
			PMRDetails(data.objects[0]);
		} else {
			showError({responseJSON: {message: 'PMR ' + pmr + ' not found.'}});
		}
	});
};

function PMRDetails(pmr) {
	$('#pmrDetailsDialog .dialogTitle').text('PMR ' + pmr.number);
	
	$('#pmrDetailsInformationComment').text(pmr.comment);
	$('#pmrDetailsInformationPmrNumber').text(pmr.number);
	$('#pmrDetailsInformationSeverity').text(pmr.severity);
	$('#pmrDetailsInformationCritSit').text(pmr.critsit);	
	$('#pmrDetailsInformationAge').text(pmr.age);
	
	$('#pmrDetailsInformationPmrQueue').text(getQueueName(pmr.queue));
	$('#pmrDetailsInformationTimeOnQueue').text(pmr.daysonqueue);
	
	$('#pmrDetailsInformationPmrOwner').text(getEngineerName(pmr.owner));
	$('#pmrDetailsInformationPmrResolver').text(getEngineerName(pmr.resolver));
	$('#pmrDetailsInformationResolver5').text(getEngineerName(pmr.resolver5));
	
	var clientName = getClientName(pmr.icn);
	if (!clientName)
		clientName = pmr.client;
	$('#pmrDetailsInformationClient').text(clientName);
	
	$('#pmrDetailsInformationAPAR').text(pmr.apar);	
	
	PMRStatus.read({pmr: pmr.number}, function(data) {
		if (data.objects.length == 0)
			$('#pmrDetailsStatusHistory .accordionGroupItemsDiv').prepend('<p>No Status History entries.</p>');
		
		for (var i=0; i < data.objects.length; i++) {
			$('#pmrDetailsStatusHistory .accordionGroupItemsDiv').prepend('<p>' + getEngineerName(data.objects[i].changedby) + ' moved to status ' + data.objects[i].status + ' on ' + data.objects[i].changedon + '</p>');
		}
		
		if ($('#pmrDetailsInformation .accordionGroupHeaderDiv').attr('title') == 'Expand') {
			$('#pmrDetailsInformation .accordionGroupHeaderDiv').click();
		}
		calculatePMRDetailsDialogSize();
		hideLoading();
	});	
	
	var text = [];
	for (var t in pmr.text) {
		if (pmr.text[t].attr > 0) {
			pmr.text[t].children = [];
			text.push(pmr.text[t]);
		
		} else {
			text[text.length-1].children.push(pmr.text[t]);
		}
	}

	for (var p in pmr.privatenotes) {
		text.push(pmr.privatenotes[p]);
	}	

	text.sort(function(a, b){
	    a = new Date(a.date);
	    b = new Date(b.date);	    
	    if(a < b) return -1;
	    if(a > b) return 1;
	    return 0;
	});
	
	for (var j=0; j < text.length; j++) {
		if (!text[j].body) {
			$('#pmrDetailsText').append('<p class="pmrTextLineSystem">' + text[j].formattedText + '</p>');
			for (var k=0; k < text[j].children.length; k++) {
				$('#pmrDetailsText').append('<p class="pmrTextLineNormal">' + text[j].children[k].formattedText + '</p>');
			}
			
		} else {
			$('#pmrDetailsText').append('<p class="pmrTextLineSystem">' + text[j].formattedText + '</p>');
			$('#pmrDetailsText').append('<div class="pmrTextLinePrivateNote">' + text[j].body + '</div>');
		}
	}
	
	var files;
	var mails;
	
	for (var i=0; i < pmr.ecurepdata.length; i++) {
		if (pmr.ecurepdata[i].name == '0-all_data') {
			files = pmr.ecurepdata[i];
		
		} else if (pmr.ecurepdata[i].name == 'mail') {
			mails = pmr.ecurepdata[i];
		}
	}
	
	if (files && files.children) {
		for (var i=0; i < files.children.length; i++) {
			var href = 'https://ecurep.mainz.de.ibm.com/rest/download/' + files.path + '/' + files.children[i];
			var html = '<div><img src="img/file.png"><a target="_blank" href="' + href + '">' + files.children[i] + '</a></div>';
			$('#pmrDetailsFiles .accordionGroupItemsDiv').append(html);
		}
	}
	
	if (mails && mails.children) {
		for (var i=0; i < mails.children.length; i++) {
			var href = 'https://ecurep.mainz.de.ibm.com/rest/download/' + mails.path + '/' + mails.children[i];		
			var html = '<div><img src="img/mail.png"><a target="_blank" href="' + href + '">' + mails.children[i] + '</a></div>';
			$('#pmrDetailsEmails .accordionGroupItemsDiv').append(html);
		}
	}	
};

function clearPMRDetails() {
	$('#pmrDetailsDialog .dialogTitle').text('');	
	$('#pmrDetailsInformationComment').text('');
	$('#pmrDetailsInformationPmrNumber').text('');
	$('#pmrDetailsInformationSeverity').text('');
	$('#pmrDetailsInformationCritSit').text('');	
	$('#pmrDetailsInformationAge').text('');	
	$('#pmrDetailsInformationPmrQueue').text('');
	$('#pmrDetailsInformationTimeOnQueue').text('');	
	$('#pmrDetailsInformationPmrOwner').text('');
	$('#pmrDetailsInformationPmrResolver').text('');
	$('#pmrDetailsInformationResolver5').text('');
	$('#pmrDetailsInformationClient').text('');
	
	$('#pmrDetailsStatusHistory .accordionGroupItemsDiv').empty();
	$('#pmrDetailsFiles .accordionGroupItemsDiv').empty();
	$('#pmrDetailsEmails .accordionGroupItemsDiv').empty();
	$('#pmrDetailsText').empty();	
};

function isPMRAged(pmr) {
	if ((pmr.severity == '1' || pmr.severity == 1) && parseInt(pmr.age) > 30)
		return true;
	else if ((pmr.severity == '2' || pmr.severity == 2) && parseInt(pmr.age) > 60)
		return true;
	else if ((pmr.severity == '3' || pmr.severity == 3) && parseInt(pmr.age) > 90)
		return true;
	else if ((pmr.severity == '4' || pmr.severity == 4) && parseInt(pmr.age) > 90)
		return true;
	else
		return false;
};

function evalExpression(expression, pmr) {
	expression = expression.toLowerCase();
	
	expression = expression.replace(/\r?\n/g, ' ');	
	expression = expression.replace('  ', '');
	
	var words = expression.split(' ');
	for (var i=0; i < words.length; i++) {
		if (words[i] == 'like') {
			var like = words[i+1];
			words[i-1] = words[i-1] + '.indexOf(' + like + ') >= 0';
			words[i] = "";
			words[i+1] = "";
		}
	}
	expression = words.join(' ');
	
	expression = replaceAll(expression, 'monitoringqueue', queueIsMonitoring(pmr.queue));
	expression = replaceAll(expression, 'age', pmr.age);
	expression = replaceAll(expression, 'comment', pmr.comment);
	expression = replaceAll(expression, 'icn', "'" + pmr.icn + "'");
	expression = replaceAll(expression, 'client', "'" + getClientName(pmr.icn) + "'");
	expression = replaceAll(expression, 'critsit', "'" + pmr.critsit + "'");
	expression = replaceAll(expression, 'ownerid', "'" + pmr.owner + "'");
	expression = replaceAll(expression, 'ownername', "'" + getEngineerName(pmr.owner) + "'");
	expression = replaceAll(expression, 'number', "'" + pmr.number + "'");
	expression = replaceAll(expression, 'queuename', "'" + getQueueName(pmr.queue) + "'");
	expression = replaceAll(expression, 'daysonqueue', pmr.daysonqueue);
	expression = replaceAll(expression, 'queue', "'" + pmr.queue + "'");
	expression = replaceAll(expression, 'resolverid', "'" + pmr.resolver + "'");
	expression = replaceAll(expression, 'resolvername', "'" + getEngineerName(pmr.resolver) + "'");
	expression = replaceAll(expression, 'resolver5id', "'" + pmr.resolver5 + "'");
	expression = replaceAll(expression, 'resolver5name', "'" + getEngineerName(pmr.resolver5) + "'");
	expression = replaceAll(expression, 'severity', pmr.severity);
	
	var pmrStatus = getPMRStatus(pmr);
	if (pmrStatus.length > 0)
		expression = replaceAll(expression, 'status', "'" + pmrStatus + "'");
	else
		expression = replaceAll(expression, 'status', "''");
		
	expression = expression.toLowerCase();
	expression = replaceAll(expression, 'indexof', 'indexOf');
	
	return eval(expression);
};

function validateExpression(expression) {
	var pmr = {};
	pmr.age = 100;
	pmr.icn = 'test';
	pmr.client = 'test';
	pmr.critsit = 'N';
	pmr.owner = 'test';
	pmr.ownername = 'test';
	pmr.number = 'test';
	pmr.queue = 'test';
	pmr.queuename = 'test';
	pmr.resolver = 'test';
	pmr.resolvername = 'test';
	pmr.resolver5 = 'test';
	pmr.resolver5name = 'test';
	pmr.severity = 2;
	pmr.daysonqueue = 50;
	
	try {
		evalExpression(expression, pmr);
		return true;
		
	} catch (e) {
		return false;
	}
};
