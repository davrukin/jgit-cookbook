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
 * Snippet which shows how to check if commits are merged into a
 * given branch.
 *
 * See also http://stackoverflow.com/questions/26644919/how-to-determine-with-jgit-which-branches-have-been-merged-to-master
 */
object CheckMergeStatusOfCommit {

	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			RevWalk(repository).use { revWalk ->
				val masterHead = revWalk.parseCommit(repository.resolve("refs/heads/master"))

				// first a commit that was merged
				var id = repository.resolve("05d18a76875716fbdbd2c200091b40caa06c713d")
				println("Had id: $id")
				var otherHead = revWalk.parseCommit(id)
				if (revWalk.isMergedInto(otherHead, masterHead)) {
					println("Commit $otherHead is merged into master")
				} else {
					println("Commit $otherHead is NOT merged into master")
				}


				// then a commit on a test-branch which is not merged
				id = repository.resolve("ae70dd60a7423eb07893d833600f096617f450d2")
				println("Had id: $id")
				otherHead = revWalk.parseCommit(id)
				if (revWalk.isMergedInto(otherHead, masterHead)) {
					println("Commit $otherHead is merged into master")
				} else {
					println("Commit $otherHead is NOT merged into master")
				}

				// and finally master-HEAD itself
				id = repository.resolve("HEAD")
				println("Had id: $id")
				otherHead = revWalk.parseCommit(id)
				if (revWalk.isMergedInto(otherHead, masterHead)) {
					println("Commit $otherHead is merged into master")
				} else {
					println("Commit $otherHead is NOT merged into master")
				}
				revWalk.dispose()
			}
		}
	}
}