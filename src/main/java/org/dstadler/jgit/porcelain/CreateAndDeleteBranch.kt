package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
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
 * Simple snippet which shows how to create and delete branches
 *
 * @author dominik.stadler at gmx.at
 */
object CreateAndDeleteBranch {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		// prepare test-repository
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				var call = git.branchList().call()
				for (ref in call) {
					println("Branch-Before: " + ref + " " + ref.name + " " + ref.objectId.name)
				}

				// make sure the branch is not there
				val refs = git.branchList().call()
				for (ref in refs) {
					println("Had branch: " + ref.name)
					if (ref.name == "refs/heads/testbranch") {
						println("Removing branch before")
						git.branchDelete()
								.setBranchNames("testbranch")
								.setForce(true)
								.call()
						break
					}
				}

				// run the add-call
				git.branchCreate()
						.setName("testbranch")
						.call()
				call = git.branchList().call()
				for (ref in call) {
					println("Branch-Created: " + ref + " " + ref.name + " " + ref.objectId.name)
				}

				// run the delete-call
				git.branchDelete()
						.setBranchNames("testbranch")
						.call()
				call = git.branchList().call()
				for (ref in call) {
					println("Branch-After: " + ref + " " + ref.name + " " + ref.objectId.name)
				}

				// run the add-call with a given starting point
				git.branchCreate()
						.setName("testbranch")
						.setStartPoint("d52a1031cd359a5941d0e047aa7ab82053f7f7c3")
						.call()
				call = git.branchList().call()
				for (ref in call) {
					println("Branch-Created with starting point: " + ref + " " + ref.name + " " + ref.objectId.name)
				}
			}
		}
	}
}