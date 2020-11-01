define(["dojo/_base/declare",
    	"dojo/_base/lang",
    	"dijit/registry",
    	"dojo/_base/array",
    	"dojo/dom",
        "dojo/dom-construct",
    	"dijit/Dialog",
        "com/ibm/rules/decisioncenter/extensions/ExtensionMixin"
], function(declare, lang, registry, array, dom, domConstruct, Dialog, ExtensionMixin) {
	
	// Entry point to add a new button in the toolbar of the view
		
	return declare([ExtensionMixin], {

		/* override */ inBranchView: function(data) {
			// Adds a new button in a context of a branch
			
			this._inView(data);
		},
		
		/* override */ inReleaseView: function(data) {
			// Adds a new button in a context of a release
			
			this._inView(data);
		},
		
		/* override */ inActivityView: function(data) {
			// Adds a new button in a context of an activity
			
			this._inView(data);
		},
		
		_inView: function(data) {
			// Adds a new button
			
			// Loads CSS file to style the button
			this.loadCss('/extensions/css/mybutton.css');
			
			this.addToolbarButton('PasteClipboardButton', 'Paste to Package', lang.hitch(this, function() { this._onPaste(data) }));
			this.addToolbarButton('CopyClipboardButton', 'Copy to Clipboard', lang.hitch(this, function() { this._onCopyToClipboard(data) }));
			  
		},

		_onPaste: function (data) {
			var onSuccess = function(data) {
				// Display a dialog that display a few metrics
				console.log("copy to package successful");
				registry.byId('contentsViewTab')._refresh();
			};
			// On error, display an error in the console
			var onError = function(err) {
				console.log(err);
			};
			

			
			// not in the decision artifacts view ignore the button
			if (currentView.currentTabId == undefined || !currentView.currenTabId == "contentsTab")
				return;

			var view = registry.byId('contentsViewTab');
			var selectedItems =view.getCurrentView().getContentTree().selectedItems;
			// element selected in the middle part
			// var selectedArtifact =
			// view.getCurrentView().detailsView._getSelectedElementsIds();
			// get the content of the selection cookie
			if (selectedItems.length == 0)
				return;
			
			var params = { target : [] };
			
			
			for (var i = 0; i < selectedItems.length; i++) {
				
				selection = selectedItems[i];	
				
				// ignoring elements in the tree and folder not rule packages or
				// projects
				if (selection.folderIds == undefined || selection.inArtifactGroupFolderHierarchy != null)
				{
					console.log ( "ignoring " + selection.name );
					continue;
				}
				if (selection.root)
				{
					console.log( "branchId="+  selection.baselineId + " project="+ selection.name + "("+selection.folderIds[0]+")");		
				}else
				{
					console.log( "branchId="+  selection.baselineId + " package="+ selection.name +"("+selection.folderIds[0]+")");		
				}
				params.target.push( selection.baselineId +"~"+ selection.folderIds[0])
			}
			
			//Send an HTTP request to get data from the server
			// unless there is no target detected
			if (params.target.length == 0)
				return;
			
			this.sendRequest('/ext/custom/copyToBranch',
			{ onSuccess : onSuccess, onError: onError, parameters: params},
			 'POST');
		},		
		
		_onCopyToClipboard: function (data) {
			// Triggered when the button is clicked on a rule or a decision
			// table.
					
			var onSuccess = function(data) {
				// Display a dialog that display a few metrics
				console.log("copy to clipboard successful");
			};

			// On error, display an error in the console
			var onError = function(err) {
				console.log(err);
			};
			
			var view = registry.byId('contentsViewTab');
			var selectedItems =view.getCurrentView().getContentTree().selectedItems;
			var detailsView = view.getCurrentView().detailsView;
			if (detailsView && detailsView.declaredClass == "com.ibm.rules.decisioncenter.library.contents.PackageFolderDetails")
			{
				// element selected in the middle part
				var selectedArtifacts = detailsView._getSelectedElementIds();

				// if nothing selected clear the clipboard
				// uncomment if you want to do nothing instead
				// if (selectedArtifacts.length == 0 )
				//{
				//	return;
				//}
				var baselineId = detailsView.gridBaselineId;
				
				var params = {
					branchId: baselineId,
					rulesId:  selectedArtifacts
				};
				
				// Send an HTTP request to get data from the server
				this.sendRequest('/ext/custom/saveToClipboard', 
						{ onSuccess : onSuccess, onError: onError, parameters: params},
						'POST');
			}
		}		

	});
});
