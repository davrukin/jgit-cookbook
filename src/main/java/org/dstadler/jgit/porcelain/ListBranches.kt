package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.api.errors.GitAPIException
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
 * Simple snippet which shows how to list all Branches in a Git repository
 *
 * @author dominik.stadler at gmx.at
 */
object ListBranches {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			println("Listing local branches:")
			Git(repository).use { git ->
				var call = git.branchList().call()
				for (ref in call) {
					println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
				}
				println("Now including remote branches:")
				call = git.branchList().setListMode(ListMode.ALL).call()
				for (ref in call) {
					println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
				}
			}
		}
	}
}