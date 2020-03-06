package org.dstadler.jgit.unfinished

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revplot.PlotWalk
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
 * Snippet which shows how to use PlotWalk to read from a specific commit.
 *
 * @author dominik.stadler at gmx.at
 */
object ListChildrenOfCommit {
	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			PlotWalk(repository).use { revWalk ->
				val rootId = repository.resolve("refs/heads/master")
				val root = revWalk.parseCommit(rootId)
				revWalk.markStart(root)
				val plotCommitList = PlotCommitList<PlotLane>()
				plotCommitList.source(revWalk)
				plotCommitList.fillTo(Int.MAX_VALUE)
				println("Printing children of commit $root")
				for (com in revWalk) {
					println("Child: $com")
				}
				println("Printing with next()")
				println("next: " + revWalk.next())
			}
		}
	}
}