/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
* Copyright IBM Corp. 1987, 2019. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/
package com.ibm.rules.decisioncenter.web.clipboardActions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.rules.decisioncenter.web.ExtUtils;

import ilog.rules.res.model.IlrIllegalArgumentRuntimeException;
import ilog.rules.teamserver.brm.IlrBaseline;
import ilog.rules.teamserver.brm.IlrBrmPackage;
import ilog.rules.teamserver.brm.IlrRuleProject;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrElementDetails;
import ilog.rules.teamserver.model.IlrElementHandle;
import ilog.rules.teamserver.model.IlrSession;

/**
 * Class to server HTTP requests used by the extension point
 *
 */

@Controller
@RequestMapping("/ext/clipboard")
public class ClipboardController {

	private static Logger LOGGER = Logger.getLogger(ClipboardController.class.getName());
	private static String CLIPBOARD_ATT = "clipboard";
	
	@RequestMapping(value = "/saveToClipboard", method = RequestMethod.POST)
	public @ResponseStatus(HttpStatus.NO_CONTENT) void addToClipboard(
			@RequestParam(value = "branchId", required = true) String branchId,
			@RequestParam(value = "rulesId", required = false) String[] RulesId) throws IlrApplicationException {
		
		// clear the clipboard
		IlrSession session = ExtUtils.retrieveSession();
		if (RulesId == null || RulesId.length == 0)
		{
			session.removeAttribute(CLIPBOARD_ATT);
			return;
		}
		
		// add new element if any
		JSONArray CLIPBOARD =  new JSONArray();
		for (int i = 0; i < RulesId.length; i++) {
			JSONObject rule = new JSONObject();
			rule.put("branchId", branchId);
			rule.put("ruleId", RulesId[i]);
			LOGGER.info("adding to clipboard: "+ rule);
			CLIPBOARD.add(rule);
		}
		session.setAttribute(CLIPBOARD_ATT, CLIPBOARD);
	}

	/**
	 * @param target: list of branchId~FolderId strings 
	 * @throws IlrApplicationException
	 * copy the rules in the clipboard to each folder listed in target. If the folderId is a project,
	 * rules are copied at the root of this project.
	 */
	@RequestMapping(value = "/copyToBranch", method = RequestMethod.POST)
	public @ResponseStatus(HttpStatus.NO_CONTENT) void saveToFolder(
			@RequestParam(value = "target", required = true) String[] target) throws IlrApplicationException {
		IlrSession session = ExtUtils.retrieveSession();
		List<List<IlrElementDetails>> targetPackages = new ArrayList<>();
		JSONArray CLIPBOARD = (JSONArray) session.getAttribute(CLIPBOARD_ATT);
		if (CLIPBOARD == null || CLIPBOARD.isEmpty())
			throw (new IlrIllegalArgumentRuntimeException("No artifact in clipboard"));

		//build the list of element details (branch, folder)
		//if the element is a project the folder is null copy to the root of that project
		for (int i = 0; i < target.length; i++) {
			LOGGER.info( target[i]);
			String[] targetPack = target[i].split("~");
			String baselineId = targetPack[0];
			String folderId = targetPack[1];
			IlrElementHandle destinationBranch = session.stringToElementHandle(baselineId);
			IlrElementHandle destinationFolder = session.stringToElementHandle(folderId);
			IlrElementDetails destBranchDetail = session.getElementDetails(destinationBranch);
			IlrElementDetails elt = session.getElementDetails(destinationFolder);
			ArrayList<IlrElementDetails> destination = new ArrayList<>();
			destination.add(destBranchDetail);
			destination.add(elt instanceof IlrRuleProject ? null : elt);
			targetPackages.add(destination);
		}

		for (int i = 0; i < CLIPBOARD.size(); i++) {

			IlrElementHandle sourceBranch = session
					.stringToElementHandle((String) ((JSONObject) CLIPBOARD.get(i)).get("branchId"));
			IlrElementHandle sourceRule = session
					.stringToElementHandle((String) ((JSONObject) CLIPBOARD.get(i)).get("ruleId"));
			LOGGER.info( "copying: " +sourceRule + " from "+ sourceBranch.toIdString() );

			IlrBaseline wb = session.getWorkingBaseline();
			session.setWorkingBaseline((IlrBaseline) session.getElementDetails(sourceBranch));
			for (List<IlrElementDetails> destination : targetPackages) {
				LOGGER.info( "to: "+ (destination.get(1) == null? "root": destination.get(1).getName()) + " in "+ destination.get(0).getName());
				session.copyTo(sourceRule, null, destination.get(1), (IlrBaseline) destination.get(0), false, null);
			}
			session.setWorkingBaseline(wb);

		}
	}

}
