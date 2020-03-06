package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.RebaseCommand
import org.eclipse.jgit.api.RebaseCommand.InteractiveHandler
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.errors.IllegalTodoFileModification
import org.eclipse.jgit.lib.RebaseTodoLine
import java.io.IOException

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */ /**
 * Snippet which shows how to rebase local changes onto a remote branch.
 *
 * See also http://stackoverflow.com/questions/22945257/jgit-how-to-squash-commits
 *
 * @author dominik.stadler at gmx.at
 */
object RebaseToOriginMaster {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				val handler: InteractiveHandler = object : InteractiveHandler {
					override fun prepareSteps(steps: List<RebaseTodoLine>) {
						// the handler receives the list of commits that are rebased, i.e. the ones on the local branch
						for (step in steps) {
							// for each step, you can decide which action should be taken
							// default is PICK
							try {
								// by selecting "EDIT", the rebase will stop and ask you to edit the commit-contents
								step.action = RebaseTodoLine.Action.EDIT
							} catch (e: IllegalTodoFileModification) {
								throw IllegalStateException(e)
							}
						}
					}

					override fun modifyCommitMessage(oldMessage: String): String {
						return oldMessage
					}
				}
				var result = git.rebase().setUpstream("origin/master").runInteractively(handler).call()
				println("Rebase had state: " + result.status + ": " + result.conflicts)

				// because of the "EDIT" in the InteractiveHandler, the rebase was stopped in-between
				// now you can adjust the commit and continue rebasing with setOperation(RebaseCommand.Operation.CONTINUE)
				// to use the local changes for the commit or setOperation(RebaseCommand.Operation.SKIP) to skip this
				// commit (i.e. remove it from the branch!)
				if (!result.status.isSuccessful) {
					// if rebasing stopped or failed, you can get back to the original state by running it with setOperation(RebaseCommand.Operation.ABORT)
					result = git.rebase().setUpstream("origin/master").runInteractively(handler).setOperation(RebaseCommand.Operation.ABORT).call()
					println("Aborted reabse with state: " + result.status + ": " + result.conflicts)
				}
			}
		}
	}
}