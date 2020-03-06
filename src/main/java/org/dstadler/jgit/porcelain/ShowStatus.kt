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
 * Simple snippet which prints the Status of a git repository, i.e. modified/added/
 * removed/ignored files, similar to "git status"
 *
 * @author dominik.stadler at gmx.at
 */
object ShowStatus {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				val status = git.status().call()
				println("Added: " + status.added)
				println("Changed: " + status.changed)
				println("Conflicting: " + status.conflicting)
				println("ConflictingStageState: " + status.conflictingStageState)
				println("IgnoredNotInIndex: " + status.ignoredNotInIndex)
				println("Missing: " + status.missing)
				println("Modified: " + status.modified)
				println("Removed: " + status.removed)
				println("Untracked: " + status.untracked)
				println("UntrackedFolders: " + status.untrackedFolders)
			}
		}
	}
}