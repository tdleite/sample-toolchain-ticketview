var global = {};

$(window).load(function() {
	checksession(function() {
		init();		
	});
});

function init() {
	loadEngineers();
	loadClients();
	loadQueues();
	loadViews();	
};


