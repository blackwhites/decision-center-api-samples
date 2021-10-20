package com.ibm.odm.tools;

import java.util.Collection;
import java.util.List;

import ilog.rules.teamserver.brm.IlrBOMPathEntry;
import ilog.rules.teamserver.brm.IlrBaseline;
import ilog.rules.teamserver.brm.IlrBrmPackage;
import ilog.rules.teamserver.brm.IlrDependency;
import ilog.rules.teamserver.brm.IlrProjectBOMEntry;
import ilog.rules.teamserver.brm.IlrProjectInfo;
import ilog.rules.teamserver.brm.IlrRuleProject;
import ilog.rules.teamserver.client.IlrRemoteSessionFactory;
import ilog.rules.teamserver.client.internal.BRMServerClient;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrCommitableObject;
import ilog.rules.teamserver.model.IlrConnectException;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.IlrSessionEx;
import ilog.rules.teamserver.model.IlrSessionHelper;


	public class RenameProject extends BRMServerClient {
		private final static String DS_NEWNAME = "-to";
		private final static String RP_ARGNAME = "-ruleProject";
		@Override
		protected void execute(String username, String password, String url, String dataSource, String[] otherArgs) {
			String rpName = null;
			String newName = null;


			if (username == null || password == null || url == null || otherArgs.length < 4 ||((otherArgs.length %2) != 0)) {
				System.out.println("Missing arguments. Use argument "+ DS_NEWNAME  +" and  " + RP_ARGNAME);
				usage();
				return;
			}
			int i = 0;
			int valueIndex = -1;
			for (String param : otherArgs) {
				if (valueIndex == i) {
					i++;
					continue;
				}
				valueIndex = i + 1;
				String value = otherArgs[valueIndex];
				switch (param) {
				case DS_NEWNAME:
					newName = value;
					break;
				case RP_ARGNAME:
					rpName = value;
					break;
				default:
					System.out.println(" unexpected argument " + param);
					usage();
					return;
				}
				i++;
			}
			
			if ((newName == null) ) {
				System.out.println("specify the new name. Use argument "+ DS_NEWNAME );
				usage();
				return;
			}
			if (( rpName == null)) {
				System.out.println("specify the project to rename. Use argument"+ RP_ARGNAME);
				usage();
				return;
			}
			
			IlrSession dataProvider = null;
			
			try {
				dataProvider = getSession(username, password, url, dataSource);
			} catch (IlrConnectException e1) {
				// TODO Auto-generated catch block
				System.out.println("Unable to connect to decision center");
				e1.printStackTrace();
				return;
			}

			renameProject(dataProvider, rpName, newName);
			
			if (dataProvider!=null)
				dataProvider.close();

		}
		
		
		private void renameProject(IlrSession session, String rpName, String newName) {
			
			IlrRuleProject dsProject = null;
				try {
					dsProject = IlrSessionHelper.getProjectNamed(session, rpName);
				} catch (IlrObjectNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if (dsProject == null) {
					System.out.println("Did not find project '" + rpName + "'.");
					return;
				}

				try {
					IlrRuleProject newProject = IlrSessionHelper.getProjectNamed(session, newName);
					if (newProject != null)
					{
						System.out.println("The project " +newName +" already exists.");
						return;
					}
						
				} catch (IlrObjectNotFoundException e2) {
					//expectect to not find this project
				}

			
			String oldName = dsProject.getName();
			System.out.println("renaming " + oldName + " into " + newName);
			IlrBrmPackage brm = session.getBrmPackage();
			IlrCommitableObject co = new IlrCommitableObject(dsProject);
			co.setRootDetails(dsProject);

			dsProject.setRawValue(brm.getModelElement_Name(), newName);
			try {
				session.commit(co);
			} catch (IlrApplicationException e) {
				e.printStackTrace();
			}

			try {

				Collection<IlrRuleProject> allProjects = session.getAccessibleProjects();
				for (IlrRuleProject ilrRuleProject : allProjects) {
					List<IlrBaseline> branches = IlrSessionHelper.getBaselines(session, ilrRuleProject);
					for (IlrBaseline ilrBranch : branches) {
						session.setWorkingBaseline(ilrBranch);
						renameDependencies(session, newName, oldName, ilrBranch);
					}
				}

			} catch (IlrApplicationException e1) {
				e1.printStackTrace();
				return;
			}

		}

		private void renameDependencies(IlrSession session, String newName, String oldName, IlrBaseline branch) throws IlrApplicationException {
			List<IlrDependency> dependencies = branch.getProjectInfo().getDependencies();
			for (IlrDependency dep : dependencies) {
				if (dep.getProjectName().equals(oldName)) {
					System.out.println("renaming dependency " + oldName + " in project " + branch.getProject().getName()
							+ " in branch " + branch.getName());
					IlrProjectInfo projectInfo = branch.getProjectInfo();
					IlrCommitableObject co = new IlrCommitableObject(projectInfo);
					IlrDependency dependency = (IlrDependency) ((IlrSessionEx) session)
							.createElementDetails(session.getBrmPackage().getDependency());
					dependency.setProjectName(newName);
					dependency.setBaselineName(dep.getBaselineName());
					co.addModifiedElement(session.getModelInfo().getBrmPackage().getProjectInfo_Dependencies(), dependency);
					co.addDeletedElement(session.getModelInfo().getBrmPackage().getProjectInfo_Dependencies(), dep);
					List<IlrBOMPathEntry> bomPathEntries = projectInfo.getBomPathEntries();
					for (IlrBOMPathEntry entry : bomPathEntries) {

						if (entry instanceof IlrProjectBOMEntry
								&& ((IlrProjectBOMEntry) entry).getProjectName().equals(oldName)) {
							IlrProjectBOMEntry projectBomEntry = (IlrProjectBOMEntry) ((IlrSessionEx) session)
									.createElementDetails(session.getBrmPackage().getProjectBOMEntry());
							projectBomEntry.setRawValue(session.getBrmPackage().getProjectBOMEntry_ProjectName(), newName);
							projectBomEntry.setRawValue(session.getBrmPackage().getBOMPathEntry_Url(),
									"platform:/" + newName);
							projectBomEntry.setRawValue(session.getBrmPackage().getBOMPathEntry_Order(), entry.getOrder());
							co.addModifiedElement(session.getBrmPackage().getProjectInfo_BomPathEntries(), projectBomEntry);
							co.addDeletedElement(session.getBrmPackage().getProjectInfo_BomPathEntries(), entry);
							break;
						}
					}

					session.commit(co);
					return;
				}

			}
		}


		@Override
		protected String usageArgs() {
			String newLine = System.lineSeparator();
			return RP_ARGNAME + " <projectName> "+ DS_NEWNAME + " <new name>" + newLine
					+ "\tRename the rule project <projecName> to <new name>."+ newLine;
		}

		public static void main(String[] args) {
			(new RenameProject()).doExecute(args);
		}

		private IlrSession getSession(String username, String password, String url, String dataSource)
				throws IlrConnectException {
			IlrSession res;
			IlrRemoteSessionFactory factory = new IlrRemoteSessionFactory();
			factory.connect(username, password, url, dataSource);
			res = factory.getSession();
			factory = null;
			return res;
		}

}
