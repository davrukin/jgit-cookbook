package org.dstadler.jgit.unfinished

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
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
 * Note: This snippet is not done and likely does not show anything useful yet
 *
 * @author dominik.stadler at gmx.at
 */
object ListRefLog {
	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				val refs = git.branchList().call()
				for (ref in refs) {
					println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
					listReflog(repository, ref)
				}
				val call = git.tagList().call()
				for (ref in call) {
					println("Tag: " + ref + " " + ref.name + " " + ref.objectId.name)
					listReflog(repository, ref)
				}
			}
		}
	}

	@Throws(GitAPIException::class)
	private fun listReflog(repository: Repository, ref: Ref) {
		/*
         * Ref head = repository.getRef(ref.getName());
         * RevWalk walk = new RevWalk(repository);
         * RevCommit commit = walk.parseCommit(head.getObjectId());
         */
		Git(repository).use { git ->
			val call = git.reflog().setRef(ref.name).call()
			for (reflog in call) {
				println("Reflog: $reflog")
			}
		}
	}
}