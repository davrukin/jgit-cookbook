package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.createNewRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
import java.io.File
import java.io.IOException
import java.util.*

/*
   Copyright 2017 Dominik Stadler

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
 * Snippet which shows how to merge changes from another branch.
 */
object MergeChanges {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val localPath = createNewRepository().use { repository ->
			val path = repository.workTree
			Git(repository).use { git ->
				// create some commit on master
				createCommit(repository, git, "masterFile", "content12")

				// create branch "changes"
				val changes = git.branchCreate().setName("changes").call()
				println("Result of creating the branch: $changes")

				// now start a change on master
				createCommit(repository, git, "sharedFile", "content12")

				// check out branch "changes"
				var checkout = git.checkout().setName("changes").call()
				println("Result of checking out the branch: $checkout")

				// create some commit on branch "changes", one of them conflicting with the change on master
				createCommit(repository, git, "branchFile", "content98")
				createCommit(repository, git, "sharedFile", "content98")

				// check out "master"
				checkout = git.checkout().setName("master").call()
				println("Result of checking out master: $checkout")

				// retrieve the objectId of the latest commit on branch
				val mergeBase = repository.resolve("changes")

				// perform the actual merge, here we disable FastForward to see the
				// actual merge-commit even though the merge is trivial
				val merge: MergeResult = git.merge().include(mergeBase).setCommit(true).setFastForward(MergeCommand.FastForwardMode.NO_FF).setMessage("Merged changes").call()
				println("Merge-Results for id: $mergeBase: $merge")
				for ((key, value) in merge.conflicts) {
					println("Key: $key")
					for (arr in value) {
						println("value: " + Arrays.toString(arr))
					}
				}
				path
			}
		}
		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}

	@Throws(IOException::class, GitAPIException::class)
	private fun createCommit(repository: Repository, git: Git, fileName: String, content: String) {
		// create the file
		val myFile = File(repository.directory.parent, fileName)
		FileUtils.writeStringToFile(myFile, content, "UTF-8")

		// run the add
		git.add()
				.addFilepattern(fileName)
				.call()

		// and then commit the changes
		val revCommit = git.commit()
				.setMessage("Added $fileName")
				.call()
		println("Committed file " + myFile + " as " + revCommit + " to repository at " + repository.directory)
	}
}