package org.dstadler.jgit.api

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.revwalk.RevWalk
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
 * Simple snippet which shows how to use RevWalk to iterate over objects
 */
object WalkRev {

	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			val head = repository.exactRef("refs/heads/master")
			RevWalk(repository).use { walk ->
				val commit = walk.parseCommit(head.objectId)
				println("Start-Commit: $commit")
				println("Walking all commits starting at HEAD")
				walk.markStart(commit)
				var count = 0
				for (rev in walk) {
					println("Commit: $rev")
					count++
				}
				println(count)
				walk.dispose()
			}
		}
	}
}