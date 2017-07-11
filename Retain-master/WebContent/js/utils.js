function setCookie (name,value,days) {
	var expires = '';

	if (days) {
		var date = new Date();
	    date.setTime(date.getTime()+(days*24*60*60*1000));
	    expires = '; expires=' + date.toGMTString();

	} else {
		expires = '';
	}

	document.cookie = name + '=' + value + expires + '; path=/';
};

function getCookie (name) {
	var nameEQ = name + '=';
	var ca = document.cookie.split(';');

	for (var i=0; i < ca.length; i++) {
		var c = ca[i];

		while (c.charAt(0) == ' ') {
	    	c = c.substring(1,c.length);
	    }

		if (c.indexOf(nameEQ) == 0) {
	    	return c.substring(nameEQ.length, c.length);
		}
	}
	return null;
};

function replaceAll(str, find, replace) {
	if (find.indexOf(replace) >= 0) {
		var temp = '@@@@@@@@@@';
		while (str.indexOf(find) >= 0) {
		  	str = str.replace(find, temp);
		  }
		find = temp;
	}
	while (str.indexOf(find) >= 0) {
		str = str.replace(find, replace);
	}
	return str;
}

function clearCookie (name) {
	setCookie(name, '', -1);
};

function downloadJSON(fileName, data) {
	var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data));
	var dlAnchorElem = document.getElementById('download');
	dlAnchorElem.setAttribute("href", dataStr);
	dlAnchorElem.setAttribute("download", fileName);
	dlAnchorElem.click();
}

function showLog(data) {
	console.log(new Date().toString().slice(0,24) + ' - ' + data.message);
	$('#log').text(data.message);
	$('#log').css('display', 'block');
	setTimeout(function() {
		$('#log').text('');
		$('#log').css('display', 'none');
	}, 5000);
};

function getErrorMessage(err) {
	return 'Error: ' + err.responseJSON.message;
};

function showError(err) {
	if (err.status == 401) {
		logout();
		return;
	}

	var errorMessage = getErrorMessage(err);
	console.error(new Date().toString().slice(0,24) + ' - ' + errorMessage);
	$('#log').text(errorMessage);
	$('#log').css('display', 'block');
	setTimeout(function() {
		$('#log').text('');
		$('#log').css('display', 'none');
	}, 5000);
};

function ask(text, yes, no) {
	$('#askQuestion').text(text);

	$('#askYes').unbind('click');
	$('#askYes').bind('click', function() {
		yes();
		closeDialog();
	});

	$('#askNo').bind('click', function() {
		no();
		closeDialog();
	});

	showDialog('askDialog');
};

function showInputDialog(title, width, height, html, callback) {
	$('#inputDialog .dialogTitle').text(title);

	$('#inputDialog .dialogContainer').width(width);
	$('#inputDialog .dialogContainer').height(height);
	
	$('#inputFields').empty();
	$('#inputFields').append(html);

	$('#inputDialog .ok').unbind('click');
	$('#inputDialog .ok').bind('click', function() {
		callback();
	});

	showDialog('inputDialog');
};

var lastShowDialog = [];
function showDialog(name) {
	$('body').css('overflow', 'hidden');

	$('.dialog').css('z-index', '99');
	$('#' + name).css('z-index', '9999');

	$('.dialog').css('background-color', 'rgb(0,0,0,0)');
	$('#' + name).css('background-color', '');

	$('#' + name).css('opacity','1');
	$('#' + name).css('pointer-events','auto');
	lastShowDialog.push(name);
};

function closeDialog() {
	$('#' + lastShowDialog[lastShowDialog.length-1]).css('opacity','0');
	$('#' + lastShowDialog[lastShowDialog.length-1]).css('pointer-events','none');
	lastShowDialog.splice(-1,1);
		
	if (lastShowDialog.length == 0)
		$('body').css('overflow', 'initial');

	else {
		$('#' + lastShowDialog[lastShowDialog.length-1]).css('z-index', '9999');
		$('#' + lastShowDialog[lastShowDialog.length-1]).css('background-color', '');
	}
};

$(window).load(function() {
	$('body').keyup(function(e) {
		if (e.which == 27) {
			closeDialog();
		}
	});
	
	$('body').on('click', 'select', function() {
		$(this).css('color', '#444444');

		if ($(this).children('optgroup').length > 0)
			return;

		if ($(this).children(':first-child').attr('value').length == 0)
			$(this).children(':first-child').remove();
	});

	$('body').click(function(e) {
		if (e.target.className != 'sortPMRs')
			$('.contextMenu').remove();
	});

	$('body').on('click', '.mainContentGroupMinimize', function() {
		$(this).parent().css('height', '');
		$(this).parent().css('min-height', '0px');
		$(this).siblings('.mainContentGroupBody').css('display', 'none');
		$(this).siblings('.mainContentGroupBody').css('height', '');
		$(this).attr('src', 'img/maximize.png');
		$(this).attr('class', 'mainContentGroupMaximize');
		$(this).attr('title', 'Maximize');
	});

	$('body').on('click', '.mainContentGroupMaximize', function() {
		$(this).parent().css('min-height', '');
		$(this).siblings('.mainContentGroupBody').css('display', '');
		$(this).attr('src', 'img/minimize.png');
		$(this).attr('class', 'mainContentGroupMinimize');
		$(this).attr('title', 'Minimize');
	});

	$('.accordion .accordionGroup:first-child .accordionGroupHeaderDiv').attr('title', 'Collapse');
	$('.accordion .accordionGroup:not(:first-child) .accordionGroupHeaderDiv').attr('title', 'Expand');
	$('.accordion .accordionGroup:not(:first-child) .accordionGroupItemsDiv').css('display', 'none');
	$('.accordion .accordionGroup:first-child .accordionGroupHeaderIcon').attr('src', 'img/collapse.png');
	$('.accordion .accordionGroup:not(:first-child) .accordionGroupHeaderIcon').attr('src', 'img/expand.png');
	$('body').on('click', '.accordion .accordionGroupHeaderDiv', function() {
		var expand =  $(this).parent().children('.accordionGroupItemsDiv').css('display') === 'none';
		
		var accordionGroups = $(this).parent().parent().children('.accordionGroup');
		accordionGroups.children('.accordionGroupItemsDiv').css('display', 'none');
		accordionGroups.children('.accordionGroupHeaderDiv').attr('title', 'Expand');
		accordionGroups.find('.accordionGroupHeaderIcon').attr('src', 'img/expand.png');

		if (expand) {
			var thisAccordionGroup = $(this).parent();
			thisAccordionGroup.children('.accordionGroupItemsDiv').css('display', '');
			thisAccordionGroup.children('.accordionGroupHeaderDiv').attr('title', 'Collapse');
			thisAccordionGroup.find('.accordionGroupHeaderIcon').attr('src', 'img/collapse.png');
		}		
	});

	$('.dialog .close').on('click', function() {
		closeDialog();
	});
	
	$('.expression textarea').on('keyup', function() {
		highlightSyntax($(this));
	});
	
	$('.expression textarea').on('scroll', function(){
		$(this).siblings('pre').scrollTop($(this).scrollTop());
	});
});

function highlightSyntax(textArea) {
	var text = textArea.val();
    var temp = '';
    var html = '';
    var isString = false;
    var stringQuote = '';
    var words = text.split('');

    var attributes = [
      'monitoringqueue',
      'age',
      'comment',
      'icn',
      'client',
      'critsit',
      'ownerid',
      'ownername',
      'number',
      'queuename',
      'queue',
      'resolverid',
      'resolvername',
      'resolver5id',
      'resolver5name',
      'severity',
      'daysonqueue'
    ];

    var operators = [
      '==',
      '&&',
      '||',
      'like',
      '>',
      '>=',
      '<',
      '<=',
      '!='
    ];

    for (var w=0; w < words.length; w++) {
        temp += words[w];
        
        if (words[w] == "'" || words[w] == '"') {
        	if (!isString) {
        		isString = true;
        		stringQuote = words[w];
        		html += temp.slice(0, -1);
        		temp = words[w];
        	
        	} else if (stringQuote == words[w]) {
        		isString = false;
        		html += '<span class="expressionValue">' + temp + '</span>';
        		temp = '';
        	}
        	        	
    	} else if (!isString && attributes.includes(temp.toLowerCase()) && (w == words.length - 1 || (w < words.length - 1 && words[w+1].match(/[a-zA-Z]+/g) == null))) {
            html += '<span class="expressionAttribute">' + temp + '</span>';
            temp = '';
        
        } else if (!isString && operators.includes(temp.toLowerCase())) {
       		html += '<span class="expressionOperator">' + temp + '</span>';
            temp = '';        	
            
        } else if (!isString && !isNaN(temp)) {
            html += '<span class="expressionNumber">' + temp + '</span>';
            temp = '';

        } else if (!isString && (words[w] == ' ' || words[w] == '(' || words[w] == ')')) {
        	html += temp;
        	temp = '';
        }
    }
    
    html += temp;
    textArea.siblings('pre').html(html);
};

function showLoading() {
	$('#loading img').attr('src', 'img/loading.gif');
	$('#loading').css('opacity', '1');
	$('#loading').css('pointer-events', 'auto');
};

function hideLoading() {
	$('#loading').css('opacity', '0');
	$('#loading').css('pointer-events', 'none');
};

