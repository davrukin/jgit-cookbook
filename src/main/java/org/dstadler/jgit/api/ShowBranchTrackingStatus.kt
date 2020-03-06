package org.dstadler.jgit.api

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Repository
import java.io.IOException
import java.util.*

/*
 * Copyright 2013, 2014 Dominik Stadler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * Snippet which shows how to use BranchTrackingStatus to print
 * how many commits away the local git repository is from the
 * remote branches.
 *
 * @author dominik.stadler at gmx.at
 */
object ShowBranchTrackingStatus {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				val call = git.branchList().call()
				for (ref in call) {
					val counts = getCounts(repository, ref.name)
					println("For branch: " + ref.name)
					println("Commits ahead : " + counts[0])
					println("Commits behind : " + counts[1])
					println()
				}
			}
		}
	}

	@Throws(IOException::class)
	private fun getCounts(repository: Repository, branchName: String): List<Int> {
		val trackingStatus = BranchTrackingStatus.of(repository, branchName)
		val counts: MutableList<Int> = ArrayList()
		if (trackingStatus != null) {
			counts.add(trackingStatus.aheadCount)
			counts.add(trackingStatus.behindCount)
		} else {
			println("Returned null, likely no remote tracking of branch $branchName")
			counts.add(0)
			counts.add(0)
		}
		return counts
	}
}