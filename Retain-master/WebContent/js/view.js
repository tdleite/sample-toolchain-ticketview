var View = {

	create : function(attributes, callback) {
		$.post('view/create', attributes)
		
		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},

	read : function(attributes, callback) {
		$.post('view/read', attributes)
		
		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
		});
	},
	
	update : function(attributes, callback, error) {
		$.post('view/update', attributes)
		
		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
			error(err);
		});
	},
	
	delete : function(attributes, callback, error) {
		$.post('view/delete', attributes)
		
		.done(function(data) {
			callback(data);

		}).fail(function(err) {
			showError(err);
			error(err);
		});
	},
};

$(window).load(function() {
	$('body').on('click', '#editMonitoringQueues', function() {
		editMonitoringQueues();
	});

	$('body').on('click', '#editMonitoringQueuesDialog .help', function() {
		showDialog('editMonitoringQueuesHelpDialog');
	});
	
	$('body').on('click', '#editView', function() {
		editView();
	});
	
	$('body').on('click', '#deleteView', function() {
		deleteView();
	});

	$('body').on('click', '#exportView', function() {
		exportView();
	});

	$('body').on('click', '#importView', function() {
		importView();
	});

	$('body').on('click', '#editViewAddGroup', function() {
		addEditViewGroup('');
	});

	$('body').on('click', '#editViewDialog .ok', function() {
		createOrUpdateView();
	});

	$('body').on('click', '#editViewDialog .help', function() {
		calculateEditViewHelpDialogSize();
		showDialog('editViewHelpDialog');
	});

	$('body').on('click', '.editViewGroup > .delete', function() {
		deleteGroup($(this));
	});

	$('body').on('click', '.editViewGroupColumn .delete', function() {
		deleteColumn($(this));
	});

	$('body').on('mouseover', '.editViewGroupColumn', function() {
		$(this).parent().parent().css('background-color', '#fff');
	});
	
	$('body').on('click', '#exportToExcel', function() {
		exportToExcel();
	});
	
	$('body').on('click', '#toggleDisplayMode', function() {
		toggleDisplayMode();
	});

	$('body').on('mouseleave', '.editViewGroupColumn', function() {
		$(this).parent().parent().css('background-color', '');
	});

	$('body').on('click', '#refreshView', function() {
		buildView();
	});

	$(window).resize(function() {
		calculateEditViewDialogSize();
		calculateEditViewHelpDialogSize();
		calculateEditMonitoringQueuesDialogSize();
	});
	
	$('#savedViews').on('change', function() {
		buildView();
	});
	
	$('body').on('click', '.sortPMRs', function() {
		openSortPMRsMenu($(this).parent().parent());
	});
	
	$('body').on('click', '.sortPMRsMenu p', function() {
		saveSort($(this));
	});
	
	$('body').on('click', '#editPostIts', function() {
		$('#editPostItsError').text('');
		showDialog('editPostItsDialog');
	});
	
	$('body').on('click', '#editPostItsDialog .help', function() {
		calculateExpressionHelpDialogSize();
		showDialog('expressionHelpDialog');
	});
	
	$('body').on('click', '#editPostItsDialog .ok', function() {
		createOrUpdateView();
	});
	
	$('body').on('click', '#editMonitoringQueuesDialog .ok', function() {
		createOrUpdateView();
	});
	
	$('body').on('click', '.editViewGroupColumn', function(e) {		
		if ($(this).attr('columntype') == 'custom' && e.target.className != 'delete') {
			showCustomColumnDialog($(this));
		}
	});
	
	$('body').on('resize', '.viewGroup', function() {		
		if (resizeTimer) 
			clearTimeout(resizeTimer);
		
		var g = $(this).attr('group');
		
		resizeTimer = setTimeout(function() {
			if (!global.view)
    			return;
    		
    		global.view.json.groups[g].height = $('#viewGroup' + g).height();    		
    		View.update({json: JSON.stringify(global.view.json), viewid: global.view.viewid}, function() {
    		});
      }, 500);		
	});
	
	setInterval(function() {
		if (lastShowDialog.length == 0) {
			buildView();
		}		
	}, 5 * 60 * 1000);
});

var resizeTimer;

function calculateEditMonitoringQueuesDialogSize() {
	var height = $(window).height();
	if (height % 2 != 0)
		height++;
	$('#editMonitoringQueuesDialog .dialogContainer').height((height - 300) + 'px');
	
	var height = $('#editMonitoringQueuesDialog .dialogContainer').height() - ($('#editMonitoringQueuesAvailableQueues .dialogBodyDivBody').offset().top - $('#editMonitoringQueuesDialog .dialogContainer').offset().top) - 40;
	$('#editMonitoringQueuesAvailableQueues .dialogBodyDivBody').height(height + 'px');
	$('#editMonitoringQueuesMonitoringQueues .dialogBodyDivBody').height(height + 'px');

	var width = ($('#editMonitoringQueuesDialog .dialogContainer').width()) / 2;
	$('#editMonitoringQueuesAvailableQueues .dialogBodyDivBody').width(width - 20 + 'px');
	$('#editMonitoringQueuesMonitoringQueues .dialogBodyDivBody').width(width - 20 + 'px');
}

function editMonitoringQueues() {
	makeMonitoringQueuesDraggable();
	$('#editMonitoringQueuesDialog .dialogBodyDivBody').droppable({
	    hoverClass: "",
	    accept: "#editMonitoringQueuesDialog .dialogBodyDivBody p",
	    drop: function(event, ui) {
	    	var queue = ui.draggable.attr('queue');
	    	var label = ui.draggable.text();
	    	var from = ui.draggable.parent().parent().attr('id');
	    	var to = from === 'editMonitoringQueuesMonitoringQueues' ? 'editMonitoringQueuesAvailableQueues' : 'editMonitoringQueuesMonitoringQueues';
	    		
			if (event.target.id === from || event.target.parentElement.id === from) {
				return;
			}
			
			$('#' + to + ' .dialogBodyDivBody').append('<p queue="' + queue + '">' + label + '</p>');
			$('#' + from + ' .dialogBodyDivBody p[queue="' + queue + '"]').remove();
			
			var objs = $('#' + to + ' .dialogBodyDivBody p');
			objs.sort(function(a,b) {
				return (a.attributes.queue.value > b.attributes.queue.value) ? 1 : ((b.attributes.queue.value > a.attributes.queue.value) ? -1 : 0);
			});
			$('#' + to + ' .dialogBodyDivBody').empty();
			for (obj in objs) {
				$('#' + to + ' .dialogBodyDivBody').append(objs[obj].outerHTML);	
			}			
			
			makeMonitoringQueuesDraggable();
	    }
	});

	calculateEditMonitoringQueuesDialogSize();
	showDialog('editMonitoringQueuesDialog');
};

function makeMonitoringQueuesDraggable() {
	$('#editMonitoringQueuesDialog .dialogBodyDivBody p').draggable({
		helper: "clone",
		start: function(event, ui) {
			var from = ui.helper.context.parentElement.parentElement.id;
			var to = '';

			if (from === 'editMonitoringQueuesMonitoringQueues')
				to = 'editMonitoringQueuesAvailableQueues';
	    	else if (from === 'editMonitoringQueuesAvailableQueues')
	    		to = 'editMonitoringQueuesMonitoringQueues';

			$('#' + to + ' .dialogBodyDivBody').css('border', '3px dashed #57417d');
		},
		stop: function(event, ui) {
			$('#editMonitoringQueuesAvailableQueues .dialogBodyDivBody').css('border', '');
			$('#editMonitoringQueuesMonitoringQueues .dialogBodyDivBody').css('border', '');
		}
	});
};

function loadMonitoringQueues() {
	$('#editViewMonitoringQueues .accordionGroupItemsDiv').empty();
	$('#editMonitoringQueuesMonitoringQueues .dialogBodyDivBody').empty();
	$('#expressionHelpMonitoringQueues .accordionGroupItemsDiv').empty();
		
	for (var i=0; i < global.view.json.monitoringqueues.length; i++) {
		var name = getQueueName(global.view.json.monitoringqueues[i]);
		$('#editMonitoringQueuesMonitoringQueues .dialogBodyDivBody').append('<p queue="' + global.view.json.monitoringqueues[i] + '">' + name + '</p>');
		$('#editViewMonitoringQueues .accordionGroupItemsDiv').append('<p columntype="queue" columnvalue="' + global.view.json.monitoringqueues[i] + '">' + name + '</p>');
		$('#expressionHelpMonitoringQueues .accordionGroupItemsDiv').append('<p columntype="queue" columnvalue="' + global.view.json.monitoringqueues[i] + '">' + name + ' (' + global.view.json.monitoringqueues[i] + ')</p>');
	
		$('#editMonitoringQueuesAvailableQueues .dialogBodyDivBody p[queue="' + global.view.json.monitoringqueues[i] + '"]').remove();
	}
	
	makeMonitoringQueuesDraggable();
};

function queueIsMonitoring(queue) {
	if (!global.view.json)
		return false;
	
	for (var j=0; j < global.view.json.monitoringqueues.length; j++) {
		if (global.view.json.monitoringqueues[j] === queue) {
			return true;
		}
	}
	return false;
};

function calculateEditViewDialogSize() {
	var height = $(window).height();
	if (height % 2 != 0)
		height++;
	$('#editViewDialog .dialogContainer').height((height - 200) + 'px');
	
	var height = $('#editViewDialog .dialogContainer').height() - ($('#editViewColumns').offset().top - $('#editViewDialog .dialogContainer').offset().top) - 50;
	$('#editViewColumns .dialogBodyDivBody').height(height + 'px');
	$('#editViewGroups .dialogBodyDivBody').height(height + 'px');

	var width = $('#editViewDialog .dialogContainer').width() - $('#editViewColumns').width() - 40;
	$('#editViewGroups').width(width + 'px');
};

function calculateEditViewHelpDialogSize() {
	var height = $(window).height();
	if (height % 2 != 0)
		height++;
	$('#editViewHelpDialog .dialogContainer').height((height - 300) + 'px');
	
	var height = $('#editViewHelpDialog .dialogContainer').height() - 40;
	$('#editViewHelpDialog .dialogBody').height(height + 'px');
};

function editView() {
	$('#editViewColumns p').draggable({
		helper: "clone",
		start: function(event, ui) {
			$('.editViewGroup').css('border', '3px dashed #57417d');
		},
		stop: function(event, ui) {
			$('.editViewGroup').css('border', '');
		}
	});

	calculateEditViewDialogSize();
	$('#editViewError').text('');
	showDialog('editViewDialog');
};

function addEditViewGroup(label) {
	var html = '<div class="editViewGroup">';
	html += '<img title="Delete" class="delete" src="img/delete.png">';
	html += '<input class="editViewGroupLabel" type="text" placeholder="label" maxlength="20" value="' + label + '">';
	html += '<div class="editViewGroupColumns"></div>';
	html += '</div>';
	$('#editViewAddGroup').before(html);

	$('#editViewAddGroup').parent().sortable();
	$('#editViewAddGroup').parent().disableSelection();

	makeGroupsDroppable();
};

function addEditViewColumn(column) {
	var sortOrder = ' sort="number" order="ascending" ';
	if (column.sort && column.order) {
		sortOrder = ' sort="' + column.sort + '" order="' + column.order + '" ';
	}

	var title = '';
	var img = 'img/postityellow.png';
	if (column.columntype == 'custom') {
		title = 'title="Edit Custom Column"';
		img = 'img/customcolumn.png';
	}	    	
	$('body').append('<span id="temp">' + column.columnlabel + '</span>');
	var html = '<div class="editViewGroupColumn" columntype="' + column.columntype + '" columnvalue="' + column.columnvalue + '" ' + sortOrder + ' style="width: ' + ($('#temp').width() + 30) + 'px;" ' + title + '>';
	$('#temp').remove();
	html += '<img title="Delete" class="delete" src="img/delete.png">';
	html += '<p class="editViewGroupColumnLabel">' + column.columnlabel + '</p>';
	html += '<img class="editViewGroupColumnPostIt" src="' + img + '">';
	html += '</div>';
	$('.editViewGroup:last-of-type .editViewGroupColumns').append(html);
	
	$('.editViewGroupColumns').sortable();
	$('.editViewGroupColumns').disableSelection();
};

function getViewAsTable() {
	var numOfPmrAttributes = 1;
	var pmrHeader = '<td></td>';
	for (a in global.view.json.table.columns) {
		if (a != 'html') {
			numOfPmrAttributes++;
			pmrHeader += '<td style="padding: 3px 10px;">' + global.view.json.table.columns[a] + '</td>';
		}			
	}
	pmrHeader = '<tr style="font-weight: bold; font-size: 12px;" class="viewTableHeader">' + pmrHeader + '</tr>';
	
	var table = '<table style="border-spacing: 0px; font-family: sans-serif;" id="viewTable">';
	var count = 1;
	
	for (g in global.groups) {
		table += '<tr style="font-weight: bold; text-align: center; font-size: 14px;" class="viewTableGroup"><td style="padding: 3px 10px;" colspan="' + numOfPmrAttributes +'">' + global.view.json.groups[g].label  + '</td></tr>';
		
		for (c in global.groups[g].columns) {
			table += '<tr style="font-weight: bold; font-size: 14px; border-top: 1px solid #a9a9a9" class="viewTableColumn"><td style="padding: 3px 10px;" colspan="' + numOfPmrAttributes +'">' + global.view.json.groups[g].columns[c].columnlabel  + '</td></tr>';
			table += pmrHeader;	
			count = 1;
			
			for (p in global.groups[g].columns[c].pmrs) {
				
				table += '<tr style="font-size: 12px;" clas="viewTablePmr"><td style="padding: 3px 10px;">' + count + '</td>';
				for (a in global.view.json.table.columns) {
					if (a != 'html') {
						var value = global.groups[g].columns[c].pmrs[p][global.view.json.table.columns[a]];
						
						if (a == 'owner' || a == 'resolver' || a == 'resolver5') {
							value = getEngineerName(value);
						
						} else if (a == 'queue') {
							value = getQueueName(value);
						
						} else if (a == 'client') {
							value = getClientName(global.groups[g].columns[c].pmrs[p].icn);
							if (!value)
								value = global.groups[g].columns[c].pmrs[p][global.view.json.table.columns[a]];
						}
		
						table += '<td style="padding: 3px 10px; border-left: 1px solid #a9a9a9; white-space: nowrap;">' + value  + '</td>';
					}
						
				}
				table += '</tr>';
				count++;
				
			}
			table += '<tr class="viewTableLineBreak"><td style="padding: 0px 10px; height: 50px;" colspan="' + numOfPmrAttributes +'"></td></tr>';
		}
	}
	
	table += '</table>';
	return table;
};

function makeGroupsDroppable() {
	$('.editViewGroup').droppable({
	    hoverClass: "",
	    accept: "#editViewColumns p",
	    drop: function(event, ui) {
	    	var title = '';
	    	var img = 'img/postityellow.png';
	    	if (ui.draggable.attr('columntype') == 'custom') {
	    		title = 'title="Edit Custom Column"';
	    		img = 'img/customcolumn.png';
	    	}
	    	var id = new Date().getTime();
	    	var idAttr = 'id="' + id + '"';
	    	$('body').append('<span id="temp">' + ui.draggable.text() + '</span>');
	    	var html = '<div ' + idAttr + ' class="editViewGroupColumn" columntype="' + ui.draggable.attr('columntype') + '" columnvalue="' + ui.draggable.attr('columnvalue') + '" style="width: ' + ($('#temp').width() + 30) + 'px;" ' + title + ' sort="number" order="ascending">';
	    	$('#temp').remove();
	    	html += '<img title="Delete" class="delete" src="img/delete.png">';
	    	html += '<p class="editViewGroupColumnLabel">' + ui.draggable.text() + '</p>';
	    	html += '<img class="editViewGroupColumnPostIt" src="' + img + '">';
	    	html += '</div>';
	    	var end = true;
	    	if ($(this).children('.editViewGroupColumns').children('div').length == 0) {
	    		$(this).children('.editViewGroupColumns').append(html);
	    		end = false;
	    	} else {
	    		$(this).find('.editViewGroupColumn').each(function() {
	    			if ($(this).offset().left >= ui.offset.left) {
	    				$(this).before(html);
	    				end = false;
	    				return false;
	    			}
	    		});
	    	}
	    	if (end) {
	    		$(this).children('.editViewGroupColumns').append(html);
	    	}
	    	$(this).children('.editViewGroupColumns').sortable();
	    	$(this).children('.editViewGroupColumns').disableSelection();
	    	
	    	if (ui.draggable.attr('columntype') == 'custom') {
	    		$('#' + id).click();
	    	}
	    	$('#' + id).removeAttr('id');
	    }
	});
};

function deleteGroup(t) {
	t.parent().remove();
};

function deleteColumn(t) {
	t.parent().remove();
};

function createOrUpdateView() {
	var name = $('#editViewName').val();
	var ispublic = $('#editViewIsPublic').is(':checked') ? '1':'0';
	var ismain = $('#editViewMainView').is(':checked') ? '1':'0';
	
	var error = false;
	if (!name) {
		$('#editViewError').text('Please choose a view name');
		$('#editPostItsError').text('Please choose a view name on Edit View dialog');
		return;
	}
	$('.editViewGroupLabel').each(function() {
		if (!$(this).val()) {
			error = true;
			return false;
		}
	}).promise().done(function() {
		if (error) {
			$('#editViewError').text('Please fill all the group labels');
			$('#editPostItsError').text('Please fill all the group labels on Edit View dialog');

		} else {
			$('#savedViews').val('');
			getViewAsJSON(function(json) {
				View.create({name: name, json: JSON.stringify(json), ispublic: ispublic, ismain: ismain, viewid: global.view.viewid}, function(data) {
					showLog(data);
					closeDialog();
					loadViews(global.view.viewid);
				});
			});
		}
	});
};

function loadNoView() {
	$('#mainContent').empty();
	$('.editViewGroup').remove();
	
	var postits = {};
	var attribute = [];
	attribute.push('number');
	attribute.push('comment');
	attribute.push('client');
	attribute.push('severity');
	attribute.push('age');
	attribute.push('daysonqueue');
				
	postits.yellow = {};
	postits.yellow.expression = 'severity == 3';
	postits.yellow.attribute = attribute;
	postits.blue = {};
	postits.blue.expression = '1 == 0';
	postits.blue.attribute = attribute;
	postits.red = {};
	postits.red.expression = 'severity == 1';
	postits.red.attribute = attribute;
	postits.gray = {};
	postits.gray.expression = '1 == 0';
	postits.gray.attribute = attribute;
	postits.purple = {};
	postits.purple.expression = '1 == 0';
	postits.purple.attribute = attribute;
	postits.orange = {};
	postits.orange.expression = 'severity == 2';
	postits.orange.attribute = attribute;
	postits.green = {};
	postits.green.expression = 'severity == 4';
	postits.green.attribute = attribute;
				
	$('.editPostItExpression').each(function() {
		var color = $(this).attr('postItColor');
		$(this).val(postits[color].expression);
		highlightSyntax($(this));
	});
	$('.editPostItAttribute').each(function() {
		var color = $(this).attr('postItColor');
		$(this).val(postits[color].attribute.join('\n'));
	});
	$('#editPostItTable').val(attribute.join('\n'));
	
	editView();
	hideLoading();
};

function buildView() {
	showLoading();
	
	global.view = {};
	$('#mainContent').empty();
	$('.editViewGroup').remove();
	
	var viewid = $('#savedViews option:selected').attr('viewid');
	if (!viewid) {
		loadNoView();
		return;
	}		
	
	View.read({viewid: viewid}, function(data) {
		global.view = data.objects[0];
		global.groups = [];
						
		loadPostItsTableDialogData();						
		loadEditViewDialogData();
		loadMonitoringQueues();
		
		var table = '';

		loadPMRStatus(function() {
			PMR.read({}, function(data) {
				for (var g in global.view.json.groups) {
					global.groups.push({});
					global.groups[g].columns = [];
					
					addEditViewGroup(global.view.json.groups[g].label);
					
					$('#mainContent').append(getPostItsGroupHTML(g));
					
					for (c in global.view.json.groups[g].columns) {
						global.groups[g].columns.push({});
						global.groups[g].columns[c].pmrs = [];
												
						addEditViewColumn(global.view.json.groups[g].columns[c]);
						
						$('#viewGroup' + g + ' .mainContentGroupBody').append(getPostItsColumnHTML(g, c));
												
						for (p in data.objects) {
							global.pmrs = data.objects;
							
							var pmr = data.objects[p];
							var html = getPostItPMRHTML(pmr);
							var add = canAddPMR(pmr, global.view.json.groups[g].columns[c]);
																		
							if (add) {
								pmr.html = html;
								global.groups[g].columns[c].pmrs.push(pmr);
							}
						}
					}
				}
				$('.viewColumn').each(function() {
					sortPMRs($(this));
				});
				loadNotifications();
				hideLoading();
			});
		});
	});
};

function loadEditViewDialogData() {
	$('#editViewName').val(global.view.name);
	$('#editViewIsPublic').prop('checked', global.view.ispublic == '1' ? true:false);
	$('#editViewMainView').prop('checked', global.view.ismain == '1' ? true:false);
};

function exportView() {
		var viewName = $('#savedViews').val();
		downloadJSON(viewName + '.json', global.view.json);
};

function importView() {
	$('#upload').unbind('change');
	$('#upload').bind('change', function(evt) {
		var file = evt.target.files[0];
		var reader = new FileReader();
		reader.onloadend = (function(theFile) {
	        return function(e) {
	        	try {
	        		var json = JSON.parse(e.target.result);
	        		
	        		var html = '<input type="text" style="width: 250px;" id="inputName" placeholder="name">';	        		
	        		showInputDialog("Save View as", '250px', '100px', html, function() {
	        			var name = $('#inputName').val();
	        			if (name) {
	        				View.create({name: name, json: JSON.stringify(json), ispublic: '0', ismain: '0'}, function(data) {
			    				showLog(data);
			    				loadViews(name);
			    			});
	        				closeDialog();
	        			} 	        			
	        		});       		
	        		$('#upload').prop('value', '');
	        	} catch (err) {
	        		showError({responseJSON: {message: 'File selected is not a valid JSON.'}});
	        		$('#upload').prop('value', '');
	        	}
		    };
		})(file);
		if (file)
			reader.readAsText(file);
	});
	$('#upload').click();
};

function makePostItDraggableDroppable() {
	$('.postIt').draggable({
		helper: "clone",
		start: function(event, ui) {
			$(ui.helper).css('z-index', '999');
			$('.viewColumnPostIts.postItDroppable').css('border', '3px dashed #57417d');
		},

		drag: function(event, ui) {
			var mainContentGroupBody = $(this).parent().parent().parent();
			if (($(this).width() + ui.position.left) - (mainContentGroupBody.width() + mainContentGroupBody.offset().left) > 0) {
				$('.mainContentGroupBody').scrollLeft(mainContentGroupBody.scrollLeft()+20);
			} else if ((ui.position.left - mainContentGroupBody.offset().left) < 0) {
				$('.mainContentGroupBody').scrollLeft(mainContentGroupBody.scrollLeft()-20);
			}
		},

		stop: function(event, ui) {
			$('.viewColumnPostIts').css('border', '');
		}
	});

	$('.viewColumnPostIts.postItDroppable').droppable({
	    hoverClass: "",
	    accept: ".postIt",
	    drop: function(event, ui) {
	    	var pmrNo = $(ui.draggable).attr('pmrnumber');
	    	var action = '';
	    	var fromColumnType = $(ui.draggable).parent().parent().attr('columntype');
	    	var fromColumnValue = $(ui.draggable).parent().parent().attr('columnvalue');
	    	var toColumnType = $(this).parent().attr('columntype');
	    	var toColumnValue = $(this).parent().attr('columnvalue');
	    	var toColumnLabel = $(this).parent().attr('columnlabel');

	    	if (fromColumnType === toColumnType && fromColumnValue === toColumnValue)
	    		return;

	    	if (toColumnType === 'queue') {
	    		action = 'send PMR ' + pmrNo + ' to queue ' + toColumnLabel + ' ?';
	    	} else if (toColumnType === 'engineer') {
	    		action = 'assign PMR ' + pmrNo + ' to ' + toColumnLabel + ' as the ' + l + ' (L' + global.monitoring.l + ') ?';
	    	} else if (toColumnType === 'attribute' && toColumnValue.indexOf('severity') >= 0) {
	    		action = 'change PMR ' + pmrNo + ' to ' + toColumnLabel + ' ?';
	    	} else if (toColumnType === 'status') {
	    		action = ' change PMR ' + pmrNo + ' to status ' + toColumnLabel + ' ?';
	    	}

	    	ask('Do you want to ' + action + '', function() {
	    		if (toColumnType === 'status') {
	    			PMRStatus.create({pmr: pmrNo, status: toColumnValue}, function(data) {
	    				showLog(data);
	    				buildView();
	    			});
	    		}

	    	}, function() {});
	    }
	});
};

function loadViews(viewid) {
	global.view = {};
	$('#mainContent').empty();
	$('#savedViewsMyViews').empty();
	$('#savedViewsPublicViews').empty();
	var found = false;
	View.read({}, function(data) {
		for (var i=0; i < data.objects.length; i++) {
			if (data.objects[i].retainid === global.user.retainid) {
				$('#savedViewsMyViews').append('<option viewid="' + data.objects[i].viewid + '" value="' + data.objects[i].name + '">' + data.objects[i].name + '</option>');
				
			} else if (data.objects[i].ispublic === '1') {
				$('#savedViewsPublicViews').append('<option viewid="' + data.objects[i].viewid + '" value="' + data.objects[i].name + '">' + data.objects[i].name + '</option>');
				
			}
			if (data.objects[i].ismain == '1' && data.objects[i].retainid == global.user.retainid && !viewid) {
				$('#savedViews option[viewid=' + data.objects[i].viewid  + ']').attr('selected','selected');
				buildView();
				found = true;
			}
		}
		if (viewid) {
			$('#savedViews option[viewid=' + viewid  + ']').attr('selected','selected');
			buildView();
		} else if (!found) {
			buildView();
		}
	});
};
	
function openSortPMRsMenu(viewColumn) {
	$('.contextMenu').remove();

	var sortButton = $('#' + viewColumn.attr('id') + ' .sortPMRs');
	var group = viewColumn.attr('group');
	var column = viewColumn.attr('column');
	var top = sortButton.offset().top + 20;
	var left = sortButton.offset().left;
	var html = '<div id="sortPMRsMenu' + viewColumn.attr('id') + '" group="' + group + '" column="' + column + '" class="sortPMRsMenu contextMenu" style="top: ' + top + 'px; left: ' + left + 'px">';
	html += '<p sort="age">Age</p>';
	html += '<p sort="client">Client</p>';
	html += '<p sort="number">PMR Number</p>';
	html += '<p sort="queue">Queue</p>';
	html += '<p sort="severity">Severity</p>';
	html += '<p sort="daysonqueue">Days on Queue</p>';	
	html += '</div>';
	$('body').append(html);
	
	var sort = viewColumn.attr('sort');
	var order = viewColumn.attr('order');
	if (sort && order) {
		$('#sortPMRsMenu' + viewColumn.attr('id') + ' p[sort=' + sort + ']').append('<img src="img/' + order + '.png">');
	}
};

function saveSort(option) {
	var group =  option.parent().attr('group');
	var column =  option.parent().attr('column');
	var newSort = option.attr('sort');
	var newOrder;
	
	var order = global.view.json.groups[group].columns[column].order;
	var sort = global.view.json.groups[group].columns[column].sort;
	
	if (order === 'ascending' && sort === option.attr('sort')) {
		newOrder = 'descending';
	} else {
		newOrder = 'ascending';
	}
	
	$('.contextMenu').remove();
		
	global.view.json.groups[group].columns[column].order = newOrder;
	global.view.json.groups[group].columns[column].sort = newSort;
	View.update({json: JSON.stringify(global.view.json), viewid: global.view.viewid}, function() {
		buildView();
	});
};

function sortPMRs(viewColumn) {
	var sort = viewColumn.attr('sort');
	var order = viewColumn.attr('order');
	var group = viewColumn.attr('group');
	var column = viewColumn.attr('column');
	
	if (!sort || !order)
		return;
	
	global.groups[group].columns[column].pmrs.sort(function(a,b) {
		var a = a[sort];
		var b = b[sort];
		
		if (isNaN(a) || isNaN(b)) {
			a = a.toLowerCase();
			b = b.toLowerCase();
		}
			
		if (order === 'ascending')
			return (a > b) ? 1 : ((b > a) ? -1 : 0);
		else
			return (a > b) ? -1 : ((b > a) ? 1 : 0);
	});
	
	if (global.view.json.displaymode == 'table') {
		$('#mainContent').empty();		
		var html = '<div class="mainContentGroup">';
		html += '<img class="mainContentGroupMinimize" title="Minimize" class="minimize" src="img/minimize.png">';
		html += '<div class="mainContentGroupBody">';
		html += '</div>';
		html += '</div>';
		$('#mainContent').append(html);
		$('#mainContent .mainContentGroupBody').append(getViewAsTable());
		return;
	}
	
	viewColumn.find('.postIt').remove();
	for (var i=0; i < global.groups[group].columns[column].pmrs.length; i++) {
		viewColumn.children('.viewColumnPostIts').append(global.groups[group].columns[column].pmrs[i].html);
	}
	viewColumn.find('.nOfPMRs').text(' (' + viewColumn.find('.postIt').length + ')');
	
	makePostItDraggableDroppable();
	$('.mainContentGroup').each(function() {
		var c = $(this).find(".viewColumnPostIts");
		$(this).resizable({
			grid: [10000, 1],
			handles: 's',
			alsoResize: c
		});
		$(this).append('<div class="ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se" style="z-index: 90;"></div>');
	});
};

function getPostItColor(pmr) {
	for (var color in global.view.json.postits) {
		if (evalExpression(global.view.json.postits[color].expression, pmr)) {
			return color;
		}
	}	
	return "yellow";
};

function deleteView() {
	if (!global.view.name)
		return;
	
	ask('Do you want to delete view ' + global.view.name + '?', function() {
		View.delete({name: global.view.name}, function(data) {
			showLog(data);
			loadViews();
			closeDialog();
		});
	}, function() {
		
	});
};

function toggleDisplayMode() {
	if (!global.view)
		return;
	if (global.view.json.displaymode == 'table') {
		global.view.json.displaymode = 'post-its';
	} else {			
		global.view.json.displaymode = 'table';
	}		
	View.update({json: JSON.stringify(global.view.json), viewid: global.view.viewid}, function() {
		buildView();
	});
};

function getPostItsGroupHTML(g) {
	var html = '<div class="mainContentGroup viewGroup" id="viewGroup' + g + '" style="height: ' + global.view.json.groups[g].height + 'px" group="' + g + '">';
	html += '<img class="mainContentGroupMinimize" title="Minimize" class="minimize" src="img/minimize.png">';
	html += '<h3 class="mainContentGroupTitle">' + global.view.json.groups[g].label  + '</h3>';
	html += '<div class="mainContentGroupBody">';
	html += '</div>';
	html += '</div>';
	return html;
};

function getPostItsColumnHTML(g, c) {
	var group = global.view.json.groups[g];
	var column = global.view.json.groups[g].columns[c];
	
	var className = 'viewColumnPostIts';
	if (column.columntype === 'queue'
	|| column.columntype === 'engineer'
	|| (column.columntype === 'status')
	|| (column.columntype === 'attribute' && column.columntype != 'aged' && column.columntype != 'critsit')) {
		className += ' postItDroppable';
	}
			
	var sortOrder = '';
	if (column.sort && column.order) {
		sortOrder = ' sort="' + column.sort + '" order="' + column.order + '" ';
	}
					
	var html = '<div class="viewColumn" id="viewGroup' + g + 'Column' + c  + '" columntype="' + column.columntype + '" columnvalue="' + column.columnvalue + '" columnlabel="' + column.columnlabel + '" ' + sortOrder + ' group="' + g + '" column="' + c + '">';
	html += '<h3 class="viewColumnLabel">' + column.columnlabel + ' <span class="nOfPMRs">(0)</span><img class="sortPMRs" title="Sort" src="img/sort.png"></h3>';
	html += '<div class="' + className + '" style="height: ' + (group.height - 50) + 'px">';
	html += '</div>';
	html += '</div>';
	
	return html;
};

function showCustomColumnDialog(customColumn) {
	$('#editCustomColumnError').text('');
	$('#editCustomColumnLabel').val(customColumn.children('.editViewGroupColumnLabel').text());
	$('#editCustomColumnValue').val(customColumn.attr('columnvalue'));
	highlightSyntax($('#editCustomColumnValue'));
	showDialog('editCustomColumnDialog');
	$('#editCustomColumnDialog .ok').unbind('click');
	$('#editCustomColumnDialog .ok').bind('click', function() {
		var label = $('#editCustomColumnLabel').val();
		var value = $('#editCustomColumnValue').val();				
		if (!(label && value)) {
			$('#editCustomColumnError').text('Please fill column title and where clause.');
			return;
		}				
		if (!validateExpression(value)) {
			$('#editCustomColumnError').text('Expression is not valid.');
			return;
		}				
		customColumn.children('.editViewGroupColumnLabel').text(label);
	 	$('body').append('<span id="temp">' + label + '</span>');
	 	customColumn.css('width', ($('#temp').width() + 30) + 'px');
		$('#temp').remove();
		customColumn.attr('columnvalue', value);
		closeDialog();
	});
};

function loadPostItsTableDialogData() {
	$('.editPostItExpression').each(function() {
		var color = $(this).attr('postItColor');
		$(this).val(global.view.json.postits[color].expression);
		highlightSyntax($(this));
	});
	$('.editPostItAttribute').each(function() {
		var color = $(this).attr('postItColor');
		$(this).val(global.view.json.postits[color].attribute.join('\n'));
	});
	$('#editPostItTable').val(global.view.json.table.columns.join('\n'));
};

function getPostItPMRHTML(pmr) {
	var color = getPostItColor(pmr);
	var html = '<div pmrnumber="' + pmr.number + '" class="postIt" style="background: url(img/postit' + color + '.png)">';
	for (var i in global.view.json.postits[color].attribute) {
		var attribute = global.view.json.postits[color].attribute[i];
			
		if (attribute == 'number') {
			html += '<p><strong>PMR ' + pmr.number + '</strong></p>';
						
		} else if (attribute == 'severity') {
			html += '<p>Sev <strong>' + pmr.severity + '</strong></p>';
						
		} else if (attribute == 'ownername') {
			html += '<p>' + getEngineerName(pmr.owner) + '</p>';
							
		} else if (attribute == 'resolver') {
			html += '<p>' + getEngineerName(pmr.resolver) + '</p>';
							
		} else if (attribute == 'resolver5') {
			html += '<p>' + getEngineerName(pmr.resolver5) + '</p>';
							
		} else if (attribute == 'age') {
			html += '<p><strong>' + pmr.age + '</strong> days old</p>';
							
		} else if (attribute == 'queuename') {
			html += '<p>' + getQueueName(pmr.queue) + '</p>';
						
		} else if (attribute == 'daysonqueue') {
			html += '<p><strong>' + pmr.daysonqueue + '</strong> days on queue ' + getQueueName(pmr.queue) + '</p>';
						
		} else if (attribute.length > 0 && pmr[attribute]) {
			html += '<p>' + pmr[attribute] + '</p>';
		}
	}
	return html;
};

function canAddPMR(pmr, column) {
	var add = false;
	
	var isMonitoringQueue = queueIsMonitoring(pmr.queue);
	var pmrAged = isPMRAged(pmr);

	var clientName = getClientName(pmr.icn);
	if (!clientName)
		clientName = pmr.client;

	var queueName = getQueueName(pmr.queue);
	if (!queueName)
		queueName = pmr.queueName;
	
	if (column.columntype === 'queue' && column.columnvalue === pmr.queue) {
		add = true;

	} else if (column.columntype === 'engineer' && (column.columnvalue === pmr.owner || column.columnvalue === pmr.resolver || column.columnvalue === pmr.resolver5)) {
		add = true;

	} else if (column.columntype === 'client' && column.columnvalue === pmr.icn && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'attribute' && column.columnvalue === 'noowner' && pmr.owner == '' && isMonitoringQueue) {
		add = true;
						
	} else if (column.columntype === 'attribute' && column.columnvalue === 'noresolver' && pmr.resolver == '' && isMonitoringQueue) {
		add = true;
						
	} else if (column.columntype === 'attribute' && column.columnvalue === 'noresolver5' && pmr.resolver5 == '' && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'attribute' && column.columnvalue === 'severity1' && pmr.severity === '1' && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'attribute' && column.columnvalue === 'severity2' && pmr.severity === '2' && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'attribute' && column.columnvalue === 'severity3' && pmr.severity === '3' && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'attribute' && column.columnvalue === 'severity4' && pmr.severity === '4' && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'attribute' && column.columnvalue === 'critsit' && pmr.critsit.trim() === 'Y' && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'attribute' && column.columnvalue === 'aged' && pmrAged && isMonitoringQueue) {
		add = true;

	} else if (column.columntype === 'status' && column.columnvalue === getPMRStatus(pmr)) {
		add = true;
					
	} else if (column.columntype === 'custom' && evalExpression(column.columnvalue, pmr)) {
		add = true;
	}
	
	return add;
}

function exportToExcel() {
	window.open('data:application/vnd.ms-excel,' + encodeURIComponent(getViewAsTable()));
};

function getViewAsJSON(callback) {
	var view = {};
	view.groups = [];
	var group = {};
	var postits = {};
	var error = false;
	var monitoringqueues = [];
	
	$('.editViewGroup').each(function() {
		group = {};
		group.label = $(this).children('.editViewGroupLabel').val();
		group.height = 365;
		group.columns = [];
		view.groups.push(group);
		
	}).promise().done(function() {
		$(this).find('.editViewGroupColumn').each(function() {
			var column = {};
			column.columntype = $(this).attr('columntype');
			column.columnvalue = $(this).attr('columnvalue');
			column.columnvalue = column.columnvalue.replace(/\r?\n/g, ' ');	
			column.columnvalue = column.columnvalue.replace('  ', '');
			column.columnlabel = $(this).children('.editViewGroupColumnLabel').text();
			column.sort = $(this).attr('sort');
			column.order = $(this).attr('order');
			view.groups[$(this).parents('.editViewGroup').index()].columns.push(column);
		
		}).promise().done(function() {
			$('#editMonitoringQueuesMonitoringQueues .dialogBodyDivBody p').each(function() {
				monitoringqueues.push($(this).attr('queue'));

			}).promise().done(function() {
				$('.editPostItExpression').each(function() {
					var expression = $(this).val();
					var color = $(this).attr('postItColor');
						
					if (!expression || !validateExpression(expression)) {
						$('#editPostItsError').text('Expression of ' + color + ' post-it is not valid');
						$('#editViewError').text('Expression of ' + color + ' post-it is not valid on Edit Post-its and Table dialog');
						error = true;
					}
						
					postits[color] = {};
					postits[color].expression = expression;
				
				}).promise().done(function() {
					$('.editPostItAttribute').each(function() {
						var attribute = $(this).val();
						var color = $(this).attr('postItColor');
						
						if (!attribute) {
							$('#editPostItsError').text('Attributes of ' + color + ' post-it are not valid');
							$('#editViewError').text('Attributes of ' + color + ' post-it are not valid on Edit Post-its and Table dialog');
							error = true;
							return;
						}
						
						attribute = attribute.replace(/\r?\n/g, ' ');	
						attribute = attribute.replace('  ', '');
						var attributeWords = attribute.split(' ');			
						postits[color].attribute = [];
						
						for (var i=0; i < attributeWords.length; i++) {
							postits[color].attribute.push(attributeWords[i].toLowerCase());
						}
					
					}).promise().done(function() {
						$('.editPostItAttribute').each(function() {
							var attribute = $(this).val();
							var color = $(this).attr('postItColor');
							
							if (!attribute) {
								$('#editPostItsError').text('Attributes of ' + color + ' post-it are not valid');
								$('#editViewError').text('Attributes of ' + color + ' post-it are not valid on Edit Post-its and Table dialog');
								error = true;
								return;
							}
							
							attribute = attribute.replace(/\r?\n/g, ' ');	
							attribute = attribute.replace('  ', '');
							var attributeWords = attribute.split(' ');			
							postits[color].attribute = [];
							
							for (var i=0; i < attributeWords.length; i++) {
								postits[color].attribute.push(attributeWords[i].toLowerCase());
							}
						
						}).promise().done(function() {
							var columns = $('#editPostItTable').val();
							if (!columns) {
								$('#editPostItsError').text('Attributes of table are not valid');
								$('#editViewError').text('Attributes of table are not valid on Edit Post-its and Table dialog');
								error = true;
								return;
							}
							
							columns = columns.replace(/\r?\n/g, ' ');	
							columns = columns.replace('  ', '');
							var columnsWords = columns.split(' ');			
							var table = {};
							table.columns = [];
							
							for (var i=0; i < columnsWords.length; i++) {
								table.columns.push(columnsWords[i].toLowerCase());
							}				
							
							if (!error) {
								view.postits = postits;
								view.table = table;
								view.displaymode = 'post-its';
								view.monitoringqueues = monitoringqueues;
								callback(view);
							}
						});
					});
				});
			});
		});
	});
};
