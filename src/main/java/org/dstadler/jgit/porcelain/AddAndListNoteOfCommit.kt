package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
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
 * Simple snippet which shows how to load Notes in a Git repository
 *
 * @author dominik.stadler at gmx.at
 */
object AddAndListNoteOfCommit {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			val head = repository.exactRef("refs/heads/master")
			println("Found head: $head")
			RevWalk(repository).use { walk ->
				val commit = walk.parseCommit(head.objectId)
				println("Found Commit: $commit")
				Git(repository).use { git ->
					git.notesAdd().setMessage("some note message").setObjectId(commit).call()
					println("Added Note to commit $commit")
					val call = git.notesList().call()
					println("Listing " + call.size + " notes")
					for (note in call) {
						// check if we found the note for this commit
						if (note.name != head.objectId.name) {
							println("Note $note did not match commit $head")
							continue
						}
						println("Found note: $note for commit $head")

						// displaying the contents of the note is done via a simple blob-read
						val loader = repository.open(note.data)
						loader.copyTo(System.out)
					}
				}
				walk.dispose()
			}
		}
	}
}