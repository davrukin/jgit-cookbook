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
 * Simple snippet which shows how to use RevWalk to quickly iterate over all available commits,
 * not just the ones on the current branch
 */
object WalkAllCommits {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				// use the following instead to list commits on a specific branch
				//ObjectId branchId = repository.resolve("HEAD");
				//Iterable<RevCommit> commits = git.log().add(branchId).call();
				val commits = git.log().all().call()
				var count = 0
				for (commit in commits) {
					println("LogCommit: $commit")
					count++
				}
				println(count)
			}
		}
	}
}