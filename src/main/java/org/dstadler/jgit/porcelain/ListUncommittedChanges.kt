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
 * Simple snippet which shows how to list various types of uncommitted changes
 * of a Git repository
 *
 * @author dominik.stadler at gmx.at
 */
object ListUncommittedChanges {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			println("Listing uncommitted changes:")
			Git(repository).use { git ->
				val status = git.status().call()
				val conflicting = status.conflicting
				for (conflict in conflicting) {
					println("Conflicting: $conflict")
				}
				val added = status.added
				for (add in added) {
					println("Added: $add")
				}
				val changed = status.changed
				for (change in changed) {
					println("Change: $change")
				}
				val missing = status.missing
				for (miss in missing) {
					println("Missing: $miss")
				}
				val modified = status.modified
				for (modify in modified) {
					println("Modification: $modify")
				}
				val removed = status.removed
				for (remove in removed) {
					println("Removed: $remove")
				}
				val uncommittedChanges = status.uncommittedChanges
				for (uncommitted in uncommittedChanges) {
					println("Uncommitted: $uncommitted")
				}
				val untracked = status.untracked
				for (untrack in untracked) {
					println("Untracked: $untrack")
				}
				val untrackedFolders = status.untrackedFolders
				for (untrack in untrackedFolders) {
					println("Untracked Folder: $untrack")
				}
				val conflictingStageState = status.conflictingStageState
				for (entry in conflictingStageState.entries) {
					println("ConflictingState: $entry")
				}
			}
		}
	}
}